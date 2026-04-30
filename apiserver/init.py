
from contextlib import asynccontextmanager
from stream_consumer import consume
import asyncio
import redis.asyncio as redis
import easyocr



# yield 전  →  서버 켜질 때 (=setUp)
# yield      →  서버 운영 중
# yield 후  →  서버 꺼질 때 (=tearDown)

@asynccontextmanager
#with 문을 async 함수로 만들어주는 데코레이터
#yield 기준으로 앞/뒤
async def lifespan(app):
    print("radis serveer?")
    # yield 전 = FastAPI 시작할 때 실행
    task = asyncio.create_task(consume())
    yield # ← FastAPI가 실행되는 구간
    # yield 후 = FastAPI 종료할 때 실행
    task.cancel()


