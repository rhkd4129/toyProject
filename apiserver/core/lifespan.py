from contextlib import asynccontextmanager
from consumers.stream_consumer import consume
from core.config import redis_settings, ENV
import asyncio
import redis.asyncio as redis
import easyocr



# yield 전  →  서버 켜질 때 (=setUp)
# yield      →  서버 운영 중
# yield 후  →  서버 꺼질 때 (=tearDown)
# FastAPI 서버가 켜질 때 / 꺼질 때 실행할 코드를 등록하는 함수
#   async def lifespan(app):
#       # ── 서버 시작 ──────────────────────────
#       task = asyncio.create_task(consume())   # Redis 구독 시작

#       yield  # ← 여기서 FastAPI가 실제로 동작함 (요청 받는 구간)

#       # ── 서버 종료 ──────────────────────────
#       task.cancel()
@asynccontextmanager
#with 문을 async 함수로 만들어주는 데코레이터
#yield 기준으로 앞/뒤
async def lifespan(app):
    print("radis serveer?")
    # yield 전 = FastAPI 시작할 때 실행
    print(f"[환경] APP_ENV = {ENV}")
    print(f"[Redis] {redis_settings.host}:{redis_settings.port}")
    print(f"[Stream] 이벤트={redis_settings.stream_pdf_events} / 결과={redis_settings.stream_pdf_results}")
    task = asyncio.create_task(consume())
    yield # ← FastAPI가 실행되는 구간
    # yield 후 = FastAPI 종료할 때 실행
    task.cancel()
