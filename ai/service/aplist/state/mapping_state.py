from typing import Dict, Any, Optional, List, TypedDict
from typing_extensions import NotRequired

class MappingState(TypedDict):
   """
   의미적 매핑 및 매핑표 생성 상태 관리
   LangGraph에서 노드 간 데이터 전달을 위한 상태 정의
   """
   
   # 입력 데이터
   avalon: str  # Redis 토큰
   max_attempts: NotRequired[int]  # 최대 재시도 횟수 (기본값: 3)
   target_score: NotRequired[float]  # 목표 검증 점수 (기본값: 80.0)
   
   # 조회된 기본 데이터
   project_key: NotRequired[Optional[int]]  # 프로젝트 키
   scenarios: NotRequired[Optional[List[Dict]]]  # 시나리오 리스트
   api_lists: NotRequired[Optional[List[Dict]]]  # API 리스트
   parameters: NotRequired[Optional[List[Dict]]]  # 파라미터 리스트
   
   # 의미적 매핑 관련 상태
   semantic_mapping: NotRequired[Optional[Dict[str, Any]]]  # 의미적 연관성 분석 결과
   mapping_status: NotRequired[str]  # 매핑 상태
   
   # 매핑표 생성 관련 상태
   generated_mapping_table: NotRequired[Optional[List[Dict]]]  # 생성된 매핑표
   generation_status: NotRequired[str]  # 생성 상태
   
   # 검증 관련 상태
   validation_result: NotRequired[Optional[Dict[str, Any]]]  # 검증 결과
   validation_status: NotRequired[str]  # 검증 상태
   validation_score: NotRequired[float]  # 검증 점수
   
   # 피드백 관련 상태
   feedback_data: NotRequired[Optional[Dict[str, Any]]]  # 피드백 데이터
   ready_for_regeneration: NotRequired[bool]  # 재생성 준비 완료 여부
   
   # 진행 상태 관리
   current_step: NotRequired[str]  # 현재 단계
   attempt_count: NotRequired[int]  # 현재 시도 횟수
   
   # 최종 결과
   final_mappings: NotRequired[Optional[List[Dict]]]  # DB 저장할 최종 매핑 데이터
   
   # 에러 처리
   error_message: NotRequired[Optional[str]]  # 에러 메시지
   has_error: NotRequired[bool]  # 에러 발생 여부


class FeedbackData(TypedDict):
   """
   검증 결과에서 추출한 피드백 데이터 구조
   """
   validation_score: float  # 검증 점수
   issues: List[Dict[str, str]]  # 이슈 목록
   suggestions: List[Dict[str, str]]  # 개선 제안
   focus_areas: List[str]  # 집중해야 할 영역
   improvement_priority: List[str]  # 개선 우선순위


class WorkflowStatus(TypedDict):
   """
   워크플로우 상태 요약용 타입
   """
   current_step: str  # 현재 단계
   attempt_count: int  # 시도 횟수
   mapping_status: str  # 매핑 상태
   generation_status: str  # 생성 상태
   validation_status: str  # 검증 상태
   validation_score: float  # 검증 점수
   has_error: bool  # 에러 여부
   is_completed: bool  # 완료 여부


# 상태 초기화 헬퍼 함수
def create_initial_mapping_state(avalon: str, max_attempts: int = 3, target_score: float = 80.0) -> MappingState:
   """
   초기 상태 생성 헬퍼 함수
   """
   return MappingState(
       avalon=avalon,
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
   return {
       "semantic_mapping": map_result,
       "mapping_status": "success",
       "current_step": "map_completed",
   }


def update_map_failed(state: MappingState, error_msg: str) -> Dict[str, Any]:
   """의미적 매핑 실패시 상태 업데이트"""
   return {
       "semantic_mapping": None,
       "mapping_status": "failed",
       "current_step": "map_failed",
       "error_message": error_msg,
       "has_error": True,
   }


def update_mapping_generation_success(state: MappingState, mapping_table: List[Dict]) -> Dict[str, Any]:
   """매핑표 생성 성공시 상태 업데이트"""
   return {
       "generated_mapping_table": mapping_table,
       "generation_status": "success",
       "current_step": "mapping_generation_completed",
   }


def update_mapping_generation_failed(state: MappingState, error_msg: str) -> Dict[str, Any]:
   """매핑표 생성 실패시 상태 업데이트"""
   return {
       "generated_mapping_table": None,
       "generation_status": "failed",
       "current_step": "mapping_generation_failed",
       "error_message": error_msg,
       "has_error": True,
   }


def update_mapping_validation_success(state: MappingState, validation_result: Dict[str, Any]) -> Dict[str, Any]:
   """매핑표 검증 성공시 상태 업데이트"""
   return {
       "validation_result": validation_result,
       "validation_status": "completed",
       "validation_score": validation_result.get("score", 0.0),
       "current_step": "mapping_validation_completed",
   }


def update_mapping_validation_failed(state: MappingState, error_msg: str) -> Dict[str, Any]:
   """매핑표 검증 실패시 상태 업데이트"""
   return {
       "validation_result": None,
       "validation_status": "failed",
       "current_step": "mapping_validation_failed",
       "error_message": error_msg,
       "has_error": True,
   }


def update_feedback_prepared(state: MappingState, feedback: FeedbackData) -> Dict[str, Any]:
   """피드백 준비 완료시 상태 업데이트"""
   return {
       "feedback_data": feedback,
       "ready_for_regeneration": True,
       "current_step": "feedback_prepared",
   }


def increment_attempt_count(state: MappingState) -> Dict[str, Any]:
   """시도 횟수 증가"""
   current_count = state.get("attempt_count", 0)
   return {"attempt_count": current_count + 1}


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


def should_continue_mapping_workflow(state: MappingState) -> bool:
   """워크플로우를 계속 진행해야 하는지 확인"""
   return (
       not state.get("has_error", False)
       and state.get("attempt_count", 0) < state.get("max_attempts", 3)
       and state.get("validation_score", 0.0) < state.get("target_score", 80.0)
   )


def get_mapping_workflow_summary(state: MappingState) -> WorkflowStatus:
   """현재 워크플로우 상태 요약"""
   return WorkflowStatus(
       current_step=state.get("current_step", "unknown"),
       attempt_count=state.get("attempt_count", 0),
       mapping_status=state.get("mapping_status", "pending"),
       generation_status=state.get("generation_status", "pending"),
       validation_status=state.get("validation_status", "pending"),
       validation_score=state.get("validation_score", 0.0),
       has_error=state.get("has_error", False),
       is_completed=state.get("current_step", "") in [
           "mapping_validation_completed", "map_failed", "mapping_generation_failed", "mapping_validation_failed"
       ]
   )