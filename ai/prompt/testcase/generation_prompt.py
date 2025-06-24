# ai/prompt/testcase/validation_prompt.py

from typing import List
from textwrap import dedent
from uuid import uuid4

from dto.request.testcase.api_mapping import ApiMapping
from dto.request.testcase.scenario import Scenario

def build_generation_prompt(api_mapping_list: List[ApiMapping], scenario: Scenario) -> str:
    """
    TC 생성 프롬프트 제작 함수
    시나리오와 시나리오에 매핑된 API들의 파라미터 정보를 기반으로 프롬프트를 생성합니다.
    """
    uuid_suffix = str(uuid4())[:8]

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
            prompt += f"  - ID: {param.param_id} | 영문명: {param.name} | 한글명: {param.ko_name} | data type={param.type}, 길이={param.length}, category={param.category}, context {param.context} | 필수: {'Y' if param.required else 'N'} | 상위 항목명: {param.parent} | 설명: {param.desc}\n"

    prompt += dedent("""
[테스트케이스 생성 조건]
1. TC 유형: 정상 / 경계값 / 비정상 각 1건씩 작성
2. 시나리오의 검증 포인트 **반드시 반영**
3. `precondition`은 이전 API 응답과 현재 API 연동 시 작성
   - 형식: `step 2:body|userId -> path|userId` (여러 개면 콤마로 구분)
4. `status`: 2, 3, 4, 5 중 하나의 **숫자형**
5. `tc_id`는 `TC-<API영문명>-<유형>-<###>-<%UUID%>` 형식으로 작성
6. 모든 `param_id`는 기존 정의된 값을 그대로 사용

---
[value 작성 규칙]
- **모든 value는 문자열(string)** 로 작성 (숫자도 `"123"`처럼 큰따옴표로 감싸야 함)
- value의 길이는 해당 param의 `length`를 **절대 초과하지 말 것**
- **최대 길이 제한**: 모든 문자열은 **255자를 초과하지 말 것**
- 아래와 같은 패턴은 **절대 금지**:
  - 반복 문자열: `"abcabcabc"`, `"123123123"`, `"MaxLenPwd@2024!MaxLenPwd@2024!"`
  - 무의미한 값: `"aaaaaaaaa@example.com"`, `"testtest123"`
- 최대 길이 테스트 시에도 **의미 있는 값처럼 보이게 작성**
  - 예: `"long.username@example.com"`, `"홍길동최대길이사용자"`

---
[출력 형식]
- 반환값은 오직 **JSON 문자열**만 허용
- 다음 문법은 절대 사용 금지:
  - 마크다운, 설명, 코드블록
  - `.repeat()`, `+` 문자열 연결, `//` 주석
- `"value"`는 실제 문자열 **그대로** 작성할 것  
  ✅ `"value": "홍길동최대길이사용자"`  
  ❌ `"value": "홍길동".repeat(10)"`

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
        "param_id": 1,
        "value": "abc123"
        }, ...
    ]
    },
    { "mapping_id": 1, "tc_id": "TC-UserCreate-경계-002-1q2w3e4r", ... }
]
```
    """).replace("%UUID%", uuid_suffix)

    return prompt
