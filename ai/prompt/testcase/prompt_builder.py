
from typing import List
from textwrap import dedent

from dto.request.testcase.api_mapping import ApiMapping
from dto.request.testcase.scenario import Scenario

def build_prompt(api_mapping_list: List[ApiMapping], scenario: Scenario) -> str:
    """
    TC 생성 프롬프트 제작 함수
    시나리오와 시나리오에 매핑된 API들의 파라미터 정보를 기반으로 프롬프트를 생성합니다.
    """
    prompt = f"""{scenario.scenario_name} 시나리오에 대한 테스트케이스 생성을 도와주세요.
[시나리오 정보]
- 시나리오명: {scenario.scenario_name}
- 시나리오 설명: {scenario.scenario_desc}
- 검증 포인트: {scenario.validation}

[시나리오에 포함된 API 목록 및 상세 정보]
"""
    for api in api_mapping_list:
        prompt += f"""\n▶ API Step {api.step}: {api.name or '(이름 없음)'} (mapping_id: {api.mapping_id})
- 설명: {api.desc}
- HTTP Method: {api.method}
- URL Path: {api.path}
  [요청 파라미터]
"""
        if not api.param_list:
            prompt += "  (없음)\n"
        for param in api.param_list:
            prompt += f"  - {param.name} ({param.type}, 길이={param.length}) | 위치: {param.context} | 필수: {'Y' if param.required else 'N'} | 설명: {param.desc}\n"

    prompt += dedent("""
[테스트케이스 생성 조건]
1. 정상 / 경계값 / 비정상 3가지 유형으로 작성(각 유형 별 하나씩만 생성한다.)
2. 반드시 시나리오의 검증 포인트를 고려하여 생성.
3. 현재 API가 이전 API와 이어지는 흐름이라면, 응답값을 다음 API의 입력으로 연동.
    - 동적 값(ex. token 등)은 precondition에 어떤 API 응답에서 획득했는지 아래 예시와 같이 명시.
    예시: (현재 API 이름)의 (파라미터 이름)에는 (이전 API 이름)의 (파라미터 이름) 값을 사용한다.
4. 테스트케이스에는 예상되는 status 코드 (2, 3, 4, 5 중 하나)를 포함하세요. 예를 들어, 200번대 응답이 예상된다면 2를, 400번대 응답이 예상된다면 4를 반환합니다.
5. tc_id는 unique해야 하며, 형식은 TC-<API영문이름>-<케이스유형>-<일련번호> 로 구성하세요. 일련번호는 3자리로 구성합니다.

[출력 형식]
- 오직 JSON 문자열만 반환하세요. 추가 설명, 마크다운, 코드 블럭 등은 절대 포함하지 마세요.
- `.repeat()`, 문자열 연결(`+`), `//` 주석 등 JavaScript 문법은 절대 사용하지 마세요.
- "value"에는 실제 값을 그대로 작성하세요. 예) `"value": "aaaaaaaaaa"` (50자면 실제로 50자 반복)

예외가 발생하면 응답을 버리게 되니 반드시 위 기준을 지키세요.
                     
```json
[
    {
    "mapping_id": 1,
    "tc_id": "TC-UserCreate-정상-001",
    "precondition": "사용자 등록 API를 통해 userId를 획득해야 함",
    "description": "정상 입력에 대한 사용자 조회 테스트",
    "expected_result": "조회된 사용자 정보가 입력과 일치하며, password는 노출되지 않아야 함",
    "status": 2,
    "test_data_list": [
        {
        "param": {
            "category": "path/query",
            "koName": "사용자ID",
            "name": "userId",
            "context": "path",
            "type": "varchar",
            "length": 20,
            "required": true,
            "desc": "조회할 사용자 ID"
        },
        "value": "abc123"
        }, ...
    ]
    },
    { "mapping_id": 1, "tc_id": "TC-UserCreate-경계-002", ... }
]
```
    """)

    return prompt
