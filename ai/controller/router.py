"""
@file controller/router.py
@brief AVALON AI의 FastAPI 라우터 설정
@details 이 모듈은 AVALON AI의 FastAPI 라우터를 설정합니다.
@version 1.0
"""

from fastapi import APIRouter, HTTPException, Response
from fastapi.responses import JSONResponse

from dto.request.scenario.scenario_flow_request import ScenarioFlowRequest
from service.scenario.agents.scenario_flow_agent import ScenarioFlowAgent
from dto.request.scenario.scenario_request import ScenarioRequest
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
    logging.info(f"시나리오 생성 요청 시작 - 프로젝트: {request.project_id}")

    try:
        # LangGraph 워크플로우 실행
        from service.scenario.graphs.scenario_graph import create_scenario_graph
        from service.scenario.state.scenario_state import create_initial_state

        graph = create_scenario_graph()
        initial_state = create_initial_state(request_data=request, max_attempts=3)

        # 워크플로우 실행
        final_state = graph.invoke(initial_state)

        # 최종 결과 로깅 추가
        final_score = final_state.get("overall_score", 0)
        final_status = final_state.get("validation_status", "unknown")
        total_attempts = final_state.get("attempt_count", 1)
        current_step = final_state.get("current_step", "unknown")

        logging.info(f"워크플로우 최종 완료:")
        logging.info(f"   최종 점수: {final_score}점")
        logging.info(f"   최종 상태: {final_status}")
        logging.info(f"   총 시도 횟수: {total_attempts}번")
        logging.info(f"   완료 단계: {current_step}")

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

        # 성공 시 생성된 시나리오 개수도 로깅
        scenario_count = (
            len(generated_scenarios.scenario_list)
            if hasattr(generated_scenarios, "scenario_list")
            else 0
        )
        logging.info(
            f"시나리오 생성 성공 - 프로젝트: {request.project_id}, 생성된 시나리오: {scenario_count}개"
        )

        # ScenarioResponse DTO 반환 (기존과 동일)
        return generated_scenarios

    except HTTPException:
        raise
    except Exception as e:
        logging.exception(
            f"시나리오 생성 중 예외 발생 - 프로젝트: {request.project_id}"
        )
        raise HTTPException(status_code=500, detail=f"Internal server error")


@router.post("/api/scenario/v1/scenario")
async def generate_flow_chart(request: ScenarioFlowRequest):
    """
    LLM을 통해 시나리오와 API 목록을 바탕으로 흐름도를 생성하고 시나리오 ID를 기준으로 흐름도 반환
    """
    flow_agent = ScenarioFlowAgent()
    scenario_flow_chart = await flow_agent.generate_scenario_flow(request)
    return scenario_flow_chart
