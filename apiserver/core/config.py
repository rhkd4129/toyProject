import os
from pathlib import Path
from pydantic_settings import BaseSettings, SettingsConfigDict

# 환경변수 APP_ENV 값을 읽어서 ENV에 저장 (없으면 기본값 "dev")
# 실행 시 APP_ENV=prod python main.py 처럼 지정 가능
ENV = os.getenv("APP_ENV", "dev")

class RedisSettings(BaseSettings):
    host: str = "localhost"
    port: int = 6379
    stream_pdf_events:  str = "pdf:events"
    stream_pdf_results: str = "pdf:results"

    model_config = SettingsConfigDict(
        # 이 클래스는 "REDIS_" 로 시작하는 환경변수만 읽음
        # ex) REDIS_HOST → host, REDIS_PORT → port
        env_prefix="REDIS_",

        # .env.dev 또는 .env.prod 파일에서 환경변수를 로드
        # Path(__file__) = 현재 파일(config.py)의 경로
        # .parent.parent = config.py → core/ → apiserver/ (두 단계 위)
        # 즉 apiserver/.env.dev 를 읽음
        env_file=Path(__file__).parent.parent / f".env.{ENV}",

        # ★ 핵심 ★
        # env_file 을 로드하면 PDF_FONT_PATH 같은 다른 클래스용 값도 같이 딸려옴
        # extra="ignore" 가 없으면 Pydantic이 "이 필드는 내 모델에 없는데?"
        # 라며 ValidationError 를 발생시킴
        # extra="ignore" 를 지정하면 모델에 정의되지 않은 필드는 조용히 무시함
        extra="ignore",
    )

redis_settings = RedisSettings()


class PathSettings(BaseSettings):
    xlsx_file_name: str = r"C:\Server\PDF\CONFIG\{year}사건부.xlsx"
    font_path: str      = r"C:\Server\PDF\CONFIG\NANUMGOTHIC.TTF"
    output_path: str    = r"C:\Server\PDF\{task_id}.xlsx"

    model_config = SettingsConfigDict(
        # 이 클래스는 "PDF_" 로 시작하는 환경변수만 읽음
        # ex) PDF_XLSX_FILE_NAME → xlsx_file_name, PDF_FONT_PATH → font_path
        env_prefix="PDF_",

        # 동일하게 apiserver/.env.dev 파일을 읽음
        env_file=Path(__file__).parent.parent / f".env.{ENV}",

        # RedisSettings 와 마찬가지로 REDIS_HOST 같은 값들이 딸려오지만
        # "PDF_" prefix 가 없는 값들은 어차피 매핑이 안 되고
        # extra="ignore" 로 조용히 무시됨
        extra="ignore",
    )

path_settings = PathSettings()


class S3Settings(BaseSettings):
    enabled: bool = False
    bucket: str = ""
    region: str = "ap-northeast-2"
    result_key_prefix: str = "xlsx/"

    model_config = SettingsConfigDict(
        env_prefix="S3_",
        env_file=Path(__file__).parent.parent / f".env.{ENV}",
        extra="ignore",
    )

s3_config = S3Settings()