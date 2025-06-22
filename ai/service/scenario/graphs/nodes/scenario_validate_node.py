# service/scenario/graphs/nodes/scenario_validate_node.py
import logging
from typing import Dict, Any

from service.scenario.agents.scenario_validator import ScenarioValidator
from service.scenario.state.scenario_state import ScenarioState


def scenario_validate_node(state: ScenarioState) -> Dict[str, Any]:
    """
    시나리오 검증 노드
    """
    logging.info("=== 시나리오 검증 노드 시작 ===")
    
    try:
        # 상태에서 필요 데이터 추출
        generated_scenarios = state.get("generated_scenarios") # 생성된 시나리오
        current_attempt = state.get("attempt_count", 1) # 시도 횟수
        
        if not generated_scenarios:
            raise ValueError("generated_scenarios가 상태에 없습니다.")
        
        logging.info(f"시나리오 검증 시작 - {current_attempt}번째 시도")
        
        # 시나리오 생성 실행
        validator = ScenarioValidator()
        validation_response = validator.validate_scenario_only(generated_scenarios)
        
        # 검증 결과 추출
        overall_status = validation_response.validation_result.overall_status # 검증 상태
        overall_score = validation_response.validation_result.overall_score # 점수
        
        # 검증 점수 상세 정보
        logging.info(f"시나리오 검증 완료: 상태={overall_status}, 점수={overall_score}점")
        logging.info(f"검증 상세: 시도횟수={current_attempt}, 시나리오개수={len(generated_scenarios.scenario_list) if hasattr(generated_scenarios, 'scenario_list') else 'N/A'}")
        
        # 상태 업데이트
        return {
            "validation_result": validation_response,
            "current_step": "validation_completed",
            "validation_status": overall_status,
            "overall_score": overall_score
        }
        
    except Exception as e:
        logging.exception("시나리오 검증 노드에서 오류 발생")
        return {
            "validation_result": None,
            "current_step": "validation_failed", 
            "validation_status": "failed",
            "error_message": str(e)
        }