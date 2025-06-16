"""
@file controller/router.py
@brief AVALON AI의 FastAPI 라우터 설정
@details 이 모듈은 AVALON AI의 FastAPI 라우터를 설정합니다.
@version 1.0
"""

from typing import Dict
from fastapi import APIRouter, File, Form, Response, UploadFile, requests

from service.spec.interface_def_parser import InterfaceDefParserService
from service.spec.interface_impl_parser import InterfaceImplParserService
from service.spec.db_design_parser import parse_db_design
from service.spec.requirement_parser import parse_requirement_file

router = APIRouter()


@router.get("/")
async def read_root() -> Response:
    """
    AVALON AI API의 기본 엔드포인트

    """
    return Response(
        content={"message": "Welcome to AVALON AI API"}, media_type="application/json"
    )


@router.post("/api/spec/v1/analyze")
async def analyze_spec(
    # projectId: str = Form(...),
    requirementFile: UploadFile = File(...),
    interfaceDef: UploadFile = File(...),
    interfaceDesign: UploadFile = File(...),
    # databaseDesign: UploadFile = File(...),
) -> Response:
    """
    명세서 분석
    4가지 문서 파싱 후 포맷팅
    정보저장api를 통해 저장
    """

    # 파싱 
    req_result = await parse_requirement_file(requirementFile)
    # db_result = await parse_db_design(databaseDesign)

    parser_impl = InterfaceImplParserService()
    impl_result = await parser_impl.parse_interface_file(interfaceDesign)

    parser_def = InterfaceDefParserService()
    def_result = await parser_def.map_req_ids_to_apis(interfaceDef, impl_result)

    # 포맷팅
    # result = formatter()

    # # 정보저장API로 POST 요청
    # response = requests.post(
    #     "http://info-save-api-url/requirements",  # 정보저장API URL로 변경
    #     # json=result
    # )

    return req_result, def_result


# @router.post("/api/scenario/v1/generate")
# async def generate_scenario(
#     projectId: str = Form(...),
#     ScenarioRequest: parsing_result = File(...), #파싱된 결과
# ) -> Response:
#     """
#     LLM을 통해 명세서 분석 결과를 바탕으로 테스트 시나리오를 생성하고 검증을 거친 후 반환
#     """
#     generate_scenario_result = generate_scenario(projectId, parsing_result)
#     validate_scenario_result = validate_scenario(generate_scenario_result)

#     return validate_scenario_result


@router.post("/api/scenario/v1/scenario")
async def generate_flow_chart() -> Response:
    """
    LLM을 통해 시나리오와 API 목록을 바탕으로 흐름도를 생성하고 시나리오 ID를 기준으로 흐름도 반환
    """

    return Response()
