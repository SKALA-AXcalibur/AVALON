import logging
import json
import os
import re
from typing import Dict, Any, List
from anthropic import Anthropic
from dotenv import load_dotenv

from ai.service.apilist.prompts.mapping_generation_prompt import create_mapping_generation_prompt

# .env 파일 로드
load_dotenv()

anthropic_client = Anthropic(api_key=os.getenv('ANTHROPIC_API_KEY'))

def perform_mapping_generation(semantic_mapping: Dict, scenarios: List[Dict], api_lists: List[Dict]) -> List[Dict]:
    """
    LLM을 사용한 매핑표 생성
    """
    try:
        prompt = create_mapping_generation_prompt(semantic_mapping, scenarios, api_lists)
        
        # 프롬프트 길이 로깅
        logging.info(f"프롬프트 길이: {len(prompt)} 문자")
        logging.info(f"프롬프트 미리보기: {prompt[:200]}...")
        
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
        
        # LLM 응답 상세 로깅
        logging.info(f"LLM 응답 길이: {len(result_text) if result_text else 0}")
        logging.info(f"LLM 응답 내용: '{result_text}'")
        
        # 빈 응답 체크
        if not result_text or result_text.strip() == "":
            logging.error("LLM이 빈 응답을 반환했습니다.")
            return []
        try:
            cleaned = re.sub(r"^```json\s*|^```\s*|```$", "", result_text.strip(), flags=re.MULTILINE)
            mapping_table = json.loads(cleaned)
        except json.JSONDecodeError as e:
            logging.warning(f"JSON 파싱 실패, 복구 시도: {str(e)}")
            try:
                start_idx = cleaned.find('{')
                end_idx = cleaned.rfind('}')
                if start_idx != -1 and end_idx != -1 and end_idx > start_idx:
                    partial_json = cleaned[start_idx:end_idx + 1]
                    open_braces = partial_json.count('{')
                    close_braces = partial_json.count('}')
                    if open_braces > close_braces:
                        partial_json += '}' * (open_braces - close_braces)
                    mapping_table = json.loads(partial_json)
                else:
                    raise e
            except json.JSONDecodeError as e2:
                logging.error(f"JSON 복구 실패: {str(e2)}")
                return []
        return mapping_table.get("mapping_table", mapping_table)  # 구조에 따라 조정
    except Exception as e:
        logging.error(f"매핑표 생성 LLM 호출 에러: {str(e)}")
        return []
