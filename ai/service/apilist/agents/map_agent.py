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
        logging.warning(f"JSON 파싱 실패, 복구 시도: {str(e)}")
        try:
            # 중괄호 균형 맞추기 등 복구 로직
            open_braces = cleaned.count('{')
            close_braces = cleaned.count('}')
            if open_braces > close_braces:
                cleaned += '}' * (open_braces - close_braces)
            elif close_braces > open_braces:
                cleaned = '{' * (close_braces - open_braces) + cleaned
            return json.loads(cleaned)
        except Exception as e2:
            logging.error(f"JSON 복구 실패: {str(e2)}")
            return {
                "mappings": [],
                "overall_confidence": 0.0,
                "error": f"JSON 파싱 실패: {str(e)}"
            }

def perform_semantic_mapping(scenarios: List[Dict], api_lists: List[Dict]) -> Dict[str, Any]:
    """LLM을 사용한 의미적 매핑 분석 - 배치 처리"""
    try:
        # 디버깅: 입력 데이터 확인
        print("=== 입력 데이터 확인 ===")
        print(f"시나리오 개수: {len(scenarios)}")
        print(f"API 개수: {len(api_lists)}")
        if scenarios:
            print(f"첫 번째 시나리오: {scenarios[0]}")
        if api_lists:
            print(f"첫 번째 API: {api_lists[0]}")
        print("========================")
        
        # 배치 크기 설정 (시나리오 5개씩 처리)
        batch_size = 5
        all_mappings = []
        total_confidence = 0.0
        batch_count = 0
        
        # 시나리오를 배치로 나누어 처리
        for i in range(0, len(scenarios), batch_size):
            batch_scenarios = scenarios[i:i+batch_size]
            batch_num = (i // batch_size) + 1
            
            print(f"=== 배치 {batch_num} 처리 중 ===")
            print(f"배치 시나리오 개수: {len(batch_scenarios)}")
            
            # 배치별 프롬프트 생성
            prompt = create_semantic_mapping_prompt(batch_scenarios, api_lists)
            system_prompt = get_semantic_mapping_system_prompt()
            
            # 디버깅: 프롬프트 확인
            print(f"=== 배치 {batch_num} 프롬프트 확인 ===")
            print(f"프롬프트 길이: {len(prompt)}")
            print("프롬프트 내용:")
            print(prompt[:500] + "..." if len(prompt) > 500 else prompt)
            print("======================")
            
            # LangChain Hosted LLM (Smith API) 사용 예시
            llm = ChatAnthropic(
                model=os.getenv("MODEL_NAME", "claude-sonnet-4-20250514"),
                temperature=float(os.getenv("MODEL_TEMPERATURE", 0.7)),
                max_tokens=4000,
                api_key=os.getenv("ANTHROPIC_API_KEY"),
            )

            # LangChain 메시지 객체 사용
            messages = [
                SystemMessage(content=system_prompt),
                HumanMessage(content=prompt)
            ]
            
            result = llm.invoke(messages)
            result_text = result.content if hasattr(result, "content") else str(result)
            print(f"배치 {batch_num} LLM 응답:", result_text)
            
            # 안전한 JSON 파싱
            batch_mapping = safe_json_parse(result_text)
            
            # 배치 결과가 유효한 경우에만 추가
            if batch_mapping.get("mappings") and not batch_mapping.get("error"):
                all_mappings.extend(batch_mapping.get("mappings", []))
                total_confidence += batch_mapping.get("overall_confidence", 0.0)
                batch_count += 1
                print(f"배치 {batch_num} 처리 성공")
            else:
                print(f"배치 {batch_num} 처리 실패: {batch_mapping.get('error', 'Unknown error')}")
        
        # 평균 confidence 계산
        average_confidence = total_confidence / batch_count if batch_count > 0 else 0.0
        
        print(f"=== 최종 결과 ===")
        print(f"총 배치 수: {batch_count}")
        print(f"총 매핑 수: {len(all_mappings)}")
        print(f"평균 confidence: {average_confidence}")
        
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
