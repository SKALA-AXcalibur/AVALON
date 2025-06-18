# ai/dto/request/spec/param.py
from pydantic import BaseModel
from typing import Optional

'''
API 파라미터 공통 항목 정의
(한글명, 영문명, 항목유형, 단계, 데이터타입, 길이, 포맷, 기본값, 필수여부, 상위항목명, 설명)
'''
class Param(BaseModel):
    id: Optional[str] = None  # 추가(project의 ParameterDetailDto랑 맞춤)
    name_ko: str
    name: str
    item_type: str
    step: int
    data_type: str
    length: Optional[int] = None
    format: Optional[str] = None
    default_value: Optional[str] = None
    required: bool
    upper: Optional[str] = None # str로 수정
    desc: Optional[str] = None
    api_id: Optional[str] = None  # 추가(project의 ParameterDetailDto랑 맞춤)
    api_name: Optional[str] = None  # 추가(project의 ParameterDetailDto랑 맞춤)