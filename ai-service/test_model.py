
import joblib
import pandas as pd

model = joblib.load("models/priority_model.joblib")

test_issues = [
    {
        "title": "All users are unable to access the platform",
        "description": "The production application is completely unavailable.",
        "expected": "CRITICAL"
    },
    {
        "title": "Some customers cannot complete payments",
        "description": "Payment requests are failing intermittently for a subset of customers.",
        "expected": "HIGH"
    },
    {
        "title": "Search results take too long",
        "description": "The search feature works but response times have increased.",
        "expected": "MEDIUM"
    },
    {
        "title": "Add export button to dashboard",
        "description": "Allow users to export dashboard information as a CSV file.",
        "expected": "LOW"
    },
    {
        "title": "Production database completely inaccessible",
        "description": "All application requests are failing because the database is unavailable.",
        "expected": "CRITICAL"
    },
    {
        "title": "Minor profile alignment problem",
        "description": "The profile page has a small visual alignment issue.",
        "expected": "LOW"
    },
    {
        "title": "Users intermittently receive server errors",
        "description": "An important feature fails for several users but the rest of the platform works.",
        "expected": "HIGH"
    },
    {
        "title": "Dashboard loading performance degraded",
        "description": "The dashboard loads slowly but eventually displays all information.",
        "expected": "MEDIUM"
    }
]

results = []

for issue in test_issues:

    text = issue["title"] + " " + issue["description"]

    prediction = model.predict([text])[0]

    probabilities = model.predict_proba([text])[0]
    confidence = float(max(probabilities))

    results.append({
        "Title": issue["title"],
        "Expected": issue["expected"],
        "Predicted": prediction,
        "Confidence": round(confidence, 3),
        "Correct": prediction == issue["expected"]
    })

df = pd.DataFrame(results)

print("\nNEW ISSUE TEST RESULTS\n")
print(df.to_string(index=False))

print("\nCorrect predictions:",
      df["Correct"].sum(), "/", len(df))

print("Accuracy on these examples:",
      round(df["Correct"].mean() * 100, 2), "%")
