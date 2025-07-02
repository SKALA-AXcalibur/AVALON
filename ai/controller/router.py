"""
@file controller/router.py
@brief AVALON AI의 FastAPI 라우터 설정
@details 이 모듈은 AVALON AI의 FastAPI 라우터를 설정합니다.
@version 1.0
"""
from dto.response.apilist.apilist_response import ApiListResponse, ApiItem, ScenarioItem
from dto.request.apilist.apilist_map_request import convert_scenario_list, convert_api_list
from service.apilist.mapping_state_processor import create_initial_mapping_state
from service.apilist.apilist_graph import create_apilist_graph
from dto.response.apilist.apilist_validation_response import ApiListValidationResponse
from dto.request.apilist.common import ApiMappingItem
from service.apilist.api_mapping_service import apiMappingService
from datetime import datetime

import logging
import traceback
from typing import Dict
from fastapi import APIRouter, Response, File, Form, HTTPException, Request, UploadFile, Body
from fastapi.responses import JSONResponse
from pydantic import ValidationError

from service.spec.formatter import formatter
from service.spec.interface_def_parser import InterfaceDefParserService
from service.spec.interface_impl_parser import InterfaceImplParserService
from service.spec.db_design_parser import DbDesignParserService
from service.spec.requirement_parser import RequirementParserService

from service.spec.info_save_service import save_to_info_api

from dto.request.testcase.tc_generation_request import TestcaseGenerationRequest
from dto.response.scenario.scenario_flow_response import ScenarioFlowResponse
from dto.response.scenario.scenario_response import ScenarioResponse
from dto.request.scenario.scenario_flow_request import ScenarioFlowRequest
from dto.request.scenario.scenario_request import ScenarioRequest
from service.scenario.scenario_flow_agent import ScenarioFlowAgent
from service.scenario.scenario_flow_storage_service import ScenarioFlowStorageService
from service.scenario.scenario_graph import create_scenario_graph
from state.scenario_state import create_initial_state
from service.testcase.tc_generation_flow import build_testcase_flow
from service.testcase.tc_response_builder import build_tc_response_from_state

from state.testcase.flow_state import FlowState

router = APIRouter()


@router.get("/")
async def read_root() -> Response:
    """
    AVALON AI API의 기본 엔드포인트

    """
    return JSONResponse(content={"message": "Welcome to AVALON AI API"})


@router.post("/api/spec/v1/analyze")
async def analyze_spec(
    project_id: str = Form(...),
    requirement_file: UploadFile = File(...),
    interface_design: UploadFile = File(...),
    interface_def: UploadFile = File(...),
    database_design: UploadFile = File(...),
):
    """
    명세서 분석 API

    명세서 파일을 파싱하고, 파싱 결과를 정보저장 API로 전달한다.

    Args:
        project_id (str): 분석 대상 프로젝트 ID
        requirement_file (UploadFile): 요구사항 정의서
        interface_design (UploadFile): 인터페이스 설계서
        interface_def (UploadFile): 인터페이스 정의서
        database_design (UploadFile): DB 설계서
    """
    try:
        result = await formatter(
            requirement_file, interface_design, interface_def, database_design
        )
    except Exception as e:
        logging.error("[formatter 실패] %s", traceback.format_exc())
        raise HTTPException(
            status_code=500,
            detail={"error": "분석 실패", "reason": f"{type(e).__name__}: {e}"},
        )

    result_dict = result.model_dump()
    result_dict.pop("project_id", None)

    try:
        response = await save_to_info_api(project_id, result_dict)
    except Exception as e:
        logging.error("[정보저장 API 실패] %s", traceback.format_exc())
        raise HTTPException(
            status_code=502,
            detail={"error": "정보 저장 실패", "reason": f"{type(e).__name__}: {e}"},
        )

    return {
        "message": "분석 및 저장 성공",
    }


