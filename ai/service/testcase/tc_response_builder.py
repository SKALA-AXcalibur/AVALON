# ai/service/testcase/tc_response_builder.py

from dto.response.testcase.tc_generation_response import TestcaseGenerationResponse
from state.testcase.flow_state import FlowState
from datetime import datetime

def build_tc_response_from_state(state: dict) -> TestcaseGenerationResponse:
    """
    LangGraph에서 반환된 dict 형태의 state를 기반으로 최종 응답 객체 구성
    """
    flow_state = FlowState(**state)

    return TestcaseGenerationResponse(
        processed_at=datetime.now(),
        validation_rate=flow_state.coverage,
        tc_list=flow_state.tc_list
    )