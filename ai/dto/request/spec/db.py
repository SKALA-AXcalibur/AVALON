# # ai/dto/request/spec/db.py

from typing import List, Optional
from pydantic import BaseModel, Field


class ColumnDto(BaseModel):
    col_name: str
    desc: Optional[str] = None
    type: str
    length: Optional[int] = None
    is_pk: bool = False
    fk: Optional[str] = None
    is_null: bool = True
    const: Optional[str] = None


class DbDesignDto(BaseModel):
    name: str
    column: List[ColumnDto]  # 요청파라미터와 맞춤