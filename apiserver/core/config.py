# apiserver/core/config.py
import os
from pathlib import Path
from pydantic_settings import BaseSettings

# 현재 실행 환경 (기본값: dev)
ENV = os.getenv("APP_ENV", "dev")

class RedisSettings(BaseSettings):
    host: str = "localhost"
    port: int = 6379

    stream_pdf_events:  str = "pdf:events"
    stream_pdf_results: str = "pdf:results"

    class Config:
        env_prefix = "REDIS_"
        # core/ 기준 한 단계 위(apiserver/)의 .env.dev 또는 .env.prod
        env_file = Path(__file__).parent.parent / f".env.{ENV}"

redis_settings = RedisSettings()
