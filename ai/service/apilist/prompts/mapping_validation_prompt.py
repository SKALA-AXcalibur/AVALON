from typing import List, Dict

def create_mapping_validation_prompt(mapping_table: List[Dict]) -> str:
    """
    매핑표 검증을 위한 LLM 프롬프트 생성
    """
    return f"""
아래는 시나리오-API 매핑표입니다.

[매핑표]
{mapping_table}

이 매핑표가 시나리오와 API의 목적에 맞게 잘 매핑되어 있는지 검증해 주세요.
문제가 있다면 어떤 점이 부족한지, 점수(0~100)와 함께 JSON으로 알려주세요.
"""
