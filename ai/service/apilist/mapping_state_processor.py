from typing import Dict, Any, List
import os
from dotenv import load_dotenv
from state.apilist.mapping_state import MappingState

# .env 파일 로드
load_dotenv()

# 상태 초기화 헬퍼 함수
def create_initial_mapping_state(max_attempts: int = int(os.getenv("MAX_RETRY")), target_score: float = float(os.getenv("TARGET_SCORE"))) -> MappingState:
   """
   초기 상태 생성 헬퍼 함수
   """
   return MappingState(
       max_attempts=max_attempts,
       target_score=target_score,
       attempt_count=0,
       current_step="initialized",
       mapping_status="pending",
       generation_status="pending",
       validation_status="pending",
       validation_score=0.0,
       has_error=False,
   )


# 상태 업데이트 헬퍼 함수들
def update_map_success(state: MappingState, map_result: Dict[str, Any]) -> Dict[str, Any]:
   """의미적 매핑 성공시 상태 업데이트"""
   result = dict(state)  # 기존 state 복사
   result.update({
       "semantic_mapping": map_result,
       "mapping_status": "success",
       "current_step": "map_completed",
   })
   return result


def update_map_failed(state: MappingState, error_msg: str) -> Dict[str, Any]:
   """의미적 매핑 실패시 상태 업데이트"""
   result = dict(state)  # 기존 state 복사
   result.update({
       "semantic_mapping": None,
       "mapping_status": "failed",
       "current_step": "map_failed",
       "error_message": error_msg,
       "has_error": True,
   })
   return result


def update_mapping_generation_success(state: MappingState, mapping_table: List[Dict]) -> Dict[str, Any]:
   """매핑표 생성 성공시 상태 업데이트"""
   result = dict(state)  # 기존 state 복사
   result.update({
       "generated_mapping_table": mapping_table,
       "generation_status": "success",
       "current_step": "mapping_generation_completed",
   })
   return result


def update_mapping_generation_failed(state: MappingState, error_msg: str) -> Dict[str, Any]:
   """매핑표 생성 실패시 상태 업데이트"""
   result = dict(state)  # 기존 state 복사
   result.update({
       "generated_mapping_table": None,
       "generation_status": "failed",
       "current_step": "mapping_generation_failed",
       "error_message": error_msg,
       "has_error": True,
   })
   return result


def update_mapping_validation_success(state: MappingState, validation_result: Dict[str, Any]) -> Dict[str, Any]:
   """매핑표 검증 성공시 상태 업데이트"""
   result = dict(state)  # 기존 state 복사
   result.update({
       "validation_result": validation_result,
       "validation_status": "completed",
       "validation_score": validation_result.get("validation_score", 0.0),
       "current_step": "mapping_validation_completed",
   })
   return result


def update_mapping_validation_failed(state: MappingState, error_msg: str) -> Dict[str, Any]:
   """매핑표 검증 실패시 상태 업데이트"""
   result = dict(state)  # 기존 state 복사
   result.update({
       "validation_result": None,
       "validation_status": "failed",
       "current_step": "mapping_validation_failed",
       "error_message": error_msg,
       "has_error": True,
   })
   return result


def increment_attempt_count(state: MappingState) -> Dict[str, Any]:
   """시도 횟수 증가"""
   result = dict(state)  # 기존 state 복사
   current_count = state.get("attempt_count", 0)
   result.update({"attempt_count": current_count + 1})
   return result


# 상태 검증 함수들
def is_map_completed(state: MappingState) -> bool:
   """의미적 매핑이 완료되었는지 확인"""
   return (
       state.get("mapping_status") == "success"
       and state.get("semantic_mapping") is not None
   )


def is_mapping_generation_completed(state: MappingState) -> bool:
   """매핑표 생성이 완료되었는지 확인"""
   return (
       state.get("generation_status") == "success"
       and state.get("generated_mapping_table") is not None
   )


def is_mapping_validation_completed(state: MappingState) -> bool:
   """매핑표 검증이 완료되었는지 확인"""
   return (
       state.get("validation_status") == "completed"
       and state.get("validation_result") is not None
   ) 