from google import genai
import os
from typing import Dict, List, Any
import json
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

# Get the API key from environment
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")
if not GEMINI_API_KEY:
    raise ValueError("GEMINI_API_KEY environment variable is not set")

# Create the Gemini client
client = genai.Client(api_key=GEMINI_API_KEY)

# Use gemini-2.0-flash as specified
MODEL_NAME = "gemini-2.0-flash"


class GeminiService:
    @staticmethod
    async def generate_budget(request_data: Dict) -> Dict:
        """Generate a travel budget using Gemini AI."""
        prompt = f"""
        Create a detailed travel budget for a trip to {request_data['destination']} for 
        {request_data['travelers']} traveler(s) for {request_data['duration_days']} days.
        Budget level: {request_data['budget_level']}

        Include budget for:
        {f"- Flights from {request_data['origin']}" if request_data.get('origin') and request_data['include_flight'] else ""}
        {f"- Accommodation" if request_data['include_accommodation'] else ""}
        {f"- Food and drinks" if request_data['include_food'] else ""}
        {f"- Activities and sightseeing" if request_data['include_activities'] else ""}
        - Local transportation
        - Miscellaneous expenses

        Provide a breakdown of costs in {request_data['currency']}, including:
        1. Total budget
        2. Daily cost per person
        3. Detailed breakdown by category
        4. Money-saving tips for this destination

        Format the response as JSON with the following structure:
        {{
            "total_budget": float,
            "breakdown": {{
                "flights": float,
                "accommodation": float,
                "food": float,
                "activities": float,
                "local_transportation": float,
                "miscellaneous": float
            }},
            "tips": [string, string, ...],
            "currency": string
        }}
        """

        try:
            # Use the client.aio.models for async operations
            response = await client.aio.models.generate_content(
                model=MODEL_NAME,
                contents=prompt
            )

            # Extract JSON from the response
            response_text = response.text
            # Find JSON in the response
            start_idx = response_text.find('{')
            end_idx = response_text.rfind('}') + 1
            if start_idx >= 0 and end_idx > start_idx:
                json_str = response_text[start_idx:end_idx]
                result = json.loads(json_str)
                return result
            else:
                raise ValueError("No valid JSON found in response")
        except Exception as e:
            # If parsing fails, return a simplified response
            print(f"Error in generate_budget: {str(e)}")
            return {
                "total_budget": 0.0,
                "breakdown": {
                    "flights": 0.0,
                    "accommodation": 0.0,
                    "food": 0.0,
                    "activities": 0.0,
                    "local_transportation": 0.0,
                    "miscellaneous": 0.0
                },
                "tips": [f"Error generating budget: {str(e)}"],
                "currency": request_data['currency']
            }

    @staticmethod
    async def generate_trip_plan(request_data: Dict) -> Dict:
        """Generate a travel itinerary using Gemini AI."""
        interests = ", ".join(request_data['interests'])

        prompt = f"""
        Create a detailed {request_data['duration_days']}-day trip itinerary for {request_data['destination']}.

        Travel style: {request_data['travel_style']}
        Interests: {interests}
        {f"Travel dates/season: {request_data['travel_dates']}" if request_data.get('travel_dates') else ""}

        Include:
        1. Brief overview of the destination
        2. Day-by-day itinerary with:
           - Morning, afternoon, and evening activities
           - Recommended local meals and restaurants
           - Estimated time needed for each activity
           - Brief description of each recommended place
        3. Top recommended places to visit
        4. Transportation tips within the destination
        {f"5. Accommodation suggestions (budget, mid-range, and luxury options)" if request_data['include_accommodation'] else ""}

        Format the response as JSON with the following structure:
        {{
            "destination_overview": string,
            "daily_itinerary": [
                {{
                    "day": int,
                    "activities": [
                        {{
                            "time": string,
                            "activity": string,
                            "description": string,
                            "location": string
                        }}
                    ],
                    "meals": [
                        {{
                            "meal": string,
                            "recommendation": string,
                            "price_range": string
                        }}
                    ]
                }}
            ],
            "recommended_places": [
                {{
                    "name": string,
                    "description": string,
                    "why_visit": string
                }}
            ],
            "transportation_tips": [string, string, ...],
            "accommodation_suggestions": [
                {{
                    "name": string,
                    "category": string,
                    "price_range": string,
                    "area": string
                }}
            ]
        }}
        """

        try:
            response = await client.aio.models.generate_content(
                model=MODEL_NAME,
                contents=prompt
            )

            # Extract JSON from the response
            response_text = response.text
            # Find JSON in the response
            start_idx = response_text.find('{')
            end_idx = response_text.rfind('}') + 1
            if start_idx >= 0 and end_idx > start_idx:
                json_str = response_text[start_idx:end_idx]
                result = json.loads(json_str)
                return result
            else:
                raise ValueError("No valid JSON found in response")
        except Exception as e:
            print(f"Error in generate_trip_plan: {str(e)}")
            # If parsing fails, return a simplified response
            return {
                "destination_overview": f"Error generating trip plan: {str(e)}",
                "daily_itinerary": [{"day": 1, "activities": [], "meals": []}],
                "recommended_places": [],
                "transportation_tips": ["Error generating transportation tips."],
                "accommodation_suggestions": []
            }

    @staticmethod
    async def generate_travel_advice(request_data: Dict) -> Dict:
        """Generate travel advice using Gemini AI."""
        specific_questions = ""
        if request_data.get('specific_questions'):
            specific_questions = "\n".join([f"- {q}" for q in request_data.get('specific_questions', [])])

        additional_questions_prompt = ""
        if specific_questions:
            additional_questions_prompt = f"Also answer these specific questions:\n{specific_questions}"

        q_struct = ""
        if request_data.get('specific_questions'):
            q_parts = []
            for q in request_data.get('specific_questions', []):
                q_parts.append(f'"{q}": "answer"')
            q_struct = f'"answers_to_questions": {{{", ".join(q_parts)}}}'

        prompt = f"""
        Provide comprehensive travel advice for {request_data['destination']}.
        {f"Travel dates/season: {request_data['travel_dates']}" if request_data.get('travel_dates') else ""}
        {f"Trip duration: {request_data['duration_days']} days" if request_data.get('duration_days') else ""}

        Include information about:
        1. Typical weather conditions and what to expect
        2. Important local customs, etiquette, and cultural norms
        3. Safety tips specific to this destination
        4. Packing suggestions based on weather and activities
        5. Visa requirements for international travelers
        6. Currency information and payment methods
        7. Health recommendations (vaccinations, medical advice)

        {additional_questions_prompt}

        Format the response as JSON with the following structure:
        {{
            "weather_info": string,
            "local_customs": [string, string, ...],
            "safety_tips": [string, string, ...],
            "packing_suggestions": [string, string, ...],
            "visa_requirements": string,
            "currency_info": string,
            "health_recommendations": [string, string, ...]
            {q_struct}
        }}
        """

        try:
            response = await client.aio.models.generate_content(
                model=MODEL_NAME,
                contents=prompt
            )

            # Extract JSON from the response
            response_text = response.text
            # Find JSON in the response
            start_idx = response_text.find('{')
            end_idx = response_text.rfind('}') + 1
            if start_idx >= 0 and end_idx > start_idx:
                json_str = response_text[start_idx:end_idx]
                result = json.loads(json_str)
                return result
            else:
                raise ValueError("No valid JSON found in response")
        except Exception as e:
            print(f"Error in generate_travel_advice: {str(e)}")
            # If parsing fails, return a simplified response
            return {
                "weather_info": f"Error generating weather information: {str(e)}",
                "local_customs": ["Error generating local customs information."],
                "safety_tips": ["Error generating safety tips."],
                "packing_suggestions": ["Error generating packing suggestions."],
                "visa_requirements": "Error generating visa requirements.",
                "currency_info": "Error generating currency information.",
                "health_recommendations": ["Error generating health recommendations."]
            }