# ai/dto/request/spec/requirement.py
from pydantic import BaseModel

'''
요구사항 명세서 객체 정의
(요구사항 명, 요구사항 설명, 중요도, 대분류, 중분류, 소분류)
'''
class Requirement(BaseModel):
    name: str
    desc: str
    priority: str
    major: str
    middle: str
    sub: str