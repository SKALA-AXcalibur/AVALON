# node/scenario/scenario_generate_node.py
import logging
import yaml
import json
import re
from typing import Dict, Any

from service.llm_service import call_model
from dto.request.scenario.scenario_request import ScenarioRequest
from dto.response.scenario.scenario_response import ScenarioResponse
from state.scenario_state import ScenarioState
from prompt.scenario.scenario_prompt import SCENARIO_GENERATION_PROMPT
from config.scenario_config import ERROR_LOG_TEXT_LIMIT


def _build_prompt_with_feedback(
    request: ScenarioRequest, feedback_data: Dict[str, Any]
) -> str:
    request_dict = request.model_dump()
    yaml_data = yaml.dump(
        request_dict, default_flow_style=False, allow_unicode=True, sort_keys=False
    )

    explanations = feedback_data.get("issues", [])

    feedback_text = "이전 시나리오 생성 시 다음과 같은 피드백이 있었습니다. 이 내용을 참고하여 시나리오를 개선해주세요:\n\n"
    feedback_text += "\n".join(f"- {exp}" for exp in explanations if exp)

    return f"""{feedback_text}

----------
위 피드백을 반영하여 아래 데이터에 대한 시나리오를 다시 생성해주세요.

{SCENARIO_GENERATION_PROMPT.format(data=yaml_data)}"""


def _build_prompt(request: ScenarioRequest) -> str:
    request_dict = request.model_dump()
    yaml_data = yaml.dump(
        request_dict, default_flow_style=False, allow_unicode=True, sort_keys=False
    )
    return SCENARIO_GENERATION_PROMPT.format(data=yaml_data)


def _call_llm(prompt: str) -> str:
    response = call_model(prompt)
    if isinstance(response, list):
        return "".join(
            part.text if hasattr(part, "text") else str(part) for part in response
        )
    return response


def _extract_json(text: str) -> dict:
    # 1. 먼저 ```json 블록을 찾아보기
    match = re.search(r"```(?:json)?\s*([\s\S]+?)\s*```", text)
    if match:
        json_str = match.group(1).strip()
    else:
        # 2. ```json 블록이 없으면 { }로 둘러싸인 JSON 찾기
        match = re.search(r"\{[\s\S]*\}", text)
        if match:
            json_str = match.group(0).strip()
        else:
            # 3. 마지막으로 전체 텍스트에서 시도
            json_str = text.strip()

    try:
        return json.loads(json_str)
    except json.JSONDecodeError as e:
        # JSON이 불완전할 수 있으므로 더 자세한 로그 출력
        logging.error(f"[JSON 파싱 실패] JSON 문자열: {json_str}")
        logging.error(
            f"[JSON 파싱 실패] 원본 응답: {text[:ERROR_LOG_TEXT_LIMIT]}..."
        )
        raise ValueError(
            f"LLM 응답에서 유효한 JSON을 추출하지 못했습니다. JSON 오류: {str(e)}"
        ) from e


def scenario_generate_node(state: ScenarioState) -> ScenarioState:
    """
    시나리오 생성 노드 (에이전트 로직 통합)
    """
    try:
        if not state.get("request_data"):
            raise ValueError("request_data가 상태에 없습니다.")

        feedback_data = state.get("feedback_data")
        if feedback_data:
            logging.info("이전 피드백을 반영하여 재생성합니다.")
            prompt = _build_prompt_with_feedback(state["request_data"], feedback_data)
        else:
            prompt = _build_prompt(state["request_data"])

        raw_response = _call_llm(prompt)

        parsed_json = _extract_json(raw_response)
        scenario_response = ScenarioResponse(**parsed_json)

        logging.info(
            f"시나리오 생성 완료: {len(scenario_response.scenario_list)}개 시나리오"
        )

        state["generated_scenarios"] = scenario_response
        state["feedback_data"] = None
        state["current_step"] = "generation_completed"
        state["generation_status"] = "success"
        state["attempt_count"] = state.get("attempt_count", 0) + 1
        return state

    except Exception as e:
        logging.exception("시나리오 생성 노드에서 오류 발생")
        error_message = f"시나리오 생성 실패: {str(e)}"
        state["generated_scenarios"] = None
        state["current_step"] = "generation_failed"
        state["generation_status"] = "failed"
        state["error_message"] = error_message
        return state
