from fastapi import FastAPI
app=FastAPI(
    title="IssuePilot AI Service",
    version="1.0.0"
)

@app.get("/health")
def health_check():
    return{
        "status":"ok",
        "service":"issuepilot-ai"
    }