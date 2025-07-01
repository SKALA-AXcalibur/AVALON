# node/scenario/decision_node.py
import logging
from state.scenario_state import ScenarioState
from config.scenario_config import RETRY_THRESHOLD, MAX_RETRIES


def decision_node(state: ScenarioState) -> ScenarioState:
    """
    검증 결과를 바탕으로 다음 단계를 결정하는 노드
    """
    next_step = "end"  # 기본값

    error_message = state.get("error_message")
    if error_message:
        logging.error(f"오류 상태 감지: {error_message}")
        next_step = "end"
    else:
        validation_result = state.get("validation_result")
        if not validation_result:
            logging.warning("검증 결과가 없어 워크플로우를 종료합니다.")
            next_step = "end"
        else:
            score = validation_result.score
            explanation = validation_result.explanation
            logging.info(f"검증 점수: {score}, 재시도 임계값: {RETRY_THRESHOLD}")

            if score < RETRY_THRESHOLD:
                attempt_count = state.get("attempt_count", 0)
                if attempt_count >= MAX_RETRIES:
                    logging.warning(
                        f"최대 재시도 횟수({MAX_RETRIES})에 도달하여 종료합니다."
                    )
                    state["error_message"] = "최대 재시도 횟수 초과"
                    next_step = "end"
                else:
                    logging.info("점수가 낮아 시나리오 재생성을 결정합니다.")
                    state["feedback_data"] = {"issues": [explanation]}
                    next_step = "regenerate"
            else:
                logging.info("점수가 충분하여 워크플로우를 성공적으로 종료합니다.")
                next_step = "end"

    # 상태에 next_step 설정하고 상태 반환
    state["next_step"] = next_step
    return state
