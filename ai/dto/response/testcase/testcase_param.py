# ai/dto/response/testcase/testcase_param.py
from pydantic import BaseModel, Field
from typing import Optional

"""
TC 파라미터 객체 정의
(파라미터 ID, 데이터에 들어가는 값)
"""
class TestcaseParam(BaseModel):
    param_id: int = Field(..., alias="paramId")
    value: Optional[str] = None

    class Config:
        validate_by_name = True