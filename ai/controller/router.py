"""
@file controller/router.py
@brief AVALON AI의 FastAPI 라우터 설정
@details 이 모듈은 AVALON AI의 FastAPI 라우터를 설정합니다.
@version 1.0
"""

import logging
import traceback
from typing import Dict
from fastapi import APIRouter, File, Form, HTTPException, Response, UploadFile
from pydantic import ValidationError

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
    interfaceDesign: UploadFile = File(...),
    interfaceDef: UploadFile = File(...),
    databaseDesign: UploadFile = File(...),
):
    """
    명세서 분석 api

    명세서 파일을 바탕으로 파싱을 진행한다.

    Args:
        project_id (str): 분석 대상 프로젝트 ID
        requirementFile (UploadFile): 요구사항 정의서 엑셀 파일
        interfaceDesign (UploadFile): 인터페이스 설계서 엑셀 파일
        interfaceDef (UploadFile): 인터페이스 정의서 엑셀 파일
        database_design (UploadFile): DB 설계서 엑셀 파일
    """
    try:
        result = await formatter(
            requirementFile, interfaceDesign, interfaceDef, databaseDesign
        )  # 파일 추가
    except ValidationError as e:
        logging.warning("명세서 Validation 실패: %s", e)
        raise HTTPException(status_code=422, detail=str(e))
    except Exception as e:
        logging.error("분석 중 예외 발생: %s", e)
        logging.error(traceback.format_exc())
        raise HTTPException(status_code=500, detail=f"{type(e).__name__}: {e}")

    result_dict = result.model_dump()
    result_dict.pop("projectId", None)

    try:
        response = await save_to_info_api(projectId, result_dict)
    except Exception as e:
        logging.error("정보저장API 호출 실패: %s", e)
        raise HTTPException(status_code=502, detail="정보 저장 실패")

    return response


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
