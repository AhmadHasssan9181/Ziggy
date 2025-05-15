"""
Search service implementation for real-time travel data using Gemini 2.0's Search tool.
"""
from google import genai
from google.genai import types
import os
import json
import asyncio
from typing import Dict, Any
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

# API key configuration - use the same one from your gemini_service
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")
if not GEMINI_API_KEY:
    raise ValueError("GEMINI_API_KEY environment variable is not set")


class SearchService:
    """Service class for getting grounded search results."""

    # Create a client instance
    _client = genai.Client(api_key=GEMINI_API_KEY)

    @classmethod
    async def _generate_grounded_content(cls, prompt: str) -> Dict[str, Any]:
        """
        Generate content grounded in Google Search results.
        """
        # Configure Google Search as a tool
        google_search_tool = types.Tool(
            google_search=types.GoogleSearch()
        )

        loop = asyncio.get_running_loop()

        def _sync_call():
            try:
                # Generate content with search grounding
                response = cls._client.models.generate_content(
                    model="gemini-2.0-flash",
                    contents=prompt,
                    config=types.GenerateContentConfig(
                        tools=[google_search_tool],
                        response_modalities=["TEXT"],
                    )
                )

                # Extract the text content
                result_text = ''
                for part in response.candidates[0].content.parts:
                    if hasattr(part, 'text'):
                        result_text += part.text

                # Extract search suggestions if available
                search_suggestions = []
                rendered_content = None

                if hasattr(response.candidates[0], 'grounding_metadata') and \
                        hasattr(response.candidates[0].grounding_metadata, 'search_entry_point') and \
                        hasattr(response.candidates[0].grounding_metadata.search_entry_point, 'rendered_content'):
                    rendered_content = response.candidates[0].grounding_metadata.search_entry_point.rendered_content

                if hasattr(response.candidates[0], 'grounding_metadata') and \
                        hasattr(response.candidates[0].grounding_metadata, 'web_search_queries'):
                    search_suggestions = response.candidates[0].grounding_metadata.web_search_queries

                return {
                    "text": result_text,
                    "search_suggestions": search_suggestions,
                    "rendered_content": rendered_content
                }
            except Exception as e:
                print(f"Error in Gemini Search API call: {str(e)}")
                raise

        # Execute in thread pool
        return await loop.run_in_executor(None, _sync_call)

    @staticmethod
    def _extract_json(text: str) -> Dict[str, Any]:
        """Extract JSON from text response with citation cleanup."""
        try:
            # Remove citation references like [9] from the text
            import re
            text = re.sub(r'\[\d+\]', '', text)

            # Try to find JSON in the cleaned text
            start = text.find('{')
            end = text.rfind('}') + 1

            if start >= 0 and end > start:
                json_str = text[start:end]
                return json.loads(json_str)

            # If no JSON found, try to parse the entire text
            return json.loads(text)
        except Exception as e:
            print(f"JSON extraction error: {str(e)}")
            # As a fallback, create a simple structure with the text
            return {"text": text, "error": f"Could not parse JSON: {str(e)}"}

    @classmethod
    async def get_weather(cls, location: str) -> Dict[str, Any]:
        """
        Get current weather information for a location using Gemini search.
        """
        prompt = f"""
        What is the current weather in {location}? 

        Please provide:
        1. Current temperature (in both Celsius and Fahrenheit)
        2. Weather conditions (sunny, cloudy, rainy, etc.)
        3. Humidity percentage
        4. Wind speed and direction
        5. Forecast for the next 24 hours

        Format as JSON with these exact fields:
        {{
            "location": string,
            "temperature": {{
                "celsius": number,
                "fahrenheit": number
            }},
            "condition": string,
            "humidity": number,
            "wind": {{
                "speed": number,
                "unit": "km/h",
                "direction": string
            }},
            "forecast": [
                {{
                    "time": string,
                    "condition": string,
                    "temperature": {{
                        "celsius": number,
                        "fahrenheit": number
                    }}
                }}
            ],
            "updated_at": string
        }}
        """

        try:
            # Get grounded response for weather
            response_data = await cls._generate_grounded_content(prompt)

            # Extract JSON from the text response
            result_json = cls._extract_json(response_data["text"])

            # Add search metadata
            result_json["search_metadata"] = {
                "search_suggestions": response_data.get("search_suggestions", []),
                "rendered_content": response_data.get("rendered_content", "")
            }

            return result_json
        except Exception as e:
            print(f"Weather search error: {str(e)}")
            # Return fallback response
            return {
                "location": location,
                "error": f"Could not retrieve weather data: {str(e)}",
                "search_metadata": {}
            }

    @classmethod
    async def search_hotel_prices(cls, location: str, check_in: str, check_out: str, guests: int = 2) -> Dict[str, Any]:
        """
        Search for hotel prices using Gemini's grounded search capabilities.
        """
        prompt = f"""
        Based on the most recent information available, provide details about hotel prices in {location} for the following dates:
        - Check-in: {check_in}
        - Check-out: {check_out}
        - Number of guests: {guests}

        Include a range of hotels (budget, mid-range, and luxury) with these details:
        1. Hotel name
        2. Price range per night in USD
        3. Total price for the stay
        4. Star rating
        5. Key amenities
        6. Brief description of the location/area

        Format the response as JSON with the following structure:
        {{
            "search_info": {{
                "location": string,
                "check_in": string,
                "check_out": string,
                "guests": int,
                "search_date": string (current date)
            }},
            "hotels": [
                {{
                    "name": string,
                    "price_per_night": float,
                    "total_price": float,
                    "star_rating": float,
                    "location": string,
                    "amenities": [string, string, ...],
                    "description": string
                }}
            ],
            "price_range": {{
                "budget": string (e.g., "$50-100"),
                "mid_range": string (e.g., "$100-200"),
                "luxury": string (e.g., "$200+")
            }}
        }}
        """

        try:
            # Get grounded response
            response_data = await cls._generate_grounded_content(prompt)

            # Extract JSON from the text response
            result_json = cls._extract_json(response_data["text"])

            # Add search metadata
            result_json["search_metadata"] = {
                "search_suggestions": response_data.get("search_suggestions", []),
                "rendered_content": response_data.get("rendered_content", "")
            }

            return result_json
        except Exception as e:
            print(f"Hotel search error: {str(e)}")
            # Return fallback response
            return {
                "search_info": {
                    "location": location,
                    "check_in": check_in,
                    "check_out": check_out,
                    "guests": guests,
                    "search_date": "Error fetching data"
                },
                "hotels": [],
                "error": str(e)
            }