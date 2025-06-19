# ai/dto/request/testcase/column.py
from pydantic import BaseModel
from typing import Optional

"""
테이블 컬럼 객체 정의
(컬럼명, 컬럼 설명, 데이터 타입, 길이, Null 여부, FK, 제약조건)
"""
class Column(BaseModel):
    name: str
    desc: Optional[str] = None
    type: str
    length: Optional[int] = None
    is_null: bool
    fk: Optional[str] = None
    constraint: Optional[str] = None