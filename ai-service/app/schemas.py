from pydantic import BaseModel, Field, field_validator



class IssueRequest(BaseModel):
    title: str = Field(min_length=3, max_length=200)
    description: str = Field(default="", max_length=5000)

    @field_validator("title")
    @classmethod
    def validate_title(cls, value):
        value = value.strip()

        if not value:
            raise ValueError("Title cannot be empty")

        return value

    @field_validator("description")
    @classmethod
    def validate_description(cls, value):
        return value.strip()



class PriorityPredictionResponse(BaseModel):
    predictedPriority: str
    confidence: float
    model: str


class DuplicateIssueCandidate(BaseModel):
    id: int
    title: str = Field(min_length=3, max_length=255)
    description: str = Field(default="", max_length=5000)


class DuplicateDetectionRequest(BaseModel):
    title: str = Field(min_length=3, max_length=200)
    description: str = Field(default="", max_length=5000)
    existingIssues: list[DuplicateIssueCandidate] = Field(
        default_factory=list,
        max_length=200
    )
    threshold: float = Field(default=0.75, ge=0.0, le=1.0)
    maxResults: int = Field(default=5, ge=1, le=20)


class DuplicateIssueMatch(BaseModel):
    issueId: int
    title: str
    description: str
    similarity: float


class DuplicateDetectionResponse(BaseModel):
    matches: list[DuplicateIssueMatch]
    threshold: float
    model: str
