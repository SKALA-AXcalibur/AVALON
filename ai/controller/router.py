"""
@file controller/router.py
@brief AVALON AI의 FastAPI 라우터 설정
@details 이 모듈은 AVALON AI의 FastAPI 라우터를 설정합니다.
@version 1.0
"""
from fastapi import APIRouter, Response, Request
from fastapi.responses import JSONResponse
from ai.dto.response.apilist.apilist_response import ApiListResponse, ApiItem, ScenarioItem
from ai.service.apilist.state.mapping_state import create_initial_mapping_state
from ai.service.apilist.graphs.nodes.map_node import map_node
from ai.service.apilist.graphs.nodes.mapping_generation_node import mapping_generation_node
from ai.service.apilist.graphs.nodes.mapping_validation_node import mapping_validation_node
from ai.service.apilist.graphs.nodes.decision_node import decision_node
from ai.service.apilist.graphs.nodes.feedback_node import feedback_node

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
async def generate_api_list():
    # 실제로는 DB나 백엔드에서 데이터 조회
    api_list = [
        ApiItem(id="api1", name="API 1", url="/api/1", path="/api/1", method="GET"),
        # ... 실제 데이터로 채우기
    ]
    scenario_list = [
        ScenarioItem(id="scn1", name="시나리오1", description="설명", validation="Y", projectKey=1),
        # ... 실제 데이터로 채우기
    ]
    response = ApiListResponse(apiList=api_list, scenarioList=scenario_list)
    return JSONResponse(content=response.dict())


@router.post('/api/list/v1/create')
async def create_api_list(request: Request):
    avalon = request.cookies.get("avalon")
    if not avalon:
        return JSONResponse(content={"error": "avalon 쿠키가 필요합니다."}, status_code=400)

    state = create_initial_mapping_state(avalon=avalon)
    state = map_node(state)
    state = mapping_generation_node(state)
    state = mapping_validation_node(state)
    state = decision_node(state)
    if state.get("current_step") == "feedback":
        state = feedback_node(state)

    return JSONResponse(content=state)

