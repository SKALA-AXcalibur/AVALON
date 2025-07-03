# node/scenario/scenario_validate_node.py
import logging
import yaml
import json
import re
from typing import Dict, Any

from service.llm_service import call_model
from state.scenario_state import ScenarioState
from dto.response.scenario.scenario_validation_response import (
    ScoreBasedValidationResponse,
)
from prompt.scenario.scenario_validation_prompt import SCENARIO_VALIDATION_PROMPT


def _build_prompt(scenarios: Any) -> str:
    scenarios_dict = scenarios.model_dump()
    scenarios_yaml = yaml.dump(
        scenarios_dict, default_flow_style=False, allow_unicode=True, sort_keys=False
    )
    return SCENARIO_VALIDATION_PROMPT.format(scenarios=scenarios_yaml)


def _extract_json(text: str) -> dict:
    match = re.search(r"```(?:json)?\s*([\s\S]+?)\s*```", text)
    json_str = match.group(1) if match else text
    try:
        return json.loads(json_str)
    except json.JSONDecodeError as e:
        logging.error(f"[JSON 파싱 실패]")
        raise ValueError("LLM 응답에서 유효한 JSON을 추출하지 못했습니다.") from e


async def scenario_validate_node(state: ScenarioState) -> ScenarioState:
    """
    생성된 시나리오를 검증하고 점수를 매기는 노드
    """
    generated_scenarios = state.get("generated_scenarios")
    if not generated_scenarios:
        raise ValueError("검증할 시나리오가 없습니다.")

    prompt = _build_prompt(generated_scenarios)
    raw_response = await call_model(prompt)
    parsed_json = _extract_json(raw_response)
    validation_result = ScoreBasedValidationResponse(**parsed_json)

    state["validation_result"] = validation_result
    state["current_step"] = "validation_completed"
    return state
