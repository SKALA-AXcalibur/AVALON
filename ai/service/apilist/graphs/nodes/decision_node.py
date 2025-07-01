import logging
from typing import Dict, Any
from service.apilist.state.mapping_state import MappingState

def should_regenerate(state: MappingState) -> str:
    """
    검증 결과에 따라 다음 단계 결정
    """
    validation_score = state.get("validation_score", 0.0)
    target_score = state.get("target_score", 80.0)
    has_error = state.get("has_error", False)

    if has_error:
        return "failed"
    elif validation_score >= target_score:
        return "complete"
    else:
        return "failed"

def decision_node(state: MappingState) -> Dict[str, Any]:
    """
    분기 결정 노드
    mapping_validation_node의 결과(점수, 상태)에 따라 다음 단계 결정
    """
    logging.info("분기 결정 노드 시작")
    validation_score = state.get("validation_score", 0.0)
    has_error = state.get("has_error", False)

    # 점수 80점 이상이면 성공, 아니면 실패
    if has_error:
        state["current_step"] = "failed"
    elif validation_score >= state.get("target_score", 80.0):
        state["current_step"] = "completed"
    else:
        state["current_step"] = "failed"

    return state