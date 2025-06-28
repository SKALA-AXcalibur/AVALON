# service/scenario/graphs/nodes/decision_node.py
import logging
from typing import Literal

from service.scenario.state.scenario_state import ScenarioState


def should_regenerate(
    state: ScenarioState,
) -> Literal["regenerate", "complete", "failed"]:
    """
    재생성 여부 결정 노드
    검증 결과와 시도 횟수를 기반으로 다음 단계 결정
        - "regenerate": 재생성 시도
        - "complete": 더 이상 시도 없이 완료 처리
        - "failed": 실패
    """


    try:
        # 상태에서 필요 데이터 추출
        overall_score = state.get("overall_score", 0)  # 점수
        validation_status = state.get("validation_status")  # 검증 상태
        current_attempt = state.get("attempt_count", 1)  # 시도 횟수
        max_attempts = state.get("max_attempts", 3)  # 최대 시도 횟수

        logging.info(
            f"결정 기준 - 점수: {overall_score}, 상태: {validation_status}, 시도: {current_attempt}/{max_attempts}"
        )

        # 검증 자체가 실패한 경우
        if validation_status == "failed":
            logging.error("검증 프로세스 실패")
            return "failed"

        # 80점 이상이면 완료
        if overall_score >= 80:
            logging.info("검증 통과 - 완료")
            return "complete"

        # 최대 시도 횟수 초과시 완료 (강제 종료)
        if current_attempt >= max_attempts:
            logging.warning(f"최대 시도 횟수({max_attempts}) 초과 - 강제 완료")
            return "complete"

        # 60점 이상이고 시도 횟수 여유 있으면 재생성
        if overall_score >= 60:
            logging.info("점수 미달 - 재생성 진행")
            return "regenerate"

        # 60점 미만이면 실패
        logging.warning(f"점수 너무 낮음({overall_score}) - 실패 처리")
        return "failed"

    except Exception as e:
        logging.exception("재생성 결정 노드에서 오류 발생")
        return "failed"