@router.post("/api/scenario/v1/generate")
async def generate_scenario(request: ScenarioRequest) -> Response:
    """
    LangGraph를 통해 명세서 분석 결과를 바탕으로 테스트 시나리오를 생성하고 검증을 거친 후 반환
    """
    try:
        graph = create_scenario_graph()
        # 딕셔너리 상태 생성
        initial_state = create_initial_state(request)
        final_state = await graph.ainvoke(initial_state)

        generated_scenarios = final_state.get("generated_scenarios")
        if generated_scenarios is None:
            error_msg = final_state.get("error_message", "알 수 없는 오류")
            logging.error(f"시나리오 생성 실패: {error_msg}")
            raise HTTPException(status_code=500, detail=error_msg)

        # 응답 변환 - ScenarioResponse 객체 자체를 반환
        return generated_scenarios

    except HTTPException:
        raise
    except Exception as e:
        logging.exception("시나리오 생성 중 예외 발생")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")


@router.post("/api/scenario/v1/scenario")
async def generate_flow_chart(request: ScenarioFlowRequest) -> ScenarioFlowResponse:
    """
    LLM을 통해 시나리오와 API 목록을 바탕으로 흐름도를 생성하고 시나리오 ID를 기준으로 흐름도 반환
    """
    try:
        flow_agent = ScenarioFlowAgent()

        # 한 번에 응답용과 저장용 데이터 생성
        scenario_flow_response, individual_flow_charts = (
            await flow_agent.generate_scenario_flow(request)
        )

        # 생성된 플로우 차트를 데이터베이스에 저장
        storage_service = ScenarioFlowStorageService()

        failed_count = 0

        # 각 시나리오별로 생성된 플로우차트를 해당 시나리오 ID로 저장
        for scenario_id, flow_chart in individual_flow_charts.items():
            try:
                storage_service.save_scenario_flow(
                    scenario_id=scenario_id, flow_chart=flow_chart
                )
            except Exception as e:
                logging.error(f"시나리오 ID {scenario_id} 저장 실패: {str(e)}")
                failed_count += 1

        total_count = len(individual_flow_charts)
        if failed_count == 0:
            return scenario_flow_response
        elif failed_count < total_count:
            return JSONResponse(
                status_code=207, content=scenario_flow_response.model_dump()
            )
        else:
            raise HTTPException(status_code=500, detail="모든 시나리오 저장 실패")

    except Exception as e:
        logging.exception("플로우 차트 생성 중 예외 발생")
        raise HTTPException(status_code=500, detail=f"플로우 차트 생성 실패")

@router.post('/api/list/v1/create')
async def create_api_list(request: Request):
    avalon = request.cookies.get("avalon")
    if not avalon:
        return JSONResponse(content={"error": "avalon 쿠키가 필요합니다."}, status_code=400)

    body = await request.json()
    scenario_list = body.get("scenarioList", [])
    api_list = body.get("apiList", [])

    # 서비스 계층에 위임
    response = await apiMappingService.doApiMapping(avalon, scenario_list, api_list)
    return JSONResponse(content=response)

@router.post('/api/list/v1/create')
async def create_api_list(request: Request):
    avalon = request.cookies.get("avalon")
    if not avalon:
        return JSONResponse(content={"error": "avalon 쿠키가 필요합니다."}, status_code=400)

    body = await request.json()
    scenario_list = body.get("scenarioList", [])
    api_list = body.get("apiList", [])

    # 서비스 계층에 위임
    response = await apiMappingService.doApiMapping(avalon, scenario_list, api_list)
    return JSONResponse(content=response)


@router.post("/api/tc/v1/{scenario_id}")
async def generate_testcases(
    scenario_id: str, request: TestcaseGenerationRequest = Body(...)
) -> JSONResponse:
    """
    API 매핑표와 시나리오 관련 문서 활용하여 TC 생성 및 검증 진행
    """
    logging.info(f"[TC 생성 요청] scenario_id: {scenario_id}")  # 진입점 확인
    state = FlowState(scenario_id=scenario_id, request=request)

    # 그래프 빌드
    graph = build_testcase_flow()

    # 그래프 실행
    result: dict = await graph.ainvoke(state)

    # 응답 조합
    response = build_tc_response_from_state(result)

    return JSONResponse(content=response.model_dump(by_alias=True, mode="json"))
