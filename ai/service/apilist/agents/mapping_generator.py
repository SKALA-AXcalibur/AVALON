import logging
import json
import os
from typing import Dict, Any, List
from anthropic import Anthropic
from dotenv import load_dotenv

from service.aplist.prompts.mapping_generation_prompt import create_mapping_generation_prompt

# .env 파일 로드
load_dotenv()

anthropic_client = Anthropic(api_key=os.getenv('ANTHROPIC_API_KEY'))

def perform_mapping_generation(semantic_mapping: Dict, scenarios: List[Dict], api_lists: List[Dict]) -> List[Dict]:
    """
    LLM을 사용한 매핑표 생성
    """
    try:
        prompt = create_mapping_generation_prompt(semantic_mapping, scenarios, api_lists)
        response = anthropic_client.messages.create(
            model=os.getenv('MODEL_NAME', 'claude-sonnet-4'),
            max_tokens=4000,
            temperature=float(os.getenv('MODEL_TEMPERATURE', 0.7)),
            system="당신은 시나리오-API 매핑표를 생성하는 전문가입니다. 반드시 JSON만 반환하세요.",
            messages=[
                {"role": "user", "content": prompt}
            ]
        )
        result_text = response.content[0].text
        mapping_table = json.loads(result_text)
        return mapping_table.get("mapping_table", mapping_table)  # 구조에 따라 조정
    except Exception as e:
        logging.error(f"매핑표 생성 LLM 호출 에러: {str(e)}")
        return []
