"""
External API service for fetching travel data from free sources.
"""
import httpx
import asyncio
import json
from typing import Dict, Any, List, Optional
import os
from dotenv import load_dotenv
import logging

# Set up logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Load environment variables
load_dotenv()

class ExternalAPIService:
    """Service for integrating with external free travel APIs."""

    # Directly use your API key as a fallback if the environment variable isn't working
    _OPEN_TRIP_MAP_KEY = os.getenv("OPEN_TRIP_MAP_KEY", "5ae2e3f221c38a28845f05b6723e0e6a3719287f588c65f538cfdc87")
    _EXCHANGE_RATE_KEY = os.getenv("EXCHANGE_RATE_KEY", "f1105d3903531011886312f3")

    # Debug output to verify the key is loaded
    @classmethod
    def print_api_key_status(cls):
        """Print API key status for debugging."""
        logger.info(f"OPEN_TRIP_MAP_KEY length: {len(cls._OPEN_TRIP_MAP_KEY) if cls._OPEN_TRIP_MAP_KEY else 0}")
        logger.info(f"Using hardcoded key as fallback: {'Yes' if '5ae2e3f2' in cls._OPEN_TRIP_MAP_KEY else 'No'}")

    # Hard-coded city coordinates as fallback
    _CITY_COORDINATES = {
        "islamabad": {"lat": 33.6844, "lon": 73.0479},
        "lahore": {"lat": 31.5497, "lon": 74.3436},
        "karachi": {"lat": 24.8607, "lon": 67.0011},
        "tokyo": {"lat": 35.6762, "lon": 139.6503},
        "new york": {"lat": 40.7128, "lon": -74.0060},
        "london": {"lat": 51.5074, "lon": -0.1278},
        "paris": {"lat": 48.8566, "lon": 2.3522},
        "dubai": {"lat": 25.2048, "lon": 55.2708}
    }

    @classmethod
    async def fetch_place_coordinates(cls, place_name: str, country_code: Optional[str] = None) -> Dict[str, Any]:
        """
        Fetch geographic coordinates for a place name using OpenTripMap's geoname endpoint.
        """
        # Print API key status
        cls.print_api_key_status()

        # Check for known coordinates first
        normalized_name = place_name.lower().strip()
        if normalized_name in cls._CITY_COORDINATES:
            logger.info(f"Using hardcoded coordinates for {place_name}")
            return cls._CITY_COORDINATES[normalized_name]

        # Otherwise proceed with API call
        base_url = "https://api.opentripmap.com/0.1/en/places/geoname"

        # Check for API key
        if not cls._OPEN_TRIP_MAP_KEY:
            logger.error("Cannot fetch coordinates: OpenTripMap API key is not set")
            # For Islamabad, use hardcoded coordinates even if not in the exact normalized form
            if "islamabad" in normalized_name:
                return cls._CITY_COORDINATES["islamabad"]
            return {"error": "API key not configured", "details": "OPEN_TRIP_MAP_KEY environment variable is not set"}

        params = {
            "name": place_name,
            "apikey": cls._OPEN_TRIP_MAP_KEY
        }

        if country_code:
            params["country"] = country_code

        try:
            async with httpx.AsyncClient() as client:
                logger.info(f"Fetching coordinates for {place_name} with API key: {cls._OPEN_TRIP_MAP_KEY[:5]}...")
                response = await client.get(base_url, params=params)
                response.raise_for_status()
                data = response.json()
                logger.info(f"Successfully got coordinates for {place_name}: {data.get('lat')}, {data.get('lon')}")
                return data
        except Exception as e:
            logger.error(f"Error fetching place coordinates: {str(e)}")

            # For known cities, use hardcoded coordinates as fallback
            for city, coords in cls._CITY_COORDINATES.items():
                if city in normalized_name:
                    logger.info(f"Using fallback coordinates for {city} in {place_name}")
                    return coords

            # Provide more detailed error information
            return {
                "error": str(e),
                "api_url": base_url,
                "place_name": place_name,
                "api_key_status": f"First 5 chars: {cls._OPEN_TRIP_MAP_KEY[:5]}..." if cls._OPEN_TRIP_MAP_KEY else "Missing"
            }

    @classmethod
    async def fetch_attractions(cls, latitude: float, longitude: float, radius: int = 10000,
                             limit: int = 20, kinds: Optional[str] = None) -> Dict[str, Any]:
        """
        Fetch attractions near a location using OpenTripMap's radius endpoint.
        """
        base_url = "https://api.opentripmap.com/0.1/en/places/radius"

        # Print API key status
        cls.print_api_key_status()

        # Check for API key
        if not cls._OPEN_TRIP_MAP_KEY:
            logger.error("Cannot fetch attractions: OpenTripMap API key is not set")
            return {"error": "API key not configured", "features": []}

        params = {
            "lat": latitude,
            "lon": longitude,
            "radius": radius,
            "limit": limit,
            "format": "json",
            "apikey": cls._OPEN_TRIP_MAP_KEY
        }

        if kinds:
            params["kinds"] = kinds

        try:
            async with httpx.AsyncClient() as client:
                logger.info(f"Fetching attractions at {latitude}, {longitude} with radius {radius}m")
                # Include part of the API key in the log for debugging
                logger.info(f"Using API key starting with: {cls._OPEN_TRIP_MAP_KEY[:5]}...")

                response = await client.get(base_url, params=params)
                response.raise_for_status()
                data = response.json()

                # Check if we got a valid response
                if isinstance(data, list):
                    logger.info(f"Successfully fetched {len(data)} attractions")
                    return {"features": data}
                else:
                    logger.warning(f"Unexpected response format: {data}")
                    return {"error": "Unexpected response format", "features": []}

        except Exception as e:
            logger.error(f"Error fetching attractions: {str(e)}")
            return {
                "error": str(e),
                "features": [],
                "api_key_status": f"First 5 chars: {cls._OPEN_TRIP_MAP_KEY[:5]}..." if cls._OPEN_TRIP_MAP_KEY else "Missing"
            }

    @classmethod
    async def fetch_place_details(cls, xid: str) -> Dict[str, Any]:
        """
        Fetch detailed information about a specific place by its XID.
        """
        base_url = f"https://api.opentripmap.com/0.1/en/places/xid/{xid}"

        # Check for API key
        if not cls._OPEN_TRIP_MAP_KEY:
            logger.error("Cannot fetch place details: OpenTripMap API key is not set")
            return {"error": "API key not configured"}

        params = {
            "apikey": cls._OPEN_TRIP_MAP_KEY
        }

        try:
            async with httpx.AsyncClient() as client:
                logger.info(f"Fetching place details for XID: {xid} using API key starting with {cls._OPEN_TRIP_MAP_KEY[:5]}...")
                response = await client.get(base_url, params=params)
                response.raise_for_status()
                return response.json()
        except Exception as e:
            logger.error(f"Error fetching place details: {str(e)}")
            return {"error": str(e)}

    @classmethod
    async def fetch_exchange_rates(cls, base_currency: str = "USD") -> Dict[str, Any]:
        """
        Fetch current exchange rates using ExchangeRate-API.
        """
        base_url = f"https://v6.exchangerate-api.com/v6/{cls._EXCHANGE_RATE_KEY}/latest/{base_currency}"

        try:
            async with httpx.AsyncClient(timeout=15.0) as client:  # Increased timeout
                logger.info(f"Fetching exchange rates for {base_currency}")
                response = await client.get(base_url)
                response.raise_for_status()
                data = response.json()

                # Check if the response contains expected fields
                if "result" in data and data["result"] == "success":
                    logger.info(f"Successfully fetched exchange rates for {base_currency}")
                    return data
                else:
                    logger.warning(f"Exchange rate API returned unexpected data: {data}")
                    return {
                        "error": "Invalid response from exchange rate API",
                        "response_data": data
                    }
        except Exception as e:
            logger.error(f"Error fetching exchange rates: {str(e)}")
            return {
                "error": str(e),
                "api_url": base_url,
                "base_currency": base_currency
            }