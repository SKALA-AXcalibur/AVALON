# ai/dto/request/spec_upload_request.py
from typing import List
from pydantic import BaseModel

from dto.request.spec.requirement import Requirement
from dto.request.spec.api import Api
from dto.request.spec.db import Db

'''
명세서 분석 결과 request 객체 정의
(요구사항 명세 객체, API 목록 객체, 테이블설계서)
'''
class SpecUploadRequest(BaseModel):
    requirement: List[Requirement]
    apiList: List[Api]
    db_design: List[Db]