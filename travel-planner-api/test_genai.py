import os
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

# Print the API keys
print(f"OPEN_TRIP_MAP_KEY: {os.getenv('OPEN_TRIP_MAP_KEY', 'NOT FOUND')}")
print(f"EXCHANGE_RATE_KEY: {os.getenv('EXCHANGE_RATE_KEY', 'NOT FOUND')}")
print(f"GEMINI_API_KEY: {os.getenv('GEMINI_API_KEY', 'NOT FOUND')[:5]}... (masked)")