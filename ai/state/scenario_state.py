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
    request_data: ScenarioRequest

    # --- 워크플로우 제어 ---
    attempt_count: int
    max_attempts: int

    # --- 노드 간 전달 데이터 ---
    generated_scenarios: Optional[ScenarioResponse]
    validation_result: Optional[ScoreBasedValidationResponse]
    feedback_data: Optional[Dict[str, Any]]

    # --- 로깅 및 에러 핸들링 ---
    error_message: Optional[str]
    current_step: str
    generation_status: str
    next_step: Optional[str]  # decision_node에서 설정하는 다음 단계


def create_initial_state(request: ScenarioRequest) -> ScenarioState:
    """초기 상태 딕셔너리를 생성합니다."""
    return ScenarioState(
        request_data=request,
        attempt_count=0,
        max_attempts=MAX_RETRIES,
        generated_scenarios=None,
        validation_result=None,
        feedback_data=None,
        error_message=None,
        current_step="initial",
        generation_status="not_started",
        next_step=None,
    )
