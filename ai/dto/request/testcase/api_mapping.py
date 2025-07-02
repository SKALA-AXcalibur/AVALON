# ai/dto/request/testcase/api_mapping.py
from pydantic import BaseModel, Field
from typing import List, Optional

from dto.request.testcase.param import Param

class ApiMapping(BaseModel):
    """
    API 매핑 객체 정의
    (매핑표 ID, 단계, API 이름, URL, Path, HTTP Method, 설명, 파라미터 목록)
    """
    mapping_id: int = Field(..., alias="mappingId")
    step: int
    name: Optional[str] = None
    url: Optional[str] = None
    path: str
    method: str
    desc: str
    param_list: List[Param] = Field(..., alias="paramList")

    model_config = {
        "populate_by_name": True,
    }