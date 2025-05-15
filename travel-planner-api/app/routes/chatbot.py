from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain.prompts import PromptTemplate
from langchain.chains import LLMChain
import os
import re
import googleapiclient.discovery
from dotenv import load_dotenv
import google.generativeai as genai

# Load environment variables
load_dotenv()

# Configure Google APIs
genai.configure(api_key=os.getenv('GOOGLE_API_KEY'))
YOUTUBE_API_KEY = os.getenv('YOUTUBE_API_KEY')

# Create router
router = APIRouter(
    prefix="/chatbot",
    tags=["Chatbot"],
    responses={404: {"description": "Not found"}},
)

# Structured Tour Guide Prompt with YouTube context
TOUR_GUIDE_PROMPT = """
You are Atlas, a world-class AI Tour Guide with unparalleled expertise in global travel. 
Your mission is to be the ultimate travel companion, offering transformative travel experiences 
through deep knowledge, personalized insights, and genuine passion for exploration.

Communication Principles:
- Speak with the wisdom of a seasoned traveler and the warmth of a trusted friend
- Blend cultural insights, historical context, and contemporary travel intelligence
- Craft responses that are informative, inspiring, and actionable

Comprehensive Travel Assistance:
- Provide nuanced destination recommendations tailored to individual preferences
- Offer insider tips that reveal the soul of a destination beyond typical tourist paths
- Balance practical logistics with cultural immersion and personal discovery

Personalization Strategy:
- Ask thoughtful, probing questions to understand the traveler's unique aspirations
- Customize recommendations considering:
  * Personal interests (adventure, culture, cuisine, history)
  * Travel style (budget, luxury, backpacking)
  * Group composition (solo, family, couples)
  * Season and current global conditions

Expert Guidance Includes:
- Detailed itinerary crafting
- Cultural etiquette and local interaction tips
- Hidden gem discoveries
- Sustainable and responsible travel advice
- Safety considerations and local insights

IMPORTANT CONTEXT FROM YOUTUBE VIDEO:
{video_context}

Response Mandate:
Transform every travel query into a gateway of potential adventure, 
connection, and personal growth. You're not just sharing information, 
you're opening worlds of possibility.

Current Traveler's Query: {query}

Respond with: 
- Comprehensive and engaging information
- Personal touch
- Actionable recommendations
- References to relevant insights from the video context when applicable
"""


# Pydantic models for request and response
class TravelQuery(BaseModel):
    message: str


class TravelResponse(BaseModel):
    response: str
    video_used: str = None


# Function to extract location from user query
def extract_location(query):
    """
    Extract location names from the user query using regex patterns
    """
    # Pattern to match "going to [Location]" or similar phrases
    patterns = [
        r"(?:going|travel|visiting|trip|visit)\s+to\s+([A-Za-z\s]+)",
        r"(?:in|at|around)\s+([A-Za-z\s]+)",
        r"explore\s+([A-Za-z\s]+)",
        r"discover\s+([A-Za-z\s]+)"
    ]

    for pattern in patterns:
        match = re.search(pattern, query, re.IGNORECASE)
        if match:
            location = match.group(1).strip()
            return location

    # If no pattern matches, try to identify proper nouns
    words = query.split()
    for word in words:
        if word[0].isupper() and len(word) > 3 and word.lower() not in [
            "what", "where", "when", "how", "which", "why", "who",
            "could", "would", "should", "will", "this", "that"
        ]:
            return word

    return None


# Function to search for top videos about a location
def search_youtube_video(location):
    """
    Search YouTube for the top video about a specific location
    """
    youtube = googleapiclient.discovery.build(
        "youtube", "v3", developerKey=YOUTUBE_API_KEY
    )

    search_response = youtube.search().list(
        q=f"{location} travel guide",
        part="snippet",
        maxResults=1,
        type="video"
    ).execute()

    if not search_response.get("items"):
        return None, None

    video_id = search_response["items"][0]["id"]["videoId"]
    video_title = search_response["items"][0]["snippet"]["title"]
    video_url = f"https://www.youtube.com/watch?v={video_id}"

    return video_id, video_url


