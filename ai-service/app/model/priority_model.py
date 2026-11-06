import joblib
from pathlib import Path


# Resolve model path independently of the working directory
BASE_DIR = Path(__file__).resolve().parents[2]

MODEL_PATH = BASE_DIR / "models" / "priority_model.joblib"

MODEL_NAME = "tfidf-logistic-regression"

# Load model once when the service starts
model = joblib.load(MODEL_PATH)


def predict_priority(title: str, description: str):

    text = f"{title} {description}"

    prediction = model.predict([text])[0]

    probabilities = model.predict_proba([text])[0]

    confidence = float(max(probabilities))

    return {
        "predictedPriority": prediction,
        "confidence": round(confidence, 4),
        "model": MODEL_NAME
    }


def get_model_info():

    return {
        "model": MODEL_NAME,
        "algorithm": "Logistic Regression",
        "vectorizer": "TF-IDF",
        "classes": list(model.classes_)
    }