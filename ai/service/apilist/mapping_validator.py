import logging
import json
import os
import re
from typing import Dict, Any, List
from dotenv import load_dotenv
from service.llm_service import get_anthropic_client

from prompt.apilist.mapping_validation_prompt import create_mapping_validation_prompt

# .env 파일 로드
load_dotenv()

anthropic_client = get_anthropic_client()

def clean_llm_json(text):
    # ```json ... ``` 또는 ``` ... ``` 코드블록 제거
    cleaned = re.sub(r"^```json\s*|^```\s*|```$", "", text.strip(), flags=re.MULTILINE)
    return cleaned.strip()

def perform_mapping_validation(mapping_table: List[Dict]) -> Dict[str, Any]:
    """
    LLM을 사용한 매핑표 검증
    """
    try:
        prompt = create_mapping_validation_prompt(mapping_table)
        response = anthropic_client.messages.create(
            model=os.getenv('MODEL_NAME', 'claude-sonnet-4'),
            max_tokens=4000,
            temperature=float(os.getenv('MODEL_TEMPERATURE', 0.7)),
            system="당신은 시나리오-API 매핑표를 검증하는 전문가입니다. 반드시 JSON만 반환하세요.",
            messages=[
                {"role": "user", "content": prompt}
            ]
        )
        result_text = response.content[0].text
        try:
            cleaned = clean_llm_json(result_text)
            validation_result = json.loads(cleaned)
        except json.JSONDecodeError as e:
            logging.error(f"JSON 파싱 실패: {str(e)}")
            return {"validation_score": 0.0, "details": f"JSON 파싱 실패: {str(e)}"}
        return validation_result
    except Exception as e:
        logging.error(f"매핑표 검증 LLM 호출 에러: {str(e)}")
        return {
            "score": 0.0,
            "details": f"LLM 호출 실패: {str(e)}"
        }