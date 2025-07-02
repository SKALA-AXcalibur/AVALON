"""
@file controller/router.py
@brief AVALON AI의 FastAPI 라우터 설정
@details 이 모듈은 AVALON AI의 FastAPI 라우터를 설정합니다.
@version 1.0
"""

from fastapi import APIRouter, HTTPException, Response
from fastapi.responses import JSONResponse

from dto.response.scenario.scenario_flow_response import ScenarioFlowResponse
from dto.response.scenario.scenario_response import ScenarioResponse
from dto.request.scenario.scenario_flow_request import ScenarioFlowRequest
from dto.request.scenario.scenario_request import ScenarioRequest
from service.scenario.scenario_flow_agent import ScenarioFlowAgent
from service.scenario.scenario_flow_storage_service import ScenarioFlowStorageService
from service.scenario.scenario_graph import create_scenario_graph
from state.scenario_state import create_initial_state
import logging

router = APIRouter()


@router.get("/")
async def read_root() -> Response:
    """
    AVALON AI API의 기본 엔드포인트

    """
    return Response(
        content={"message": "Welcome to AVALON AI API"}, media_type="application/json"
    )


@router.post("/api/scenario/v1/generate")
async def generate_scenario(request: ScenarioRequest) -> ScenarioResponse:
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
        scenario_flow_chart = await flow_agent.generate_scenario_flow(request)

        # 생성된 플로우 차트를 데이터베이스에 저장
        storage_service = ScenarioFlowStorageService()

        failed_count = 0

        for scenario_item in request.scenario_list:
            try:
                storage_service.save_scenario_flow(
                    scenario_id=scenario_item.id, flow_chart=scenario_flow_chart.data
                )
            except Exception as e:
                logging.error(f"저장 실패: {str(e)}")
                failed_count += 1

        total_count = len(request.scenario_list)
        if failed_count == 0:
            return scenario_flow_chart
        elif failed_count < total_count:
            return JSONResponse(
                status_code=207, content=scenario_flow_chart.model_dump()
            )
        else:
            raise HTTPException(status_code=500, detail="모든 시나리오 저장 실패")

    except Exception as e:
        logging.exception("플로우 차트 생성 중 예외 발생")
        raise HTTPException(status_code=500, detail=f"플로우 차트 생성 실패")
