# ai/prompt/testcase/regeneration_prompt.py
from typing import Dict, List
import json
from textwrap import dedent

from dto.request.testcase.api_mapping import ApiMapping
from dto.request.testcase.scenario import Scenario
from dto.response.testcase.testcase_data import TestcaseData
from state.testcase.flow_state import FlowState


def build_regeneration_prompt(state: FlowState) -> str:
    scenario: Scenario = state.request.scenario
    api_mapping_list: List[ApiMapping] = state.request.api_mapping_list
    tc_list: List[TestcaseData] = state.tc_list
    revalidation_targets = state.revalidation_targets

    # 재검토 대상만 필터링
    revalidation_tc_ids = {tc_id for tc_id, _ in revalidation_targets}
    reasons_map: Dict[str, str] = {tc_id: reason for tc_id, reason in revalidation_targets}
    target_tc_list = [tc for tc in tc_list if tc.tc_id in revalidation_tc_ids]

    # 최소 param 정보만 포함 (paramId, name, type, required)
    param_spec_map = {}
    for api in api_mapping_list:
        for param in api.param_list:
            param_spec_map[param.param_id] = {
                "name": param.name,
                "type": param.type,
                "required": param.required
            }

    testcase_summary = []
    for tc in target_tc_list:
        input_data = []
        for p in tc.test_data_list:
            if p.param_id not in param_spec_map:
                raise ValueError(f"[build_regeneration_prompt] paramId {p.param_id} 에 대한 파라미터 정보를 찾을 수 없습니다.")
            input_data.append({
                "paramId": p.param_id,
                "value": p.value,
                "paramSpec": param_spec_map[p.param_id]
            })
        
        testcase_summary.append({
            "tcId": tc.tc_id,
            "precondition": tc.precondition,
            "expected_result": tc.expected_result,
            "status": tc.status,
            "input": input_data,
            "reason": reasons_map.get(tc.tc_id, "")
        })

    prompt = f"""{scenario.scenario_name} 시나리오에 대한 테스트케이스 일부를 재생성하려고 합니다.

[시나리오 설명]
- {scenario.scenario_desc}

[검증 포인트]
- {scenario.validation}

[기존 테스트케이스 요약 및 수정 사유]
다음 테스트케이스 목록과 각 항목의 `reason`을 참고하여 동일 mapping_id, paramId 구조로 새로운 테스트케이스를 생성해주세요. 검증 포인트가 반영되도록 보완이 필요합니다.

[출력 규칙 - 반드시 준수]
- 출력은 JSON 배열 하나만. 마크다운, 코드블럭, 주석 절대 금지.
- 테스트케이스 수·순서는 기존과 동일.
- 필드는 모두 스네이크케이스 (예: tc_id, expected_result).

[필수 필드]
- mapping_id (숫자), tc_id, precondition, description, expected_result, status (2/3/4/5 숫자형), test_data_list

[test_data_list 규칙]
- 각 항목은 paramId는 숫자, value는 문자열 형태여야 함
- 모든 value는 문자열 (숫자도 "123"처럼 작성)
- value는 param의 length를 절대 초과하지 않음
- required 파라미터는 빠짐없이 포함할 것

[precondition 작성]
- '이전' API 응답값을 '현재' API에 연동할 경우 작성
- 형식 예시: step 2:body|userId -> path|userId
- 조건이 2개 이상이면 콤마로 구분

[기존 테스트케이스 목록]
{json.dumps(testcase_summary, ensure_ascii=False, separators=(',', ':'))}
"""

    return dedent(prompt)
