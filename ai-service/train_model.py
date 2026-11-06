
import pandas as pd
import joblib

from sklearn.model_selection import StratifiedGroupKFold, cross_val_predict
from sklearn.pipeline import Pipeline
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.svm import LinearSVC

from sklearn.metrics import (
    accuracy_score,
    classification_report,
    confusion_matrix,
    f1_score
)


# Load dataset
df = pd.read_csv("data/priority_issues.csv")

df["priority"] = df["priority"].str.strip()
df["text"] = df["title"] + " " + df["description"]

X = df["text"]
y = df["priority"]
groups = df["group_id"]

labels = ["CRITICAL", "HIGH", "MEDIUM", "LOW"]


# Group-based cross-validation
cv = StratifiedGroupKFold(
    n_splits=5,
    shuffle=True,
    random_state=42
)


# Define algorithms
models = {
    "Logistic Regression": Pipeline([
        ("tfidf", TfidfVectorizer(
            lowercase=True,
            ngram_range=(1, 2)
        )),
        ("classifier", LogisticRegression(
            max_iter=1000
        ))
    ]),

    "Linear SVM": Pipeline([
        ("tfidf", TfidfVectorizer(
            lowercase=True,
            ngram_range=(1, 2)
        )),
        ("classifier", LinearSVC(
            C=1.0
        ))
    ])
}


results = {}

# Evaluate both models using out-of-fold predictions
for name, model in models.items():

    print("\n" + "=" * 50)
    print(name)
    print("=" * 50)

    predictions = cross_val_predict(
        model,
        X,
        y,
        groups=groups,
        cv=cv
    )

    accuracy = accuracy_score(y, predictions)
    macro_f1 = f1_score(
        y,
        predictions,
        average="macro"
    )

    results[name] = {
        "accuracy": accuracy,
        "macro_f1": macro_f1
    }

    print("Accuracy:", round(accuracy, 3))
    print("Macro F1:", round(macro_f1, 3))

    print("\nClassification Report:")
    print(classification_report(
        y,
        predictions,
        labels=labels,
        zero_division=0
    ))

    print("\nConfusion Matrix:")
    print(pd.DataFrame(
        confusion_matrix(y, predictions, labels=labels),
        index=[f"Actual {label}" for label in labels],
        columns=[f"Predicted {label}" for label in labels]
    ))


# Compare algorithms
print("\n" + "=" * 50)
print("MODEL COMPARISON")
print("=" * 50)

for name, metrics in results.items():
    print(
        f"{name}: "
        f"Accuracy={metrics['accuracy']:.3f}, "
        f"Macro F1={metrics['macro_f1']:.3f}"
    )


# Select model based on macro F1
best_model_name = max(
    results,
    key=lambda name: results[name]["macro_f1"]
)

best_model = models[best_model_name]

# Train selected model on the complete dataset
best_model.fit(X, y)

joblib.dump(
    best_model,
    "models/priority_model.joblib"
)

print("\nSelected model:", best_model_name)
print("Final model trained on all examples.")
print("Saved to models/priority_model.joblib")
