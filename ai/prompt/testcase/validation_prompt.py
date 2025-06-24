# ai/prompt/testcase/validation_prompt.py

import json

from state.testcase.flow_state import FlowState

def build_validation_prompt(state: FlowState) -> str:
    """
    TC 검증 프롬프트 제작 함수
    시나리오의 검증 조건과 생성된 TC 목록을 기반으로, LLM에게 각 TC가 조건을 만족하는지 확인하도록 프롬프트 생성
    """
    # 최소화된 param 정보
    api_summaries = []
    for api in state.request.api_mapping_list:
        api_summary = {
            "step": api.step,
            "mapping_id": api.mapping_id,
            "param_list": [
                {
                    "param_id": param.param_id,
                    "name": param.name,
                    "type": param.type,
                    "length": param.length,
                    "required": param.required
                }
                for param in api.param_list
            ]
        }
        api_summaries.append(api_summary)

    # 테스트케이스 요약
    testcase_summary = [
        {   
            "mapping_id": tc.mapping_id,
            "tc_id": tc.tc_id,
            "precondition": tc.precondition,
            "expected_result": tc.expected_result,
            "status": tc.status,
            "input": [{"param_id": p.param_id, "value": p.value} for p in tc.test_data_list]
        }
        for tc in state.tc_list
    ]

    prompt = f"""
다음은 시나리오의 검증 조건입니다:
"{state.request.scenario.validation}"

아래는 API 파라미터 정보와 생성된 테스트케이스 목록입니다.  
각 테스트케이스가 아래 조건을 **충분히 만족하는지** 판단해주세요.

[검토 기준]
1. 파라미터의 입력값(value), expected_result, status 코드 간 의미적 정합성이 있는가?
2. mapping_id, param_id 값이 실제 API/파라미터 정보와 일치하는가?
3. 각 value가 해당 param의 데이터 타입(type)과 일치하는가?
4. required 여부가 Y인 파라미터를 누락했는가?
5. 시나리오 검증 포인트가 충분히 반영되었는가?

- 각 항목에 대해 다음 형식 중 하나로 구성된 **리스트**를 반환해주세요:
  - 조건을 만족하면: `True`
  - 조건을 명백히 불충분하면: `(False, "간단한 사유")`
- 테스트케이스는 하나의 API에 대한 테스트입니다.  
  - 시나리오 전체 검증 조건을 **해당 API 관점에서 부분적으로** 만족하면 충분합니다.  
  - 다른 API와 연계된 조건은 해당 API의 TC에서 따로 확인합니다.
- 리스트의 **순서는 테스트케이스 순서와 반드시 일치해야 합니다.**
- `True`를 반환할 때는 **절대로 설명을 추가하지 마세요.**
- 사소한 표현 차이는 무시하고, **정말 중요한 조건 누락만 False**로 판단해주세요.

[응답 예시]
[True, (False, "파라미터 ID 불일치"), True, True]

[API 파라미터 정보]
{json.dumps(api_summaries, ensure_ascii=False, separators=(',', ':'))}

[테스트케이스 목록]
{json.dumps(testcase_summary, ensure_ascii=False, separators=(',', ':'))}
    """.strip()

    return prompt