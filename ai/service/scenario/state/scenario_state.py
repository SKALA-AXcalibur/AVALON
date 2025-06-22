# service/scenario/state/scenario_state.py
from typing import Dict, Any, Optional, List, TypedDict
from typing_extensions import NotRequired

from dto.response.scenario.scenario_response import ScenarioResponse
from dto.response.scenario.scenario_validation_response import ScenarioValidationResponse


class ScenarioState(TypedDict):
    """
    시나리오 생성 상태 관리
    LangGraph에서 노드 간 데이터 전달을 위한 상태 정의
    """
    
    # 입력 데이터
    request_data: Any  # 초기 요청 데이터 (요구사항, API 리스트 등)
    max_attempts: NotRequired[int]  # 최대 재시도 횟수 (기본값: 3)
    
    # 생성 관련 상태
    generated_scenarios: NotRequired[Optional[ScenarioResponse]]  # 생성된 시나리오
    generation_status: NotRequired[str]  # 생성 상태
    
    # 검증 관련 상태  
    validation_result: NotRequired[Optional[ScenarioValidationResponse]]  # 검증 결과
    validation_status: NotRequired[str]  # 검증 상태
    overall_score: NotRequired[int]  # 점수
    
    # 피드백 관련 상태
    feedback_data: NotRequired[Optional[Dict[str, Any]]]  # 다음 생성에 활용할 피드백
    ready_for_regeneration: NotRequired[bool]  # 재생성 준비 완료 여부
    
    # 진행 상태 관리
    current_step: NotRequired[str]  # 현재 단계
    attempt_count: NotRequired[int]  # 현재 시도 횟수
    
    # 에러 처리
    error_message: NotRequired[Optional[str]]  # 에러 메시지
    has_error: NotRequired[bool]  # 에러 발생 여부


class FeedbackData(TypedDict):
    """
    검증 결과에서 추출한 피드백 데이터 구조
    """
    overall_score: int # 점수
    issues: List[Dict[str, str]] # 이슈
    suggestions: List[Dict[str, str]] # 제안
    focus_areas: List[str] # 초점 영역
    improvement_priority: List[str] # 개선 우선순위


class WorkflowStatus(TypedDict):
    """
    워크플로우 상태 요약용 타입
    """
    current_step: str # 현재 단계
    attempt_count: int # 시도 횟수
    generation_status: str # 생성 상태
    validation_status: str # 검증 상태
    overall_score: int # 점수
    has_error: bool # 에러 여부
    is_completed: bool # 완료 여부


# 상태 초기화 헬퍼 함수
def create_initial_state(request_data: Any, max_attempts: int = 3) -> ScenarioState:
    """
    초기 상태 생성 헬퍼 함수
    """
    return ScenarioState(
        request_data=request_data,
        max_attempts=max_attempts,
        attempt_count=0,
        current_step="initialized",
        generation_status="pending",
        validation_status="pending",
        overall_score=0,
        has_error=False
    )


# 상태 업데이트 헬퍼 함수들
def update_generation_success(state: ScenarioState, scenarios: ScenarioResponse) -> Dict[str, Any]:
    """생성 성공시 상태 업데이트"""
    return {
        "generated_scenarios": scenarios,
        "generation_status": "success",
        "current_step": "generation_completed"
    }


def update_generation_failed(state: ScenarioState, error_msg: str) -> Dict[str, Any]:
    """생성 실패시 상태 업데이트"""
    return {
        "generated_scenarios": None,
        "generation_status": "failed",
        "current_step": "generation_failed",
        "error_message": error_msg,
        "has_error": True
    }


def update_validation_success(state: ScenarioState, validation: ScenarioValidationResponse) -> Dict[str, Any]:
    """검증 성공시 상태 업데이트"""
    return {
        "validation_result": validation,
        "validation_status": validation.validation_result.overall_status,
        "overall_score": validation.validation_result.overall_score,
        "current_step": "validation_completed"
    }


def update_validation_failed(state: ScenarioState, error_msg: str) -> Dict[str, Any]:
    """검증 실패시 상태 업데이트"""
    return {
        "validation_result": None,
        "validation_status": "failed",
        "current_step": "validation_failed",
        "error_message": error_msg,
        "has_error": True
    }


def update_feedback_prepared(state: ScenarioState, feedback: FeedbackData) -> Dict[str, Any]:
    """피드백 준비 완료시 상태 업데이트"""
    return {
        "feedback_data": feedback,
        "ready_for_regeneration": True,
        "current_step": "feedback_prepared"
    }


def increment_attempt_count(state: ScenarioState) -> Dict[str, Any]:
    """시도 횟수 증가"""
    current_count = state.get("attempt_count", 0)
    return {
        "attempt_count": current_count + 1
    }


# 상태 검증 함수들
def is_generation_completed(state: ScenarioState) -> bool:
    """생성이 완료되었는지 확인"""
    return (
        state.get("generation_status") == "success" and 
        state.get("generated_scenarios") is not None
    )


def is_validation_completed(state: ScenarioState) -> bool:
    """검증이 완료되었는지 확인"""
    return (
        state.get("validation_status") in ["pass", "review_required", "failed"] and
        state.get("validation_result") is not None
    )


def should_continue_workflow(state: ScenarioState) -> bool:
    """워크플로우를 계속 진행해야 하는지 확인"""
    return (
        not state.get("has_error", False) and
        state.get("attempt_count", 0) < state.get("max_attempts", 3) and
        state.get("overall_score", 0) < 80
    )


# def get_workflow_summary(state: ScenarioState) -> WorkflowStatus:
#     """현재 워크플로우 상태 요약"""
#     return WorkflowStatus(
#         current_step=state.get("current_step", "unknown"),
#         attempt_count=state.get("attempt_count", 0),
#         generation_status=state.get("generation_status", "pending"),
#         validation_status=state.get("validation_status", "pending"),
#         overall_score=state.get("overall_score", 0),
#         has_error=state.get("has_error", False),
#         is_completed=state.get("current_step", "") in [
#             "validation_completed", "generation_failed", "validation_failed"
#         ]
    # )