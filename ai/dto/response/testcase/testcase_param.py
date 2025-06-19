# ai/dto/response/testcase/testcase_param.py
from pydantic import BaseModel
from typing import Optional

from dto.request.testcase.param import Param

"""
TC 파라미터 객체 정의
(파라미터 목록, 데이터에 들어가는 값)
"""
class TestcaseParam(BaseModel):
    param: Optional[Param] = None
    value: Optional[str] = None