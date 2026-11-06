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