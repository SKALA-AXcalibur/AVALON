
from typing import List
from textwrap import dedent
from uuid import uuid4

from dto.request.testcase.api_mapping import ApiMapping
from dto.request.testcase.scenario import Scenario

def build_prompt(api_mapping_list: List[ApiMapping], scenario: Scenario) -> str:
    uuid_suffix = str(uuid4())[:8]  # 예: 'f1a2c3b4'

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
            prompt += f"  - ID: {param.param_id} | {param.name} | 한글명({param.ko_name} ({param.type}, 길이={param.length}, category={param.category}) | context {param.context} | 필수: {'Y' if param.required else 'N'} | 상위 항목명: {param.parent} | 설명: {param.desc}\n"

    prompt += dedent("""
[테스트케이스 생성 조건]
1. 정상 / 경계값 / 비정상 3가지 유형으로 작성(각 유형 별 하나씩만 생성한다.)
2. 반드시 시나리오의 검증 포인트를 고려하여 생성.
3. 현재 API가 이전 API와 이어지는 흐름이라면, 응답값을 다음 API의 입력으로 연동.
    - 동적 값(ex. token 등)은 precondition에 어떤 API 응답에서 획득했는지 아래 예시와 같이 명시.
    예시: (현재 API 이름)의 (파라미터 이름)에는 (이전 API 이름)의 (파라미터 이름) 값을 사용함
4. 배열(array) 또는 객체(object) 형태의 파라미터가 포함된 경우, 내부 항목에는 해당 상위 항목명을 'parent' 필드로 명확히 작성
    - 주어진 paramList에 'parent' 항목 정보가 포함되어 있으므로 동일하게 반영한다.
5. 테스트케이스에는 예상되는 status 코드 (2, 3, 4, 5 중 하나)를 포함하여 작성. 예를 들어, 200번대 응답이 예상된다면 2를, 400번대 응답이 예상된다면 4를 반환
6. tc_id는 unique해야 하며, 형식은 TC-<API영문이름>-<케이스유형>-<일련번호>-<%UUID%> 로 구성한다. 일련번호는 3자리로 구성한다.
7. `category` 값은 path/query, request, response 중 하나이며 paramList에서 받은 값을 **변경 없이 그대로 사용**합니다.
   (예: category 가 "request" 이면 출력도 "request")
8. 각 파라미터에는 paramId 항목도 포함되어야 하며, paramList 내 param_id 값을 그대로 반영한다.

※ 유의사항
- 각 value는 실제 사용 가능한 자연스러운 예시로 작성하세요.
- 다음과 같은 잘못된 예시는 **절대** 사용하지 마세요:
    - 반복 문자열: "Aa1!Aa1!Aa1!..." ← 절대 금지
    - 의미 없는 문자 나열: "xxxxxxxxxxxxxxxxxx", "123123123..."
- 최대 길이 테스트가 필요한 경우에도, 의미 있는 값으로 채우세요.
    - 예: 이메일의 경우 "long.username@example.com"
    - 예: 비밀번호의 경우 "Aa1!xYz9#Kq3$Lm7@" (형식을 만족하는 다양한 문자 조합 사용)
- 모든 테스트 데이터는 해당 필드의 `length`를 **절대 초과하지 마세요.**
- 모든 "value" 값은 반드시 문자열(string)로 작성해주세요. 숫자형 데이터도 예: "12345" 와 같이 큰따옴표로 감싸 문자열로 표현해야 합니다.

[출력 형식]
- 오직 JSON 문자열만 반환하세요. 추가 설명, 마크다운, 코드 블럭 등은 절대 포함하지 마세요.
- `.repeat()`, 문자열 연결(`+`), `//` 주석 등 JavaScript 문법은 절대 사용하지 마세요.
- "value"에는 실제 값을 그대로 작성하세요. 예) `"value": "aaaaaaaaaa"` (50자면 실제로 50자 반복)

예외가 발생하면 응답을 버리게 되니 반드시 위 기준을 지키세요.
                     
```json
[
    {
    "mapping_id": 1,
    "tc_id": "TC-UserCreate-정상-001-f1a2c3b4",
    "precondition": "사용자 등록 API를 통해 userId를 획득해야 함",
    "description": "정상 입력에 대한 사용자 조회 테스트",
    "expected_result": "조회된 사용자 정보가 입력과 일치하며, password는 노출되지 않아야 함",
    "status": 2,
    "test_data_list": [
        {
        "param": {
            "paramId": 1,
            "category": "path/query",
            "koName": "사용자ID",
            "name": "userId",
            "context": "path",
            "type": "varchar",
            "length": 20,
            "required": true,
            "parent": "사용자 데이터",
            "desc": "조회할 사용자 ID"
        },
        "value": "abc123"
        }, ...
    ]
    },
    { "mapping_id": 1, "tc_id": "TC-UserCreate-경계-002-1q2w3e4r", ... }
]
```
    """).replace("%UUID%", uuid_suffix)

    return prompt
