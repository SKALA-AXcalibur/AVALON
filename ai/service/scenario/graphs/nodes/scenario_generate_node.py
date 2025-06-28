# service/scenario/graphs/nodes/scenario_generate_node.py
import logging
from typing import Dict, Any

from service.scenario.agents.scenario_generator import ScenarioGenerator
from service.scenario.state.scenario_state import ScenarioState


def scenario_generate_node(state: ScenarioState) -> Dict[str, Any]:
    """
    시나리오 생성 노드
    """

    try:
        # 상태에서 입력 데이터 추출
        request_data = state.get("request_data")
        feedback_data = state.get("feedback_data")  # 재생성시 피드백 활용
        current_attempt = state.get("attempt_count", 0)  # 시도 횟수

        if not request_data:
            raise ValueError("request_data가 상태에 없습니다.")

        if feedback_data:
            logging.info("이전 피드백을 반영하여 재생성합니다.")

        # 시나리오 생성 실행
        generator = ScenarioGenerator()

        # 피드백이 있으면 포함해서 생성
        if feedback_data:
            scenario_response = generator.generate_scenario_with_feedback(
                request_data, feedback_data
            )
        else:
            scenario_response = generator.generate_scenario_request(request_data)

        logging.info(
            f"시나리오 생성 완료: {len(scenario_response.scenario_list)}개 시나리오"
        )

        # 상태 업데이트
        return {
            "generated_scenarios": scenario_response,
            "current_step": "generation_completed",
            "generation_status": "success",
            "attempt_count": current_attempt + 1,
        }

    except Exception as e:
        logging.exception("시나리오 생성 노드에서 오류 발생")
        error_message = f"시나리오 생성 실패: {str(e)}"
        if "timeout" in str(e).lower():
            error_message += (
                " (LLM API 타임아웃 - 더 간단한 요청으로 다시 시도해보세요)"
            )
        return {
            "generated_scenarios": None,
            "current_step": "generation_failed",
            "generation_status": "failed",
            "error_message": error_message,
        }
