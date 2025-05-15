from fastapi import APIRouter, HTTPException
from ..models import TravelAdviceRequest, TravelAdviceResponse
from ..services.gemini_service import GeminiService

router = APIRouter(prefix="/travel", tags=["Travel Advice"])

@router.post("/advice", response_model=TravelAdviceResponse)
async def generate_travel_advice(request: TravelAdviceRequest):
    """
    Generate travel advice including weather information, local customs,
    safety tips, packing suggestions, and more.
    """
    try:
        result = await GeminiService.generate_travel_advice(request.model_dump())
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to generate travel advice: {str(e)}")