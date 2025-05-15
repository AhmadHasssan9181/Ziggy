"""
Routes for travel search features with Google Search suggestions integration.
"""
from fastapi import APIRouter, HTTPException, Query
from ..models import (
    HotelSearchRequest, HotelSearchResponse,
    DestinationRequest, WeatherResponse
)
from typing import Dict, Any
from ..services.travel_data_service import TravelDataService
from ..services.search_service import SearchService
from ..services.external_apis import ExternalAPIService

router = APIRouter(prefix="/search", tags=["Search"])


@router.post("/hotels", response_model=HotelSearchResponse)
async def search_hotels(request: HotelSearchRequest):
    """
    Search for hotels with real-time pricing based on location and dates.
    """
    try:
        result = await TravelDataService.get_hotel_prices(
            location=request.location,
            check_in=request.check_in_date,
            check_out=request.check_out_date,
            guests=request.guests
        )
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to search hotels: {str(e)}")


@router.post("/destination")
async def get_destination_info(request: DestinationRequest):
    """
    Get comprehensive destination information including weather, attractions, and more.
    """
    try:
        result = await TravelDataService.get_destination_info(location=request.location)
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to get destination info: {str(e)}")


@router.get("/weather/{location}", response_model=WeatherResponse)
async def get_weather(location: str):
    """
    Get current weather information for a location.
    """
    try:
        weather_data = await SearchService.get_weather(location)
        return weather_data
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to get weather: {str(e)}")


@router.get("/attractions/{location}")
async def get_attractions(
        location: str,
        radius: int = Query(10000, ge=1000, le=50000, description="Radius in meters"),
        limit: int = Query(20, ge=1, le=50, description="Maximum number of attractions to return")
):
    """
    Get attractions near a location using OpenTripMap API.
    """
    try:
        # First get coordinates for the location
        location_data = await ExternalAPIService.fetch_place_coordinates(location)

        if "error" in location_data:
            raise HTTPException(status_code=404, detail=f"Location not found: {location}")

        # Then fetch attractions at those coordinates
        attractions = await ExternalAPIService.fetch_attractions(
            latitude=location_data["lat"],
            longitude=location_data["lon"],
            radius=radius,
            limit=limit
        )

        return attractions
    except Exception as e:
        if isinstance(e, HTTPException):
            raise e
        raise HTTPException(status_code=500, detail=f"Failed to get attractions: {str(e)}")



@router.get("/exchange-rates")
async def get_exchange_rates(base_currency: str = "USD"):
    """
    Get current exchange rates.
    """
    try:
        exchange_rates = await ExternalAPIService.fetch_exchange_rates(base_currency)
        return exchange_rates
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to get exchange rates: {str(e)}")