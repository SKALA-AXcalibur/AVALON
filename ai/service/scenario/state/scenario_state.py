# service/scenario/state/scenario_state.py
from typing import Any, Optional, TypedDict
from urllib import request

from dto.response.scenario.scenario_response import ScenarioResponse
from dto.response.scenario.scenario_validation_response import (
    ScenarioValidationResponse,
)


class ScenarioState(TypedDict):
    """
    LangGraph 워크플로우 전체에서 공유하는 상태 구조 (실제 사용하는 필드만 유지)
    """

    request_data: Any
    attempt_count: int
    max_attempts: int
    generated_scenarios: Optional[ScenarioResponse]
    generation_status: str
    validation_result: Optional[ScenarioValidationResponse]
    validation_status: str
    overall_score: int
    error_message: Optional[str]
    has_error: bool
