from fastapi import FastAPI

from app.schemas import (
    IssueRequest,
    PriorityPredictionResponse
)

from app.model.priority_model import (
    predict_priority,
    get_model_info
)


app = FastAPI(
    title="IssuePilot AI Service",
    description="ML-powered issue priority prediction service",
    version="1.0.0"
)


@app.get("/health")
def health_check():

    return {
        "status": "ok",
        "service": "issuepilot-ai"
    }


@app.get("/api/ai/model-info")
def model_info():

    return get_model_info()


@app.post(
    "/api/ai/predict-priority",
    response_model=PriorityPredictionResponse
)
def predict_issue_priority(issue: IssueRequest):

    result = predict_priority(
        issue.title,
        issue.description
    )

    return result