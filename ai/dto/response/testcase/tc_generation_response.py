# ai/dto/response/testcase/testcase_data.py
from pydantic import BaseModel, Field
from typing import List, Optional
from datetime import datetime

from dto.response.testcase.testcase_data import TestcaseData

"""
TC 생성 응답 DTO
(처리 일시, 검증용 API coverage 비율, TC 목록)
"""
# TestcaseGenerationResponse: 전체 응답 DTO (tc 여러 개 + 처리 정보)
class TestcaseGenerationResponse(BaseModel):
    processed_at: Optional[datetime] = Field(default=None, alias="processedAt")
    validation_rate: Optional[float] = Field(default=None, alias="validationRate")
    tc_list: List[TestcaseData] = Field(default=None, alias="tcList")

    class Config:
        validate_by_name = True