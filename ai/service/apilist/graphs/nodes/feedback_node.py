import logging
from typing import Dict, Any
from service.aplist.state.mapping_state import MappingState, update_feedback_prepared

def feedback_node(state: MappingState) -> Dict[str, Any]:
    """
    피드백 생성 노드
    mapping_validation_node의 결과를 바탕으로 피드백 생성
    """
    logging.info("피드백 노드 시작")
    validation_result = state.get("validation_result")
    if not validation_result:
        # 피드백 생성 실패 처리
        state["ready_for_regeneration"] = False
        state["current_step"] = "feedback_failed"
        return state

    # TODO: 실제 피드백 생성 로직 (임시로 validation_result를 feedback_data로 사용)
    feedback = {
        "validation_score": validation_result.get("score", 0.0),
        "issues": validation_result.get("issues", []),
        "suggestions": validation_result.get("suggestions", []),
        "focus_areas": validation_result.get("focus_areas", []),
        "improvement_priority": validation_result.get("improvement_priority", [])
    }

    return update_feedback_prepared(state, feedback)
