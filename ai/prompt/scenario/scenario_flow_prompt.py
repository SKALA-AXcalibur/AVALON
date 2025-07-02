SCENARIO_FLOW_PROMPT = """
테스트 시나리오들을 각각 Mermaid flowchart로 시각화해줘.

중요한 규칙:
- 각 시나리오별로 개별 Mermaid flowchart LR 문법 사용
- 노드 형식: "API_ID: API 설명" 
- Start → 비즈니스 순서대로 → End
- **각 시나리오의 api_list에 있는 모든 API를 반드시 포함해야 함**
- 시나리오 ID를 구분자로 사용하여 다음 형식으로 응답:

=== 시나리오_ID ===
```mermaid
flowchart LR
    Start --> A[API_ID: API 설명]
    A --> B[API_ID: API 설명]
    B --> End
```

입력 시나리오 목록: {data}

각 시나리오별로 위 형식에 맞춰 플로우차트를 생성해주세요.
"""
