import logging
from typing import Dict, Any
from ai.service.apilist.state.mapping_state import MappingState

def decision_node(state: MappingState) -> Dict[str, Any]:
    """
    분기 결정 노드
    mapping_validation_node의 결과(점수, 상태)에 따라 다음 단계 결정
    """
    logging.info("분기 결정 노드 시작")
    validation_score = state.get("validation_score", 0.0)
    max_attempts = state.get("max_attempts", 3)
    attempt_count = state.get("attempt_count", 0)
    has_error = state.get("has_error", False)

    # 예시: 점수 80점 이상이면 성공, 아니면 재시도(최대 3회), 에러시 실패
    if has_error:
        state["current_step"] = "failed"
    elif validation_score >= state.get("target_score", 80.0):
        state["current_step"] = "completed"
    elif attempt_count + 1 < max_attempts:
        state["current_step"] = "feedback"
        state["attempt_count"] = attempt_count + 1
    else:
        state["current_step"] = "failed"

    return state