from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any


class BudgetRequest(BaseModel):
    destination: str = Field(..., description="Travel destination")
    duration_days: int = Field(..., description="Trip duration in days")
    travelers: int = Field(..., description="Number of travelers")
    budget_level: str = Field(..., description="Budget level (economy, mid-range, luxury)")
    include_flight: bool = Field(True, description="Whether to include flight in budget")
    include_accommodation: bool = Field(True, description="Whether to include accommodation in budget")
    include_activities: bool = Field(True, description="Whether to include activities in budget")
    include_food: bool = Field(True, description="Whether to include food in budget")
    origin: Optional[str] = Field(None, description="Origin location for flight calculation")
    currency: str = Field("USD", description="Currency for budget")


class BudgetResponse(BaseModel):
    total_budget: float
    breakdown: Dict[str, float]
    tips: List[str]
    currency: str


class TripPlanRequest(BaseModel):
    destination: str = Field(..., description="Travel destination")
    duration_days: int = Field(..., description="Trip duration in days")
    interests: List[str] = Field(..., description="List of traveler interests")
    travel_style: str = Field(..., description="Travel style (relaxed, adventurous, cultural, etc.)")
    travel_dates: Optional[str] = Field(None, description="Approximate travel dates or season")
    include_accommodation: bool = Field(True, description="Include accommodation recommendations")
    include_transportation: bool = Field(True, description="Include transportation recommendations")


class DailyItinerary(BaseModel):
    day: int
    activities: List[Dict[str, str]]  # time, activity, description, location
    meals: List[Dict[str, str]]  # meal, recommendation, price_range


class TripPlanResponse(BaseModel):
    destination_overview: str
    daily_itinerary: List[DailyItinerary]
    recommended_places: List[Dict[str, str]]
    transportation_tips: List[str]
    accommodation_suggestions: Optional[List[Dict[str, str]]] = None


class TravelAdviceRequest(BaseModel):
    destination: str = Field(..., description="Travel destination")
    travel_dates: Optional[str] = Field(None, description="Approximate travel dates or season")
    duration_days: Optional[int] = Field(None, description="Trip duration in days")
    specific_questions: Optional[List[str]] = Field(None, description="Specific travel questions")


class TravelAdviceResponse(BaseModel):
    weather_info: str
    local_customs: List[str]
    safety_tips: List[str]
    packing_suggestions: List[str]
    visa_requirements: str
    currency_info: str
    health_recommendations: List[str]
    answers_to_questions: Optional[Dict[str, str]] = None

# Add these search-related models to your existing models.py

class HotelSearchRequest(BaseModel):
    location: str
    check_in_date: str
    check_out_date: str
    guests: int = Field(default=2, ge=1, le=10)

class Hotel(BaseModel):
    name: str
    price_per_night: float
    total_price: float
    star_rating: float
    location: str
    amenities: List[str]
    description: str

class PriceRange(BaseModel):
    budget: str
    mid_range: str
    luxury: str

class SearchInfo(BaseModel):
    location: str
    check_in: str
    check_out: str
    guests: int
    search_date: str

class SearchMetadata(BaseModel):
    search_suggestions: List[str] = []
    rendered_content: Optional[str] = None

class HotelSearchResponse(BaseModel):
    search_info: SearchInfo
    hotels: List[Hotel]
    price_range: Optional[PriceRange] = None
    search_metadata: Optional[SearchMetadata] = None
    exchange_rates: Optional[Dict[str, float]] = None
    coordinates: Optional[Dict[str, float]] = None

class DestinationRequest(BaseModel):
    location: str

class WeatherResponse(BaseModel):
    location: str
    temperature: Dict[str, float]
    condition: str
    humidity: float
    wind: Dict[str, Any]
    forecast: List[Dict[str, Any]]
    updated_at: str
    search_metadata: Optional[SearchMetadata] = None