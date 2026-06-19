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
      print(f"[환경] APP_ENV = {ENV}")
      print(f"[Redis] {redis_settings.host}:{redis_settings.port}")

      async def run_consumer():
          while True:
              try:
                  await consume()
              except Exception as e:
                  print(f"[consume 재시작] {e}, 5초 후 재시도")
                  await asyncio.sleep(5)

      task = asyncio.create_task(run_consumer())
      yield
      task.cancel()

    # print("radis serveer?")
    # # yield 전 = FastAPI 시작할 때 실행
    # print(f"[환경] APP_ENV = {ENV}")
    # print(f"[Redis] {redis_settings.host}:{redis_settings.port}")
    # print(f"[Stream] 이벤트={redis_settings.stream_pdf_events} / 결과={redis_settings.stream_pdf_results}")
    # task = asyncio.create_task(consume())
    # yield # ← FastAPI가 실행되는 구간
    # # yield 후 = FastAPI 종료할 때 실행
    # task.cancel()
# ●  consume()은 단일 코루틴이고 asyncio.create_task()로 태스크 하나가 만들어짐
#   내부는 while True 무한루프로 메시지를 순차 처리

#   asyncio.create_task(consume())
#       └── while True
#               ├── xread (블로킹 대기)
#               ├── 메시지 도착
#               ├── process_message() 처리
#               └── 다시 xread 대기

#   즉, 메시지는 한 번에 하나씩 순서대로 처리. PDF가 동시에 여러 개 들어와도 큐에 쌓이고 하나씩 처리되는 구조

#   그래서 consume() 태스크가 죽으면 새 메시지가 와도 아무도 읽는 사람이 없는 상태가 되는 거