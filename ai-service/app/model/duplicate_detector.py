
from pathlib import Path

from sentence_transformers import SentenceTransformer, util


MODEL_NAME = "sentence-transformers/all-MiniLM-L6-v2"

# Load the model only once, when first needed.
_model = None


def get_embedding_model():
    global _model

    if _model is None:
        print(f"Loading duplicate detection model: {MODEL_NAME}")
        _model = SentenceTransformer(MODEL_NAME)
        print("Duplicate detection model loaded successfully.")

    return _model


def prepare_issue_text(title: str, description: str = "") -> str:
    """Combine issue title and description for semantic comparison."""

    title = title.strip()
    description = description.strip()

    if description:
        return f"{title}. {description}"

    return title


def detect_duplicates(
        title: str,
        description: str,
        existing_issues: list,
        threshold: float = 0.75,
        max_results: int = 5
) -> dict:
    """
    Find semantically similar issues using sentence embeddings
    and cosine similarity.
    """

    if not existing_issues:
        return {
            "matches": [],
            "threshold": threshold,
            "model": MODEL_NAME
        }

    model = get_embedding_model()

    new_issue_text = prepare_issue_text(title, description)

    existing_texts = [
        prepare_issue_text(issue.title, issue.description or "")
        for issue in existing_issues
    ]

    # Generate normalized sentence embeddings.
    new_embedding = model.encode(
        new_issue_text,
        convert_to_tensor=True,
        normalize_embeddings=True
    )

    existing_embeddings = model.encode(
        existing_texts,
        convert_to_tensor=True,
        normalize_embeddings=True
    )

    # For normalized embeddings, dot product equals cosine similarity.
    similarities = util.dot_score(
        new_embedding,
        existing_embeddings
    )[0]

    matches = []

    for issue, score in zip(existing_issues, similarities):
        similarity = float(score.item())

        if similarity >= threshold:
            matches.append({
                "issueId": issue.id,
                "title": issue.title,
                "description": issue.description or "",
                "similarity": round(similarity, 4)
            })

    matches.sort(
        key=lambda item: item["similarity"],
        reverse=True
    )

    return {
        "matches": matches[:max_results],
        "threshold": threshold,
        "model": MODEL_NAME
    }
