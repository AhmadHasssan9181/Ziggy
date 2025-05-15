from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.routes import budget_router, trip_router, travel_router, search_router, chatbot_router, youtube_router
import os
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

# Check for API keys
if not os.getenv("GEMINI_API_KEY"):
    print("WARNING: GEMINI_API_KEY environment variable is not set. API calls will fail.")

if not os.getenv("OPEN_TRIP_MAP_KEY"):
    print("WARNING: OPEN_TRIP_MAP_KEY environment variable is not set. Some search features will fail.")

if not os.getenv("GOOGLE_API_KEY"):
    print("WARNING: GOOGLE_API_KEY environment variable is not set. Chatbot features will fail.")

if not os.getenv("YOUTUBE_API_KEY"):
    print("WARNING: YOUTUBE_API_KEY environment variable is not set. YouTube search will fail.")

app = FastAPI(
    title="Travel Planner API",
    description="API for planning trips, generating travel budgets, and providing travel advice using Gemini AI",
    version="1.1.0",
)

# Configure CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Allows all origins
    allow_credentials=True,
    allow_methods=["*"],  # Allows all methods
    allow_headers=["*"],  # Allows all headers
)

# Include routers
app.include_router(budget_router)
app.include_router(trip_router)
app.include_router(travel_router)
app.include_router(search_router)
app.include_router(chatbot_router)  # Add the new chatbot router
app.include_router(youtube_router)  # Add the new youtube router

@app.get("/", tags=["Root"])
async def read_root():
    """Root endpoint to check if the API is running."""
    return {
        "message": "Welcome to the Travel Planner API",
        "docs": "/docs",
        "endpoints": [
            {"name": "Generate Budget", "path": "/budget/generate"},
            {"name": "Generate Trip Plan", "path": "/trip/plan"},
            {"name": "Generate Travel Advice", "path": "/travel/advice"},
            {"name": "Search Hotels", "path": "/search/hotels"},
            {"name": "Get Destination Info", "path": "/search/destination"},
            {"name": "Get Weather", "path": "/search/weather/{location}"},
            {"name": "Get Attractions", "path": "/search/attractions/{location}"},
            {"name": "Get Exchange Rates", "path": "/search/exchange-rates"},
            {"name": "Chat with Travel Guide", "path": "/chatbot/chat"},
            {"name": "YouTube Search", "path": "/youtube/search"}
        ]
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=True)