# service/scenario/agents/scenario_generator.py
import json
import logging
import re
from typing import Any, Dict

from service.llm_service import call_model
from dto.response.scenario.scenario_response import ScenarioResponse
from ai.service.scenario.prompts.scenario_prompt import SCENARIO_GENERATION_PROMPT


class ScenarioGenerator:
    """
    시나리오 생성을 위한 에이전트
    LLM을 호출하여 시나리오 데이터를 생성
    """ 

    def generate_scenario_request(self, request: Any) -> ScenarioResponse:
        """
        시나리오 생성을 위한 LLM 호출 및 응답 파싱
        """
        try:
            # 프롬프트 생성
            prompt = self._build_prompt(request)
            logging.info("Sending prompt to LLM...")

            # LLM 호출
            raw_response = self._call_llm(prompt)
            logging.debug(f"Raw LLM Response:\n{raw_response}")

            # JSON 추출
            parsed_json = self._extract_json(raw_response)

            # 응답 반환
            return ScenarioResponse(**parsed_json)
        except Exception as e:
            logging.exception("[ScenarioGenerator Error] 시나리오 생성 중 예외 발생")
            raise RuntimeError(f"시나리오 생성 실패: {e}")

    def generate_scenario_with_feedback(
        self, request: Any, feedback_data: Dict[str, Any]) -> ScenarioResponse:
        """
        피드백을 반영한 시나리오 재생성
        """
        try:
            # 프롬프트 생성
            prompt = self._build_prompt_with_feedback(request, feedback_data)
            logging.info("Sending feedback-enhanced prompt to LLM...")

            # LLM 호출
            raw_response = self._call_llm(prompt)
            logging.debug(f"Raw LLM Response (with feedback):\n{raw_response}")

            # JSON 추출
            parsed_json = self._extract_json(raw_response)

            # 응답 반환
            return ScenarioResponse(**parsed_json)
        except Exception as e:
            logging.exception("[ScenarioGenerator Error] 피드백 반영 시나리오 생성 중 예외 발생")
            raise RuntimeError(f"피드백 반영 시나리오 생성 실패: {e}")

    def _build_prompt(self, request: Any) -> str:
        """
        입력 요청 객체를 기반으로 프롬프트 생성
        """
        input_json = json.dumps(request.model_dump(), ensure_ascii=False, indent=2)
        return SCENARIO_GENERATION_PROMPT.format(data=input_json)

    def _build_prompt_with_feedback(self, request: Any, feedback_data: Dict[str, Any]) -> str:
        """
        피드백을 반영한 프롬프트 생성
        """
        input_json = json.dumps(request.model_dump(), ensure_ascii=False, indent=2)

        # 피드백 내용 추출 (최대 3개까지만 사용)
        issues = [issue.get("issue", "") for issue in feedback_data.get("issues", [])]
        suggestions = [
            s.get("suggestion", "") for s in feedback_data.get("suggestions", [])
        ]

        # 피드백 요약
        feedback_text = "이전 시나리오 개선점:\n"
        feedback_text += "\n".join([f"- {issue}" for issue in issues[:3]])  # 최대 3개만
        feedback_text += "\n" + "\n".join(
            [f"- {suggestion}" for suggestion in suggestions[:3]]
        ) 

        # 피드백 요약 프롬프트 생성
        return f"""
{feedback_text}

위 개선점을 반영하여 시나리오를 다시 생성해주세요.

{SCENARIO_GENERATION_PROMPT.format(data=input_json)}
"""

    def _call_llm(self, prompt: str) -> str:
        """
        LLM 모델 호출
        """
        response = call_model(prompt)
        if isinstance(response, list):
            return "".join(
                part.text if hasattr(part, "text") else str(part) for part in response
            )
        return response

    def _extract_json(self, text: str) -> dict:
        """
        JSON을 추출하여 파싱
        """
        match = re.search(r"```(?:json)?\s*([\s\S]+?)\s*```", text)
        json_str = match.group(1) if match else text

        try:
            return json.loads(json_str)
        except json.JSONDecodeError as e:
            logging.error(f"[JSON 파싱 실패] 원문: {json_str[:200]}...")
            raise ValueError("Claude 응답에서 유효한 JSON을 추출하지 못했습니다.") from e
