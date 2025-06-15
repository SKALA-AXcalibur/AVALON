"""
@file controller/router.py
@brief AVALON AI의 FastAPI 라우터 설정
@details 이 모듈은 AVALON AI의 FastAPI 라우터를 설정합니다.
@version 1.0
"""
from fastapi import APIRouter, Response

router = APIRouter()


@router.get("/")
async def read_root() -> Response:
    """
    AVALON AI API의 기본 엔드포인트

    """
    return Response(content={"message": "Welcome to AVALON AI API"}, media_type="application/json")


@router.post('/api/scenario/v1/generate')
async def generate_scenario() -> Response:
    """
    LLM을 통해 명세서 분석 결과를 바탕으로 테스트 시나리오를 생성하고 검증을 거친 후 반환
    """
    return Response()



@router.post('/api/scenario/v1/scenario')
async def generate_flow_chart() -> Response:
    """
    LLM을 통해 시나리오와 API 목록을 바탕으로 흐름도를 생성하고 시나리오 ID를 기준으로 흐름도 반환
    """
    return Response()
