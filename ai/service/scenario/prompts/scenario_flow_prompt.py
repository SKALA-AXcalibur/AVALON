SCENARIO_FLOW_PROMPT = """
너는 테스트 시나리오와 해당 시나리오에서 사용되는 API 목록을 기반으로,
각 시나리오의 **비즈니스 흐름을 Mermaid flowchart 문법으로 시각화하는 역할**을 맡고 있어.

다음 기준을 반드시 지켜줘:

1. **Mermaid flowchart TD 문법**을 사용하여 시나리오 흐름도를 작성할 것
2. 각 노드는 `"API_ID: API 설명"` 형식으로 표현할 것 (예: `IF-ORD-0001: 주문 생성`)
3. 흐름은 **시나리오상 비즈니스 순서**를 반영할 것 (ex. 생성 → 검증 → 저장 등)
4. 시작은 항상 `Start`, 종료는 `End`로 설정할 것
5. 결과는 **Mermaid 마크다운 코드블럭 형식**(```mermaid\n ... ```)으로만 응답할 것
6. 시나리오마다 하나의 Mermaid 흐름도만 생성할 것
7. 입력은 시나리오 리스트 형식이며, 각 시나리오 객체에는 시나리오 ID, 설명, 해당 API 목록이 포함됨

---

입력 예시 (JSON 일부):

```json
{{
  "scenario_list": [
    {{
      "id": "SCN-001",
      "description": "신규 주문 생성 시나리오",
      "api_list": [
        {{ "id": "IF-ORD-0001", "name": "주문 생성", "description": "신규 주문을 생성한다" }},
        {{ "id": "IF-ORD-0002", "name": "결제 처리", "description": "결제 과정을 처리한다" }},
        {{ "id": "IF-ORD-0003", "name": "재고 차감", "description": "해당 상품의 재고를 차감한다" }},
        {{ "id": "IF-ORD-0004", "name": "주문 완료 처리", "description": "주문 완료 상태로 변경한다" }}
      ]
    }}
  ]
}}

아래는 실제 입력 시나리오 목록입니다:
{data}
"""
