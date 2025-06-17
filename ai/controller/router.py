"""
@file controller/router.py
@brief AVALON AI의 FastAPI 라우터 설정
@details 이 모듈은 AVALON AI의 FastAPI 라우터를 설정합니다.
@version 1.0
"""

from typing import Dict
from fastapi import APIRouter, File, Form, HTTPException, Response, UploadFile, requests

from service.spec.formatter import formatter
from service.spec.interface_def_parser import InterfaceDefParserService
from service.spec.interface_impl_parser import InterfaceImplParserService
from service.spec.db_design_parser import DbDesignParserService
from service.spec.requirement_parser import RequirementParserService
from service.spec.info_save_service import save_to_info_api

router = APIRouter()


@router.get("/")
async def read_root() -> Response:
    """
    AVALON AI API의 기본 엔드포인트

    """
    return Response(
        content={"message": "Welcome to AVALON AI API"}, media_type="application/json"
    )


@router.post("/api/spec/v1/analyze")
async def analyze_spec(
    projectId: str = Form(...),
    requirementFile: UploadFile = File(...),
    interfaceDef: UploadFile = File(...),
    interfaceDesign: UploadFile = File(...),
    databaseDesign: UploadFile = File(...),
) -> Response:
    """
    명세서 분석
    4가지 문서 파싱 후 포맷팅
    정보저장api를 통해 저장
    """

    # 포맷팅
    try:
        result = await formatter(
            requirementFile, interfaceDesign, interfaceDef, databaseDesign
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

    # 정보저장API로 POST 요청
    response = await save_to_info_api(result.model_dump())  # dict로 변환해서 넘김
    return Response


@router.post("/api/scenario/v1/generate")
async def generate_scenario() -> Response:
    """
    LLM을 통해 명세서 분석 결과를 바탕으로 테스트 시나리오를 생성하고 검증을 거친 후 반환
    """

    return Response()


@router.post("/api/scenario/v1/scenario")
async def generate_flow_chart() -> Response:
    """
    LLM을 통해 시나리오와 API 목록을 바탕으로 흐름도를 생성하고 시나리오 ID를 기준으로 흐름도 반환
    """

    return Response()
