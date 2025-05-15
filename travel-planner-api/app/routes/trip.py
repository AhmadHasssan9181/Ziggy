from fastapi import APIRouter, HTTPException
from ..models import TripPlanRequest, TripPlanResponse
from ..services.gemini_service import GeminiService

router = APIRouter(prefix="/trip", tags=["Trip Planning"])

@router.post("/plan", response_model=TripPlanResponse)
async def generate_trip_plan(request: TripPlanRequest):
    """
    Generate a comprehensive trip plan including daily itinerary, recommended places,
    and transportation options.
    """
    try:
        result = await GeminiService.generate_trip_plan(request.model_dump())
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to generate trip plan: {str(e)}")