# ai/dto/request/testcase/param.py
from pydantic import BaseModel
from typing import Optional

"""
파라미터 객체 정의
(파라미터 항목, 한글명, 영문명, 항목유형, 데이터타입, 길이, 포맷, 기본값, 필수여부, 상위항목명, 설명)
"""
class Param(BaseModel):
    category: str
    ko_name: str
    name: str
    context: str
    type: str
    length: Optional[int] = None
    format: Optional[str] = None
    default_value: Optional[str] = None
    required: bool
    parent: Optional[str]
    desc: Optional[str]