# # ai/dto/request/spec/db.py

from typing import List, Optional
from pydantic import BaseModel


class ColumnDto(BaseModel):
    col_name: str
    desc: Optional[str] = None
    type: str
    length: Optional[str] = None
    isPk: bool = False
    fk: Optional[str] = None
    isNull: bool = True
    constraint: Optional[str] = None


class DbDesignDto(BaseModel):
    name: str
    col: List[ColumnDto] # 요청파라미터와 맞춤
