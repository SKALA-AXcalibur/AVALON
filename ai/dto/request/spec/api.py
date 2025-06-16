# ai/dto/request/spec/api.py
from pydantic import BaseModel
from typing import List, Optional

from dto.request.spec.param import Param

'''
인터페이스 객체 정의
(인터페이스 ID, 인터페이스명, 인터페이스 설명, HTTP Method, URL, path, Path/Query 객체, 요청 객체, 응답 객체)
'''
class Api(BaseModel):
    id: str
    name: str
    desc: str
    method: str
    url: str
    path: Optional[str] = None
    pathQuery: List[Param]
    request: List[Param]
    response: List[Param]