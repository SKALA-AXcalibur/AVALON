# ai/dto/request/spec/api.py
from pydantic import BaseModel
from typing import List, Optional

from dto.request.spec.param import Param

'''
인터페이스 객체 정의
(인터페이스 ID, 인터페이스명, 인터페이스 설명, HTTP Method, URL, path, Path/Query 객체, 요청 객체, 응답 객체)
'''
class Api(BaseModel):
    api_pk: Optional[int] = None  # 추가(project의 ApiInfoDto랑 맞춤), 수정
    id: str
    name: str
    desc: str
    method: str
    url: Optional[str] = None
    req_id: Optional[str] = None
    path: str
    path_query: List[Param] # 수정
    request: List[Param]
    response: List[Param]
    