"""
@file: main.py
@brief AVALON AI의 FastAPI 애플리케이션 설정
@details 이 모듈은 AVALON AI의 FastAPI 애플리케이션을 설정합니다.
@version 1.0
"""

from contextlib import asynccontextmanager
from dotenv import load_dotenv
from fastapi import FastAPI
from ai.controller.router import router

@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    FastAPI 시작 및 종료 이벤트 핸들러
    """
    # 여기에 애플리케이션 시작 시 필요한 초기화 작업을 추가할 수 있습니다.
    print("Starting AVALON AI application...")
    load_dotenv()
    yield
    # 여기에 애플리케이션 종료 시 필요한 정리 작업을 추가할 수 있습니다.

app = FastAPI(lifespan=lifespan)

app.include_router(router)
