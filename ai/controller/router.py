"""
@file controller/router.py
@brief AVALON AI의 FastAPI 라우터 설정
@details 이 모듈은 AVALON AI의 FastAPI 라우터를 설정합니다.
@version 1.0
"""
from fastapi import APIRouter, Response, Request
from fastapi.responses import JSONResponse
from ai.dto.response.apilist.apilist_response import ApiListResponse, ApiItem, ScenarioItem
from ai.dto.request.apilist.apilist_map_request import convert_scenario_list, convert_api_list
from ai.service.apilist.state.mapping_state import create_initial_mapping_state
from ai.service.apilist.graphs.nodes.map_node import map_node
from ai.service.apilist.graphs.nodes.mapping_generation_node import mapping_generation_node
from ai.service.apilist.graphs.nodes.mapping_validation_node import mapping_validation_node
from ai.service.apilist.graphs.nodes.decision_node import decision_node
from datetime import datetime

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


@router.post('/api/list/v1/generate')
async def generate_api_list(request: Request):
    avalon = request.cookies.get("avalon")
    if not avalon:
        return JSONResponse(content={"error": "avalon 쿠키가 필요합니다."}, status_code=400)
    # 실제 매핑 요청 처리 로직(생략)
    return JSONResponse(content={"processedAt": datetime.now().isoformat()})


@router.post('/api/list/v1/create')
async def create_api_list(request: Request):
    avalon = request.cookies.get("avalon")
    if not avalon:
        return JSONResponse(content={"error": "avalon 쿠키가 필요합니다."}, status_code=400)

    body = await request.json()
    scenario_list = body.get("scenarioList", [])
    api_list = body.get("apiList", [])

    converted_scenarios = convert_scenario_list(scenario_list)
    converted_apis = convert_api_list(api_list)

    state = create_initial_mapping_state(avalon=avalon)
    state["scenarios"] = converted_scenarios
    state["api_lists"] = converted_apis

    state = map_node(state)
    state = mapping_generation_node(state)
    state = mapping_validation_node(state)
    state = decision_node(state)

    # 설계서 구조에 맞게 응답 가공
    from ai.dto.response.apilist.apilist_validation_response import ApiListValidationResponse, ApiMappingItem

    print("=== 최종 state ===")
    print(state)
    
    # generated_mapping_table에서 매핑 데이터 가져오기
    mapping_table = state.get("generated_mapping_table", [])
    api_mapping_list = [
        ApiMappingItem(
            scenarioId=item["scenarioId"],
            stepName=item["stepName"],
            apiName=item["apiName"],
            description=item["description"],
            uri=item["uri"],
            method=item["method"],
            parameters=item["parameters"],
            responseStructure=item["responseStructure"],
        )
        for item in mapping_table
    ]

    # validation_result에서 검증 점수 가져오기
    validation_result = state.get("validation_result", {})
    validation_score = validation_result.get("validation_score", 0.0)
    
    # validation_score가 없으면 validationRate에서 가져오기
    if validation_score == 0.0:
        validation_score = state.get("validationRate", 0.0)

    response = ApiListValidationResponse(
        processedAt=datetime.now().isoformat(),
        validationRate=validation_score,
        apiMapping=api_mapping_list
    )
    return JSONResponse(content=response.dict())

