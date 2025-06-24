SCENARIO_FLOW_PROMPT = """
테스트 시나리오를 Mermaid flowchart로 시각화해줘.

규칙:
- Mermaid flowchart LR 문법 사용
- 노드 형식: "API_ID: API 설명" 
- Start → 비즈니스 순서대로 → End
- ```mermaid 코드블럭으로만 응답
- 시나리오별 하나씩 생성

입력: {data}
"""
