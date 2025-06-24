# service/scenario/agents/scenario_validator.py
import json
import logging
import re
import yaml
from typing import Any

from service.llm_service import call_model
from dto.response.scenario.scenario_validation_response import (
    ScenarioValidationResponse,
)
from service.scenario.prompts.scenario_validation_prompt import (
    SCENARIO_VALIDATION_PROMPT,
)


class ScenarioValidator:
    """
    시나리오 검증을 위한 에이전트
    """

    def validate_scenario_only(self, scenarios: Any) -> ScenarioValidationResponse:
        """
        시나리오만으로 검증을 위한 LLM 호출 및 응답 파싱
        """
        try:
            # 프롬프트 생성
            prompt = self._build_simple_prompt(scenarios)
            logging.info("Sending simple validation prompt to LLM...")

            # LLM 호출
            raw_response = self._call_llm(prompt)
            logging.debug(f"Raw LLM Validation Response:\n{raw_response}")

            # JSON 추출
            parsed_json = self._extract_json(raw_response)

            # 응답 반환
            return ScenarioValidationResponse(**parsed_json)
        except Exception as e:
            logging.exception("[ScenarioValidator Error] 시나리오 검증 중 예외 발생")
            raise RuntimeError(f"시나리오 검증 실패: {e}")

    def _build_simple_prompt(self, scenarios: Any) -> str:
        """
        시나리오만으로 검증 프롬프트 생성 (YAML 형태로 변환)
        """
        # Pydantic 모델을 dict로 변환
        scenarios_dict = scenarios.model_dump()

        # YAML로 변환
        scenarios_yaml = yaml.dump(
            scenarios_dict,
            default_flow_style=False,
            allow_unicode=True,
            sort_keys=False,
        )

        return SCENARIO_VALIDATION_PROMPT.format(scenarios=scenarios_yaml)

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
            raise ValueError(
                "Claude 응답에서 유효한 JSON을 추출하지 못했습니다."
            ) from e
