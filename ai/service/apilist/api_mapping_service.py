"""
@file service/apilist/api_mapping_service.py
@brief API 매핑 서비스
@details API 매핑 관련 비즈니스 로직을 처리하는 서비스 클래스
@version 1.0
"""
from typing import List, Dict, Any
from dto.request.apilist.apilist_map_request import convert_scenario_list, convert_api_list
from dto.response.apilist.apilist_validation_response import ApiListValidationResponse
from dto.request.apilist.common import ApiMappingItem
from service.apilist.mapping_state_processor import create_initial_mapping_state
from service.apilist.apilist_graph import create_apilist_graph
from datetime import datetime
import asyncio
from fastapi import HTTPException
import os


class ApiMappingService:
    """API 매핑 서비스 클래스"""
    
    async def doApiMapping(self, avalon: str, scenario_list: List, api_list: List) -> Dict[str, Any]:
        """
        API 매핑을 수행하는 메인 메소드
        
        Args:
            avalon (str): Avalon 쿠키 값
            scenario_list (List): 시나리오 리스트
            api_list (List): API 리스트
            
        Returns:
            Dict[str, Any]: 매핑 결과
        """
        # 1. 데이터 변환
        converted_scenarios = convert_scenario_list(scenario_list)
        converted_apis = convert_api_list(api_list)
        
        # 2. 초기 상태 생성
        state = create_initial_mapping_state(avalon=avalon)
        state = state.model_copy(update={
            "scenarios": converted_scenarios,
            "api_lists": converted_apis
        })
        
        # 3. LangGraph 워크플로우 실행
        timeout = int(os.getenv("MODEL_TIMEOUT", 120))  # .env에서 읽어옴
        graph = create_apilist_graph()
        try:
            result = await asyncio.wait_for(graph.ainvoke(state), timeout=timeout)
        except asyncio.TimeoutError:
            raise HTTPException(status_code=504, detail=f"매핑표 생성이 {timeout}초 내에 완료되지 않았습니다.")
        
        # 4. 응답 생성 및 반환
        return self._build_response(result)
    
    def _build_response(self, result: Dict[str, Any]) -> Dict[str, Any]:
        """
        LangGraph 결과를 응답 형태로 변환
        
        Args:
            result (Dict[str, Any]): LangGraph 실행 결과
            
        Returns:
            Dict[str, Any]: 변환된 응답
        """
        # generated_mapping_table에서 매핑 데이터 가져오기
        mapping_table = result.get("generated_mapping_table", [])
        
        # None 체크 추가
        if mapping_table is None:
            mapping_table = []
            
        api_mapping_list = [
            ApiMappingItem(
                scenarioId=item["scenarioId"],
                stepName=item["stepName"],
                apiName=item["apiName"],
                description=item["description"],
                url=item["url"],
                method=item["method"],
                parameters=item["parameters"],
                responseStructure=item["responseStructure"],
            )
            for item in mapping_table
        ]

        # validation_result에서 검증 점수 가져오기
        validation_result = result.get("validation_result")
        
        if validation_result is not None:
            validation_score = validation_result.get("validation_score", 0.0)
        else:
            validation_score = result.get("validationRate", 0.0)

        response = ApiListValidationResponse(
            processedAt=datetime.now().isoformat(),
            validationRate=validation_score,
            apiMapping=api_mapping_list
        )
        
        return response.model_dump()


# 싱글톤 인스턴스
apiMappingService = ApiMappingService() 