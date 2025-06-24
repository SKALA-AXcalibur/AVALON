# service/scenario/graphs/nodes/feedback_preparation_node.py
import logging
from typing import Dict, Any

from service.scenario.state.scenario_state import ScenarioState


def feedback_preparation_node(state: ScenarioState) -> Dict[str, Any]:
    """
    검증 결과를 다음 생성에 활용할 피드백으로 변환
        {
            "feedback_data": {...},         # 피드백 데이터
            "current_step": "feedback_prepared",  # 현재 상태 표시
            "ready_for_regeneration": True         # 재생성 가능 여부
        }
    """
    logging.info("=== 피드백 준비 노드 시작 ===")

    try:
        # 상태에서 필요 데이터 추출
        validation_result = state.get("validation_result")  # 검증 결과
        current_attempt = state.get("attempt_count", 1)  # 시도 횟수

        # 검증 결과가 없으면 실패
        if not validation_result:
            raise ValueError("validation_result가 상태에 없습니다.")

        # 검증 결과에서 피드백 데이터 추출
        feedback_data = _extract_feedback_from_validation(validation_result)

        logging.info(
            f"피드백 준비 완료 - {len(feedback_data.get('issues', []))}개 이슈, {len(feedback_data.get('suggestions', []))}개 제안사항"
        )

        return {
            "feedback_data": feedback_data,
            "current_step": "feedback_prepared",
            "ready_for_regeneration": True,
        }

    except Exception as e:
        logging.exception("피드백 준비 노드에서 오류 발생")
        return {
            "feedback_data": None,
            "current_step": "feedback_preparation_failed",
            "error_message": str(e),
        }


def _extract_feedback_from_validation(validation_result) -> Dict[str, Any]:
    """
    검증 결과에서 구체적인 피드백 데이터 추출
        - overall_score: 전체 점수
        - issues: 각 시나리오의 이슈 목록
        - suggestions: 개선 제안 목록
        - focus_areas: 점수가 낮은 영역
        - improvement_priority: 우선 개선 순위
    """
    feedback = {
        "overall_score": validation_result.validation_result.overall_score,
        "issues": [],
        "suggestions": [],
        "focus_areas": [],
        "improvement_priority": validation_result.validation_result.improvement_priority
        or [],
    }

    # 각 시나리오별 이슈와 제안사항 수집
    for scenario in validation_result.validation_result.scenarios:
        # 이슈
        for issue in scenario.critical_issues:
            feedback["issues"].append(
                {
                    "type": "critical",
                    "scenario_id": scenario.scenario_id,
                    "issue": issue,
                }
            )

        # 주요 이슈
        for issue in scenario.major_issues:
            feedback["issues"].append(
                {"type": "major", "scenario_id": scenario.scenario_id, "issue": issue}
            )

        # 개선 제안
        for suggestion in scenario.suggestions:
            feedback["suggestions"].append(
                {"scenario_id": scenario.scenario_id, "suggestion": suggestion}
            )

        # 점수가 낮은 영역 식별
        scores = scenario.scores
        if scores.get("completeness", 0) < 20:
            feedback["focus_areas"].append("completeness")
        if scores.get("independence", 0) < 16:
            feedback["focus_areas"].append("independence")
        if scores.get("practicality", 0) < 20:
            feedback["focus_areas"].append("practicality")
        if scores.get("api_mapping", 0) < 16:
            feedback["focus_areas"].append("api_mapping")
        if scores.get("validation_quality", 0) < 8:
            feedback["focus_areas"].append("validation_quality")

    # 중복 제거
    feedback["focus_areas"] = list(set(feedback["focus_areas"]))

    return feedback
