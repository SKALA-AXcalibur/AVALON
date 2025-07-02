from typing import Dict, Any, Optional, List
from pydantic import BaseModel, Field
import os
from dotenv import load_dotenv

# .env 파일 로드
load_dotenv()

class MappingState(BaseModel):
   """
   의미적 매핑 및 매핑표 생성/검증을 위한 상태 관리
   LangGraph에서 노드 간 데이터 전달을 위한 상태 정의
   """
   
   # 입력 데이터
   avalon: str  # Redis 토큰
   max_attempts: Optional[int] = Field(default=int(os.getenv("MAX_RETRY")))  # 최대 재시도 횟수
   target_score: Optional[float] = Field(default=float(os.getenv("TARGET_SCORE")))  # 목표 검증 점수
   
   # 조회된 기본 데이터
   project_key: Optional[int] = None  # 프로젝트 키
   scenarios: Optional[List[Dict]] = None  # 시나리오 리스트
   api_lists: Optional[List[Dict]] = None  # API 리스트
   parameters: Optional[List[Dict]] = None  # 파라미터 리스트
   
   # 의미적 매핑 관련 상태
   semantic_mapping: Optional[Dict[str, Any]] = None  # 의미적 연관성 분석 결과
   mapping_status: Optional[str] = Field(default="pending")  # 매핑 상태
   
   # 매핑표 생성 관련 상태
   generated_mapping_table: Optional[List[Dict]] = None  # 생성된 매핑표
   generation_status: Optional[str] = Field(default="pending")  # 생성 상태
   
   # 검증 관련 상태
   validation_result: Optional[Dict[str, Any]] = None  # 검증 결과
   validation_status: Optional[str] = Field(default="pending")  # 검증 상태
   validation_score: Optional[float] = Field(default=0.0)  # 검증 점수
   
   # 진행 상태 관리
   current_step: Optional[str] = Field(default="initialized")  # 현재 단계
   attempt_count: Optional[int] = Field(default=0)  # 현재 시도 횟수
   
   # 최종 결과
   final_mappings: Optional[List[Dict]] = None  # DB 저장할 최종 매핑 데이터
   
   # 에러 처리
   error_message: Optional[str] = None  # 에러 메시지
   has_error: Optional[bool] = Field(default=False)  # 에러 발생 여부


class WorkflowStatus(BaseModel):
   """
   워크플로우 상태 요약용 타입
   """
   current_step: str  # 현재 단계
   attempt_count: int  # 시도 횟수
   mapping_status: str  # 매핑 상태
   generation_status: str  # 생성 상태
   validation_status: str  # 검증 상태
   validation_score: float  # 검증 점수
   has_error: bool  # 에러 여부
   is_completed: bool  # 완료 여부