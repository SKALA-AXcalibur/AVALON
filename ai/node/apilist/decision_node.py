import logging
from typing import Dict, Any
from state.apilist.mapping_state import MappingState

def should_regenerate(state: MappingState) -> str:
    """
    검증 결과에 따라 다음 단계 결정
    최대 5회까지만 반복
    """
    validation_score = state.get("validation_score", 0.0)
    target_score = state.get("target_score", 70.0)
    retry_count = state.get("retry_count", 0)
    max_retry = 5

    if validation_score >= target_score:
        return "complete"
    elif retry_count >= max_retry:
        return "complete"
    else:
        return "retry"

def decision_node(state: MappingState) -> Dict[str, Any]:
    """
    분기 결정 노드
    mapping_validation_node의 결과(점수, 상태)에 따라 다음 단계 결정
    """
    logging.info("분기 결정 노드 시작")
    validation_score = state.get("validation_score", 0.0)
    has_error = state.get("has_error", False)

    # 점수 70점 이상이면 성공, 아니면 실패
    if has_error:
        state["current_step"] = "failed"
    elif validation_score >= state.get("target_score", 70.0):
        state["current_step"] = "completed"
    else:
        state["current_step"] = "failed"

    return state