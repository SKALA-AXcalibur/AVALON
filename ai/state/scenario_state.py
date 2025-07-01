from typing import Optional, Dict, Any, TypedDict

from dto.request.scenario.scenario_request import ScenarioRequest
from dto.response.scenario.scenario_response import ScenarioResponse
from dto.response.scenario.scenario_validation_response import ScoreBasedValidationResponse

from config.scenario_config import MAX_RETRIES


class ScenarioState(TypedDict, total=False):
    """
    시나리오 생성 워크플로우의 상태를 정의하는 딕셔너리 상태
    """

    # --- 입력 데이터 ---
    request_data: ScenarioRequest # 사용자 입력 데이터

    # --- 워크플로우 제어 ---
    attempt_count: int # 재시도 횟수
    max_attempts: int # 최대 재시도 횟수

    # --- 노드 간 전달 데이터 ---
    generated_scenarios: Optional[ScenarioResponse] # 생성된 시나리오 데이터
    validation_result: Optional[ScoreBasedValidationResponse] # 검증 결과 데이터
    feedback_data: Optional[Dict[str, Any]] # 피드백 데이터

    # --- 로깅 및 에러 핸들링 ---
    error_message: Optional[str] # 에러 메시지
    current_step: str
    generation_status: str
    next_step: Optional[str]  # decision_node에서 설정하는 다음 단계


def create_initial_state(request: ScenarioRequest) -> ScenarioState:
    """초기 상태 딕셔너리를 생성합니다."""
    return ScenarioState(
        request_data=request,           # 사용자 입력 데이터
        attempt_count=0,                # 재시도 횟수
        max_attempts=MAX_RETRIES,       # 최대 재시도 횟수
        generated_scenarios=None,        # 생성된 시나리오 데이터
        validation_result=None,         # 검증 결과 데이터
        feedback_data=None,             # 피드백 데이터
        error_message=None,             # 에러 메시지
        current_step="initial",         # 현재 단계
        generation_status="not_started", # 생성 상태
        next_step=None,                  # 다음 단계
    )
