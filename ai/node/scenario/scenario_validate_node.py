# node/scenario/scenario_validate_node.py
import logging
import yaml
import json
import re
from typing import Dict, Any

from service.llm_service import call_model
from state.scenario_state import ScenarioState
from dto.response.scenario.scenario_validation_response import ScoreBasedValidationResponse
from prompt.scenario.scenario_validation_prompt import SCENARIO_VALIDATION_PROMPT
from config.scenario_config import LOG_TEXT_LIMIT


def _build_prompt(scenarios: Any) -> str:
    scenarios_dict = scenarios.model_dump()
    scenarios_yaml = yaml.dump(
        scenarios_dict, default_flow_style=False, allow_unicode=True, sort_keys=False
    )
    return SCENARIO_VALIDATION_PROMPT.format(scenarios=scenarios_yaml)


def _call_llm(prompt: str) -> str:
    response = call_model(prompt)
    if isinstance(response, list):
        return "".join(
            part.text if hasattr(part, "text") else str(part) for part in response
        )
    return response


def _extract_json(text: str) -> dict:
    match = re.search(r"```(?:json)?\s*([\s\S]+?)\s*```", text)
    json_str = match.group(1) if match else text
    try:
        return json.loads(json_str)
    except json.JSONDecodeError as e:
        logging.error(f"[JSON 파싱 실패] 원문: {json_str[:LOG_TEXT_LIMIT]}...")
        raise ValueError("LLM 응답에서 유효한 JSON을 추출하지 못했습니다.") from e


def scenario_validate_node(state: ScenarioState) -> ScenarioState:
    """
    생성된 시나리오를 검증하고 점수를 매기는 노드 (에이전트 로직 통합)
    """
    try:
        generated_scenarios = state.get("generated_scenarios")
        if not generated_scenarios:
            raise ValueError("검증할 시나리오가 없습니다.")

        prompt = _build_prompt(generated_scenarios)
        raw_response = _call_llm(prompt)
        parsed_json = _extract_json(raw_response)
        validation_result = ScoreBasedValidationResponse(**parsed_json)

        state["validation_result"] = validation_result
        state["current_step"] = "validation_completed"
        return state

    except Exception as e:
        logging.exception("시나리오 검증 노드에서 오류 발생")
        error_message = f"시나리오 검증 실패: {str(e)}"
        state["current_step"] = "validation_failed"
        state["error_message"] = error_message
        return state
