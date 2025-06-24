from typing import List, Dict

def create_mapping_generation_prompt(semantic_mapping: Dict, scenarios: List[Dict], api_lists: List[Dict]) -> str:
    """
    매핑표 생성을 위한 LLM 프롬프트 생성
    """
    return f"""
아래는 시나리오-API 의미적 매핑 결과입니다.

[의미적 매핑 결과]
{semantic_mapping}

[시나리오 목록]
{scenarios}

[API 목록]
{api_lists}

위 정보를 바탕으로, 각 시나리오별로 실제로 호출해야 할 API와 파라미터를 포함한 매핑표를 JSON으로 생성해 주세요.
"""
