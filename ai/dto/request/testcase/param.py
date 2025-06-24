# ai/dto/request/testcase/param.py
from pydantic import BaseModel, Field
from typing import Optional

class Param(BaseModel):
    """
    파라미터 객체 정의
    (파라미터 ID, 파라미터 항목, 한글명, 영문명, 항목유형, 데이터타입, 길이, 포맷, 기본값, 필수여부, 상위항목명, 부모항목, 설명)
    """
    param_id: int = Field(..., alias="paramId")
    category: str
    ko_name: str = Field(default=None, alias="koName")
    name: str
    context: str
    type: str
    length: Optional[int] = None
    format: Optional[str] = None
    default_value: Optional[str] = Field(default=None, alias="defaultValue")
    required: bool
    parent: Optional[str] = None
    desc: Optional[str]

    model_config = {
        "populate_by_name": True,
    }