"""
@file controller/router.py
@brief AVALON AI의 FastAPI 라우터 설정
@details 이 모듈은 AVALON AI의 FastAPI 라우터를 설정합니다.
@version 1.0
"""

from fastapi import APIRouter, HTTPException
from fastapi.responses import JSONResponse

from dto.request.scenario.scenario_flow_request import ScenarioFlowRequest
from service.scenario.agents.scenario_flow_agent import ScenarioFlowAgent
from dto.request.scenario.scenario_request import ScenarioRequest
from service.scenario.scenario_flow_storage_service import ScenarioFlowStorageService
from service.scenario.graphs.scenario_graph import create_scenario_graph
from service.scenario.state.scenario_state import create_initial_state
import logging

router = APIRouter()


@router.get("/")
async def read_root() -> JSONResponse:
    """
    AVALON AI API의 기본 엔드포인트
    """
    return JSONResponse(content={"message": "Welcome to AVALON AI API"})


@router.post("/api/scenario/v1/generate")
async def generate_scenario(request: ScenarioRequest):
    """
    LangGraph를 통해 명세서 분석 결과를 바탕으로 테스트 시나리오를 생성하고 검증을 거친 후 반환
    """
    try:
        graph = create_scenario_graph()
        initial_state = create_initial_state(request_data=request, max_attempts=3)

        # 워크플로우 실행
        final_state = graph.invoke(initial_state)

        # 결과 추출
        generated_scenarios = final_state.get("generated_scenarios")

        if generated_scenarios is None:
            error_msg = final_state.get(
                "error_message", "시나리오 생성에 실패했습니다."
            )
            logging.error(
                f"시나리오 생성 실패 - 프로젝트: {request.project_id}, 오류: {error_msg}"
            )
            raise HTTPException(
                status_code=500, detail=f"Failed to generate scenario. {error_msg}"
            )

        return generated_scenarios

    except HTTPException:
        raise
    except Exception as e:
        logging.exception(
            f"시나리오 생성 중 예외 발생"
        )
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")


@router.post("/api/scenario/v1/scenario")
async def generate_flow_chart(request: ScenarioFlowRequest):
    """
    LLM을 통해 시나리오와 API 목록을 바탕으로 흐름도를 생성하고 시나리오 ID를 기준으로 흐름도 반환
    """
    try:
        flow_agent = ScenarioFlowAgent()
        scenario_flow_chart = await flow_agent.generate_scenario_flow(request)

        # 생성된 플로우 차트를 데이터베이스에 저장
        storage_service = ScenarioFlowStorageService()

        # 각 시나리오별로 플로우 차트 저장
        saved_scenario_ids = []
        for scenario_item in request.scenario_list:
            try:
                # 해당 시나리오의 플로우 차트를 DB에 저장
                saved_id = storage_service.save_scenario_flow(
                    scenario_id=scenario_item.id, flow_chart=scenario_flow_chart.data
                )
                saved_scenario_ids.append(saved_id)
                logging.info(f"시나리오 {scenario_item.id}의 플로우 차트 저장 완료")
            except Exception as e:
                logging.error(
                    f"시나리오 {scenario_item.id}의 플로우 차트 저장 실패: {str(e)}"
                )

        return scenario_flow_chart

    except Exception as e:
        logging.exception("플로우 차트 생성 중 예외 발생")
        raise HTTPException(status_code=500, detail=f"플로우 차트 생성 실패: {str(e)}")
