import json
import os
from dataclasses import dataclass, asdict
from typing import List, Optional

from dotenv import load_dotenv
from fastapi import APIRouter, Query, HTTPException
from pydantic import BaseModel
from googleapiclient.discovery import build

# Load environment variables
load_dotenv()
API_KEY = os.getenv("YOUTUBE_API_KEY")

if not API_KEY:
    raise ValueError("YOUTUBE_API_KEY environment variable not set")

# Create router
router = APIRouter(
    prefix="/youtube",
    tags=["YouTube"],
    responses={404: {"description": "Not found"}},
)


# Dataclass to structure video data
@dataclass
class YouTubeVideo:
    video_id: str
    title: str
    description: str
    channel_title: str
    publish_time: str


# Pydantic model for API response
class YouTubeVideoModel(BaseModel):
    video_id: str
    title: str
    description: str
    channel_title: str
    publish_time: str


class YouTubeSearchResponse(BaseModel):
    videos: List[YouTubeVideoModel]


def search_youtube_videos(query: str, max_results: int = 10) -> List[YouTubeVideo]:
    """
    Search YouTube videos using the YouTube Data API.
    """
    try:
        youtube = build('youtube', 'v3', developerKey=API_KEY)
        request = youtube.search().list(
            q=query,
            part='snippet',
            type='video',
            maxResults=max_results
        )
        response = request.execute()

        videos = []
        for item in response.get('items', []):
            video = YouTubeVideo(
                video_id=item['id']['videoId'],
                title=item['snippet']['title'],
                description=item['snippet']['description'],
                channel_title=item['snippet']['channelTitle'],
                publish_time=item['snippet']['publishedAt']
            )
            videos.append(video)

        return videos
    except Exception as e:
        # Log the error in a production environment
        print(f"Error searching YouTube videos: {e}")
        raise HTTPException(status_code=500, detail=f"Error searching YouTube videos: {str(e)}")


def save_videos_to_json(videos: List[YouTubeVideo], filename: str):
    """
    Save videos to a JSON file.
    """
    with open(filename, 'w', encoding='utf-8') as f:
        json.dump([asdict(video) for video in videos], f, ensure_ascii=False, indent=4)


@router.get("/", response_model=dict)
async def root():
    """
    Root endpoint returning API information.
    """
    return {
        "message": "YouTube Search API",
        "version": "1.0.0",
        "endpoints": {
            "/youtube/search": "Search for YouTube videos",
            "/youtube/search-and-save": "Search for YouTube videos and save results to a file"
        }
    }


@router.get("/search", response_model=YouTubeSearchResponse)
async def search_videos(
        query: str = Query(..., description="Search query for YouTube videos"),
        max_results: Optional[int] = Query(10, description="Maximum number of results to return", ge=1, le=50)
):
    """
    Search for YouTube videos based on a query.
    """
    videos = search_youtube_videos(query, max_results)
    # Convert dataclass objects to dictionaries
    video_dicts = [asdict(video) for video in videos]
    # Convert dictionaries to Pydantic models
    video_models = [YouTubeVideoModel(**video_dict) for video_dict in video_dicts]

    return {"videos": video_models}


@router.get("/search-and-save", response_model=dict)
async def search_and_save_videos(
        query: str = Query(..., description="Search query for YouTube videos"),
        max_results: Optional[int] = Query(10, description="Maximum number of results to return", ge=1, le=50),
        filename: Optional[str] = Query("youtube_results.json", description="Filename to save results to")
):
    """
    Search for YouTube videos based on a query and save results to a file.
    """
    videos = search_youtube_videos(query, max_results)
    save_videos_to_json(videos, filename)

    return {
        "message": f"Videos saved to {filename}",
        "video_count": len(videos)
    }