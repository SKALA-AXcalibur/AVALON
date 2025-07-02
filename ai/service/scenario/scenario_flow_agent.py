# service/scenario/scenario_flow_agent.py
import json
import re
import logging
import yaml

from dto.request.scenario.scenario_flow_request import ScenarioFlowRequest
from dto.response.scenario.scenario_flow_response import ScenarioFlowResponse
from service.llm_service import call_model
from prompt.scenario.scenario_flow_prompt import SCENARIO_FLOW_PROMPT


class ScenarioFlowAgent:
    """
    시나리오 흐름도를 생성하는 에이전트
    LLM을 호출하여 Mermaid 형식의 str 생성
    """

    async def generate_scenario_flow(
        self, request: ScenarioFlowRequest
    ) -> tuple[ScenarioFlowResponse, dict]:
        """
        주어진 시나리오 요청 데이터를 바탕으로 모든 시나리오의 Mermaid 흐름도를 한 번에 생성
        응답용 합친 데이터와 저장용 개별 데이터를 함께 반환
        """
        try:
            # 모든 시나리오를 한 번에 처리
            prompt = self._build_prompt(request)
            raw_response = await call_model(prompt)

            # 응답에서 시나리오별로 플로우차트 파싱
            individual_flow_charts = self._parse_flow_charts(
                raw_response, request.scenario_list
            )

            # 응답용 합친 데이터 생성
            flow_charts = []
            for scenario_id, flow_chart in individual_flow_charts.items():
                flow_charts.append(
                    f"=== 시나리오 {scenario_id} ===\n```mermaid\n{flow_chart}\n```"
                )

            combined_flow_chart = "\n\n".join(flow_charts)
            response = ScenarioFlowResponse(data=combined_flow_chart)

            return response, individual_flow_charts

        except Exception as e:
            logging.exception("[ScenarioFlowAgent Error] 흐름도 생성 중 예외 발생")
            raise RuntimeError(f"시나리오 흐름도 생성 실패")

    def _parse_flow_charts(self, raw_response: str, scenario_list) -> dict:
        """
        LLM 응답에서 시나리오별 플로우차트를 파싱
        """
        individual_flow_charts = {}

        try:
            # 시나리오 ID별로 구분하여 파싱
            for scenario_item in scenario_list:
                scenario_id = scenario_item.id

                # 해당 시나리오 섹션 찾기
                pattern = (
                    rf"=== {re.escape(scenario_id)} ===\s*```mermaid\s*(.*?)\s*```"
                )
                match = re.search(pattern, raw_response, re.DOTALL)

                if match:
                    individual_flow_charts[scenario_id] = match.group(1).strip()
                else:
                    # 패턴 매칭 실패시 기본 플로우차트 생성
                    logging.warning(f"시나리오 {scenario_id}의 플로우차트 파싱 실패")
                    individual_flow_charts[scenario_id] = (
                        self._create_default_flow_chart(scenario_item)
                    )

        except Exception as e:
            logging.error(f"플로우차트 파싱 중 오류")
            # 파싱 실패시 각 시나리오별로 기본 플로우차트 생성
            for scenario_item in scenario_list:
                individual_flow_charts[scenario_item.id] = (
                    self._create_default_flow_chart(scenario_item)
                )

        return individual_flow_charts

    def _create_default_flow_chart(self, scenario_item) -> str:
        """
        파싱 실패시 기본 플로우차트 생성
        """
        if not scenario_item.api_list:
            return "flowchart LR\n    Start --> End"

        # 모든 API를 순차적으로 연결
        flow_chart = f"flowchart LR\n    Start --> A0[{scenario_item.api_list[0].id}: {scenario_item.api_list[0].name}]\n"

        for i in range(1, len(scenario_item.api_list)):
            api = scenario_item.api_list[i]
            flow_chart += f"    A{i-1} --> A{i}[{api.id}: {api.name}]\n"

        # 마지막 API에서 End로 연결
        flow_chart += f"    A{len(scenario_item.api_list)-1} --> End"

        return flow_chart

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
