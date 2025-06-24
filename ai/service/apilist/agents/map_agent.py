import logging
import json
import os
from typing import Dict, Any, List
from anthropic import Anthropic
from dotenv import load_dotenv

from service.aplist.prompts.map_prompt import (
    create_semantic_mapping_prompt,
    get_semantic_mapping_system_prompt
)

# .env 파일 로드
load_dotenv()

# Anthropic Claude 클라이언트 설정
anthropic_client = Anthropic(api_key=os.getenv('ANTHROPIC_API_KEY'))


def perform_semantic_mapping(scenarios: List[Dict], api_lists: List[Dict]) -> Dict[str, Any]:
    """LLM을 사용한 의미적 매핑 분석"""
    try:
        # LLM 프롬프트 구성
        prompt = create_semantic_mapping_prompt(scenarios, api_lists)
        system_prompt = get_semantic_mapping_system_prompt()
        
        # Anthropic Claude API 호출
        response = anthropic_client.messages.create(
            model=os.getenv('MODEL_NAME', 'claude-sonnet-4'),
            max_tokens=4000,
            temperature=float(os.getenv('MODEL_TEMPERATURE', 0.7)),
            system=system_prompt,
            messages=[
                {"role": "user", "content": prompt}
            ]
        )
        
        # 응답 파싱
        result_text = response.content[0].text
        semantic_mapping = json.loads(result_text)
        
        return semantic_mapping
        
    except Exception as e:
        logging.error(f"의미적 매핑 분석 에러: {str(e)}")
        return {
            "mappings": [],
            "overall_confidence": 0.0,
            "error": str(e)
        }
