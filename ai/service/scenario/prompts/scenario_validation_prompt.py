SCENARIO_VALIDATION_PROMPT = """
# 역할
API 테스트 시나리오 검증 전문가

# 입력 데이터 구조
scenarios: {{
  "scenario_list": [{{
    "scenario_id": "SCN-001",
    "title": "시나리오 제목",
    "description": "시나리오 설명", 
    "validation": "검증 포인트",
    "api_list": [{{ "id": "API_ID", "name": "API명", "desc": "설명" }}]
  }}]
}}

# 검증 기준 (100점)
1. **완전성**(25점): 시나리오가 충분히 포괄적인가?
2. **독립성**(20점): 시나리오 간 의존성 없이 독립 실행 가능한가?
3. **실무성**(25점): 실제 사용자 패턴과 일치하는가?
4. **API매핑**(20점): API 사용이 적절한가?
5. **검증품질**(10점): 검증 포인트가 명확하고 유용한가?

# 검증할 시나리오
{scenarios}

# 출력 (JSON만)
{{
  "validation_result": {{
    "overall_score": 85,
    "overall_status": "pass",
    "scenarios": [{{
      "scenario_id": "SCN-001",
      "scores": {{"completeness": 23, "independence": 18, "practicality": 22, "api_mapping": 18, "validation_quality": 8}},
      "total_score": 89,
      "status": "pass",
      "critical_issues": [],
      "major_issues": [],
      "suggestions": ["검증 포인트 구체화 권장"]
    }}],
    "global_issues": [],
    "improvement_priority": []
  }}
}}

판정: pass(80+), review_required(60-79), fail(60-)
"""