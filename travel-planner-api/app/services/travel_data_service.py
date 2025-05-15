"""
Comprehensive travel data service that combines Gemini search and free APIs.
"""
from .search_service import SearchService
from .external_apis import ExternalAPIService
from typing import Dict, Any
import asyncio
import datetime


class TravelDataService:
    """
    Service that combines multiple data sources for comprehensive travel information.
    """

    @classmethod
    async def get_destination_info(cls, location: str) -> Dict[str, Any]:
        """
        Get comprehensive destination information by combining multiple data sources.
        """
        # Step 1: Get coordinates for the location
        location_data = await ExternalAPIService.fetch_place_coordinates(location)

        # Step 2: Prepare tasks based on whether we have coordinates
        tasks = []

        # Add exchange rates task (not location-dependent)
        exchange_task = ExternalAPIService.fetch_exchange_rates()
        tasks.append(exchange_task)

        # If we have coordinates, add attractions task
        attractions_task = None
        if "error" not in location_data and "lat" in location_data and "lon" in location_data:
            attractions_task = ExternalAPIService.fetch_attractions(
                latitude=location_data["lat"],
                longitude=location_data["lon"],
                radius=10000,
                kinds="interesting_places,cultural,historical,natural,architecture"
            )
            tasks.append(attractions_task)

        # Add weather task
        weather_task = SearchService.get_weather(location)
        tasks.append(weather_task)

        # Gemini search query for destination information
        gemini_prompt = f"""
        Provide comprehensive destination information about {location} including:
        1. Brief overview and history
        2. Best time to visit (peak, shoulder, and off-seasons)
        3. Local customs and etiquette
        4. Transportation options
        5. Safety information
        6. Must-see attractions
        7. Food and dining recommendations

        Format as JSON with these sections:
        {{
            "overview": string,
            "best_time_to_visit": {{
                "peak_season": string,
                "shoulder_season": string,
                "off_season": string,
                "current_season": string
            }},
            "local_customs": [string, string, ...],
            "transportation": {{
                "from_airport": string,
                "within_city": string,
                "regional": string
            }},
            "safety_tips": [string, string, ...],
            "must_see": [string, string, ...],
            "food_recommendations": [
                {{
                    "name": string,
                    "cuisine": string,
                    "price_range": string
                }}
            ]
        }}
        """

        gemini_task = SearchService._generate_grounded_content(gemini_prompt)
        tasks.append(gemini_task)

        # Await all results
        results = await asyncio.gather(*tasks)

        # Extract results based on task order
        exchange_data = results[0]

        # Initialize variables
        attractions_data = {"features": []}
        destination_info = {}
        weather_data = {}

        # Process results
        task_index = 1

        if attractions_task:
            attractions_data = results[task_index]
            task_index += 1

        weather_data = results[task_index]
        task_index += 1

        gemini_response = results[task_index]

        # Process Gemini response
        try:
            destination_info = SearchService._extract_json(gemini_response["text"])
            # Add search suggestions and rendered content
            destination_info["search_metadata"] = {
                "search_suggestions": gemini_response.get("search_suggestions", []),
                "rendered_content": gemini_response.get("rendered_content", "")
            }
        except Exception as e:
            destination_info = {"error": f"Failed to parse destination info: {str(e)}"}

        # Process attractions data - get details for top attractions if available
        top_attractions = []
        if "features" in attractions_data and attractions_data["features"]:
            # Take top 5 attractions
            top_5_attractions = attractions_data["features"][:5]

            # Fetch details for each attraction
            attraction_details_tasks = []
            for attraction in top_5_attractions:
                if "xid" in attraction:
                    attraction_details_tasks.append(
                        ExternalAPIService.fetch_place_details(attraction["xid"])
                    )

            if attraction_details_tasks:
                attraction_details = await asyncio.gather(*attraction_details_tasks)
                top_attractions = attraction_details

        # Combine all data
        return {
            "destination": location,
            "timestamp": datetime.datetime.now().isoformat(),
            "coordinates": {
                "latitude": location_data.get("lat"),
                "longitude": location_data.get("lon")
            } if "lat" in location_data and "lon" in location_data else None,
            "destination_info": destination_info,
            "weather": weather_data,
            "exchange_rates": exchange_data.get("conversion_rates", {}),
            "attractions": top_attractions
        }

    @classmethod
    async def get_hotel_prices(cls, location: str, check_in: str, check_out: str, guests: int = 2) -> Dict[str, Any]:
        """
        Get hotel prices using primarily Gemini search with fallback to other data.
        """
        # Use the SearchService for hotel prices
        hotel_data = await SearchService.search_hotel_prices(
            location=location,
            check_in=check_in,
            check_out=check_out,
            guests=guests
        )

        # Add exchange rate data
        exchange_rates = await ExternalAPIService.fetch_exchange_rates()
        if "error" not in exchange_rates and "conversion_rates" in exchange_rates:
            hotel_data["exchange_rates"] = exchange_rates.get("conversion_rates", {})

        # Get location coordinates
        location_data = await ExternalAPIService.fetch_place_coordinates(location)
        if "error" not in location_data and "lat" in location_data and "lon" in location_data:
            hotel_data["coordinates"] = {
                "latitude": location_data.get("lat"),
                "longitude": location_data.get("lon")
            }

        return hotel_data