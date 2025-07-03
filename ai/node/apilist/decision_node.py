import logging
import os
from typing import Dict, Any
from dotenv import load_dotenv
from service.apilist.mapping_state_processor import MappingState

# .env 파일 로드
load_dotenv()

def should_regenerate(state: MappingState) -> str:
    """
    검증 결과에 따라 다음 단계 결정
    최대 5회까지만 반복
    """
    validation_score = state.validation_score or 0.0
    target_score = state.target_score or float(os.getenv("TARGET_SCORE"))
    retry_count = state.attempt_count or 0
    max_retry = int(os.getenv("MAX_RETRY"))

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
    validation_score = state.validation_score or 0.0
    has_error = state.has_error or False

    # 점수 70점 이상이면 성공, 아니면 실패
    if has_error:
        return state.model_copy(update={"current_step": "failed"})
    elif validation_score >= (state.target_score or float(os.getenv("TARGET_SCORE"))):
        return state.model_copy(update={"current_step": "completed"})
    else:
        return state.model_copy(update={"current_step": "failed"})