# Function to get video understanding from Gemini
def analyze_youtube_video(video_url):
    """
    Use Gemini 2.0's video understanding capabilities to summarize a YouTube video
    """
    try:
        model = genai.GenerativeModel(model_name="gemini-2.0-flash")

        response = model.generate_content(
            contents=[
                {
                    "file_data": {
                        "file_uri": video_url,
                        "mime_type": "text/html"
                    }
                },
                {
                    "text": "Please provide a detailed summary of this travel video. Focus on key attractions, cultural insights, practical travel tips, and unique experiences featured in the video."
                }
            ]
        )

        return response.text
    except Exception as e:
        print(f"Error analyzing video: {str(e)}")
        return "No video context available."


# Initialize LLM and Chain with video context
def create_tour_guide_chain(video_context=""):
    """
    Create a new LangChain tour guide assistant with video context
    """
    # Use Gemini Pro through LangChain
    llm = ChatGoogleGenerativeAI(
        model="gemini-2.0-flash",
        google_api_key=os.getenv('GOOGLE_API_KEY'),
        temperature=0.7,  # Creative but controlled
        top_p=0.9,
        max_tokens=1024
    )

    # Create prompt template with video context
    prompt = PromptTemplate(
        input_variables=["query", "video_context"],
        template=TOUR_GUIDE_PROMPT
    )

    # Create conversation chain
    return LLMChain(llm=llm, prompt=prompt)


@router.post("/chat", response_model=TravelResponse)
async def chat_endpoint(query: TravelQuery):
    """
    Process travel-related queries using Gemini 2.0 with YouTube video context
    """
    try:
        # Extract location from query
        location = extract_location(query.message)
        video_url = None
        video_context = "No specific location detected in the query."

        # If location is found, search for relevant YouTube video
        if location:
            video_id, video_url = search_youtube_video(location)
            if video_id:
                # Get video understanding from Gemini
                video_context = analyze_youtube_video(video_url)

        # Create a tour guide chain with the video context
        tour_guide_chain = create_tour_guide_chain()

        # Run the chain with both query and video context
        response = tour_guide_chain.run(
            query=query.message,
            video_context=video_context
        )

        return TravelResponse(
            response=response,
            video_used=video_url
        )

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


# Conversation history management with video context
conversation_history = []
video_contexts = {}


@router.post("/chat/context", response_model=TravelResponse)
async def contextual_chat_endpoint(query: TravelQuery):
    """
    Process queries with conversation context and YouTube video insights
    """
    try:
        # Extract location from query
        location = extract_location(query.message)
        video_url = None

        # If location is found, search for relevant YouTube video
        if location and location not in video_contexts:
            video_id, video_url = search_youtube_video(location)
            if video_id:
                # Get video understanding from Gemini
                video_contexts[location] = analyze_youtube_video(video_url)

        # Combine all video contexts we have gathered
        combined_video_context = "\n\n".join([
            f"Context for {loc}: {context}"
            for loc, context in video_contexts.items()
        ]) or "No specific location detected in conversation history."

        # Add current query to conversation history
        conversation_history.append(query.message)

        # Limit conversation history to last 5 messages
        context_prompt = TOUR_GUIDE_PROMPT + "\n\nConversation History: " + \
                         "\n".join(conversation_history[-5:])

        # Create a temporary prompt template with extended context
        prompt = PromptTemplate(
            input_variables=["query", "video_context"],
            template=context_prompt
        )

        # Create a tour guide chain
        llm = ChatGoogleGenerativeAI(
            model="gemini-2.0-flash",
            google_api_key=os.getenv('GOOGLE_API_KEY'),
            temperature=0.7,
            top_p=0.9,
            max_tokens=1024
        )

        # Create a temporary chain with the contextual prompt
        contextual_chain = LLMChain(
            llm=llm,
            prompt=prompt
        )

        # Run the contextual chain
        response = contextual_chain.run(
            query=query.message,
            video_context=combined_video_context
        )

        return TravelResponse(
            response=response,
            video_used=video_url
        )

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


# Health check endpoint
@router.get("/health")
async def health_check():
    """
    Simple health check endpoint
    """
    return {
        "status": "healthy",
        "assistant_name": "Atlas",
        "model": "Gemini 2.0 Flash",
        "features": ["YouTube video context integration"]
    }