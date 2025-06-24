# service/scenario/agents/scenario_flow_agent.py
import json
import re
import logging
import yaml

from dto.request.scenario.scenario_flow_request import ScenarioFlowRequest
from dto.response.scenario.scenario_flow_response import ScenarioFlowResponse
from service.llm_service import call_model
from service.scenario.prompts.scenario_flow_prompt import SCENARIO_FLOW_PROMPT


class ScenarioFlowAgent:
    """
    시나리오 흐름도를 생성하는 에이전트
    LLM을 호출하여 Mermaid 형식의 str 생성
    """

    async def generate_scenario_flow(
        self, request: ScenarioFlowRequest
    ) -> ScenarioFlowResponse:
        """
        주어진 시나리오 요청 데이터를 바탕으로 Mermaid 흐름도를 반환
        """
        try:
            prompt = self._build_prompt(request)
            logging.info("[ScenarioFlowAgent] LLM 프롬프트 생성 완료")

            raw_response = self._call_llm(prompt)
            logging.debug(f"[ScenarioFlowAgent] LLM 응답 수신:\n{raw_response}")

            mermaid_code = self._extract_mermaid(raw_response)
            return ScenarioFlowResponse(data=mermaid_code)
        except Exception as e:
            logging.exception("[ScenarioFlowAgent Error] 흐름도 생성 중 예외 발생")
            raise RuntimeError(f"시나리오 흐름도 생성 실패: {e}")

    def _build_prompt(self, request: ScenarioFlowRequest) -> str:
        """
        시나리오 요청 데이터를 YAML 문자열로 직렬화
        """
        # Pydantic 모델을 dict로 변환
        request_dict = request.model_dump(by_alias=True)

        # YAML로 변환
        scenario_input = yaml.dump(
            request_dict, default_flow_style=False, allow_unicode=True, sort_keys=False
        )

        return SCENARIO_FLOW_PROMPT.format(data=scenario_input)

    def _call_llm(self, prompt: str) -> str:
        """
        LLM 모델 호출
        """
        return call_model(prompt)

    def _extract_mermaid(self, text: str) -> str:
        """
        Mermaid 코드블럭을 추출하여 반환
        """
        match = re.search(r"```mermaid\s*([\s\S]+?)\s*```", text)
        if match:
            return match.group(1).strip()
        logging.warning(
            "[ScenarioFlowAgent] Mermaid 코드블럭 추출 실패, 전체 응답 반환"
        )
        return text.strip()
