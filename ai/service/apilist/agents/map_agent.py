import logging
import json
import os
import re
from typing import Dict, Any, List
from dotenv import load_dotenv
from langchain_anthropic import ChatAnthropic
from langchain_core.messages import HumanMessage, SystemMessage

from service.apilist.prompts.map_prompt import (
    create_semantic_mapping_prompt,
    get_semantic_mapping_system_prompt
)

# .env 파일 로드
load_dotenv()

# 배치 처리 크기 상수
BATCH_SIZE = 5

# LangChain Hosted LLM (Smith API) 사용 예시 - 루프 밖에서 한 번만 생성
llm = ChatAnthropic(
    model=os.getenv("MODEL_NAME", "claude-sonnet-4-20250514"),
    temperature=float(os.getenv("MODEL_TEMPERATURE", 0.7)),
    max_tokens=4000,
    api_key=os.getenv("ANTHROPIC_API_KEY"),
)

def clean_llm_json(text):
    """LLM 응답에서 JSON 추출 및 정리"""
    # ```json ... ``` 또는 ``` ... ``` 코드블록 제거
    cleaned = re.sub(r"^```json\s*|^```\s*|```$", "", text.strip(), flags=re.MULTILINE)
    cleaned = cleaned.strip()
    
    # JSON 객체 시작과 끝 찾기
    start_idx = cleaned.find('{')
    end_idx = cleaned.rfind('}')
    
    if start_idx != -1 and end_idx != -1 and end_idx > start_idx:
        cleaned = cleaned[start_idx:end_idx + 1]
    
    return cleaned

def safe_json_parse(text: str) -> Dict[str, Any]:
    """안전한 JSON 파싱 - 코드블록 제거 후 파싱"""
    try:
        # 먼저 코드블록 제거
        cleaned = clean_llm_json(text)
        return json.loads(cleaned)
    except json.JSONDecodeError as e:
        logging.error(f"JSON 파싱에 실패했습니다: {e}. 원본 텍스트: \"{cleaned}\"")
        return {
            "mappings": [],
            "overall_confidence": 0.0,
            "error": f"JSON 파싱 실패: {e}"
        }

def perform_semantic_mapping(scenarios: List[Dict], api_lists: List[Dict]) -> Dict[str, Any]:
    """LLM을 사용한 의미적 매핑 분석 - 배치 처리"""
    try:
        
        all_mappings = []
        total_confidence = 0.0
        batch_count = 0
        
        # 시나리오를 배치로 나누어 처리
        for i in range(0, len(scenarios), BATCH_SIZE):
            batch_scenarios = scenarios[i:i+BATCH_SIZE]
            batch_num = (i // BATCH_SIZE) + 1
            
            # 배치별 프롬프트 생성
            prompt = create_semantic_mapping_prompt(batch_scenarios, api_lists)
            system_prompt = get_semantic_mapping_system_prompt()

            # LangChain 메시지 객체 사용
            messages = [
                SystemMessage(content=system_prompt),
                HumanMessage(content=prompt)
            ]
            
            result = llm.invoke(messages)
            result_text = result.content if hasattr(result, "content") else str(result)
            
            # 안전한 JSON 파싱
            batch_mapping = safe_json_parse(result_text)
            
            # 배치 결과가 유효한 경우에만 추가
            if batch_mapping.get("mappings") and not batch_mapping.get("error"):
                all_mappings.extend(batch_mapping.get("mappings", []))
                total_confidence += batch_mapping.get("overall_confidence", 0.0)
                batch_count += 1
        
        # 평균 confidence 계산
        average_confidence = total_confidence / batch_count if batch_count > 0 else 0.0
        
        return {
            "mappings": all_mappings,
            "overall_confidence": average_confidence
        }
        
    except Exception as e:
        logging.error(f"LangChain LLM 호출 에러: {str(e)}")
        return {
            "mappings": [],
            "overall_confidence": 0.0,
            "error": str(e)
        }
