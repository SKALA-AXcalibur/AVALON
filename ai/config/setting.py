# config/settings.py
from pydantic import BaseSettings

class Settings(BaseSettings):
    project_api_base_url: str

    class Config:
        env_file = ".env"

settings = Settings()
