# ai/dto/request/testcase/api_mapping.py
from pydantic import BaseModel
from typing import List, Optional

from dto.request.testcase.param import Param

"""
API 매핑 객체 정의
(매핑표 ID, 단계, API 이름, URL, Path, HTTP Method, 설명, 파라미터 목록)
"""
class ApiMapping(BaseModel):
    mapping_id: int
    step: int
    name: str
    url: Optional[str] = None
    path: str
    method: str
    desc: str
    param_list: List[Param]