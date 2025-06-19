# ai/dto/response/testcase/testcase_data.py
from pydantic import BaseModel
from typing import List, Optional

from dto.response.testcase.testcase_param import TestcaseParam

"""
생성을 통해 결정되는 하나의 TC 객체 정의
(API 매핑표 ID(식별용), TC ID, 사전조건, 설명, 예상 결과, 예상 결과 코드(2XX, 3XX, 4XX, 5XX), 테스트 파라미터 목록)
"""
class TestcaseData(BaseModel):
    mapping_id: int
    tc_id: str
    precondition: Optional[str]
    description: Optional[str]
    expected_result: Optional[str]
    status: int
    test_data_list: List[TestcaseParam]