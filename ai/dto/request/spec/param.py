# ai/dto/request/spec/param.py
from pydantic import BaseModel
from typing import Optional

'''
API 파라미터 공통 항목 정의
(한글명, 영문명, 항목유형, 단계, 데이터타입, 길이, 포맷, 기본값, 필수여부, 상위항목명, 설명)
'''
class Param(BaseModel):
    korName: str
    name: str
    itemType: str
    step: int
    dataType: str
    length: Optional[int] = None
    format: Optional[str] = None
    default: Optional[str] = None
    required: bool
    upper: Optional[str] = None
    desc: Optional[str] = None