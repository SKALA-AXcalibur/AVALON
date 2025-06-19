# ai/dto/request/testcase/table.py
from pydantic import BaseModel
from typing import List

from dto.request.testcase.column import Column

"""
테이블 객체 정의
(테이블 명, 컬럼 목록)
"""
class Table(BaseModel):
    table_name: str
    col_list: List[Column]