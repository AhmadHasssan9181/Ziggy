from fastapi import APIRouter, HTTPException
from ..models import BudgetRequest, BudgetResponse
from ..services.gemini_service import GeminiService

router = APIRouter(prefix="/budget", tags=["Budget"])

@router.post("/generate", response_model=BudgetResponse)
async def generate_budget(request: BudgetRequest):
    """
    Generate a travel budget based on destination, duration, and number of travelers.
    """
    try:
        result = await GeminiService.generate_budget(request.model_dump())
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to generate budget: {str(e)}")