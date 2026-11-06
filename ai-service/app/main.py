
from fastapi import FastAPI

from app.schemas import (
    IssueRequest,
    PriorityPredictionResponse,
    DuplicateDetectionRequest,
    DuplicateDetectionResponse
)

from app.model.priority_model import (
    predict_priority,
    get_model_info
)

from app.model.duplicate_detector import detect_duplicates


app = FastAPI(
    title="IssuePilot AI Service",
    description="ML-powered issue intelligence service",
    version="1.1.0"
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


@app.post(
    "/api/ai/detect-duplicates",
    response_model=DuplicateDetectionResponse
)
def detect_issue_duplicates(request: DuplicateDetectionRequest):

    result = detect_duplicates(
        title=request.title,
        description=request.description,
        existing_issues=request.existingIssues,
        threshold=request.threshold,
        max_results=request.maxResults
    )

    return result
