# ai/service/spec/formatter.py

from fastapi import UploadFile, HTTPException
from service.spec.requirement_parser import RequirementParserService
from service.spec.interface_impl_parser import InterfaceImplParserService
from service.spec.interface_def_parser import InterfaceDefParserService
from service.spec.db_design_parser import DbDesignParserService

from dto.request.spec.spec_upload_request import SpecUploadRequest


async def formatter(
    requirementFile: UploadFile,
    interfaceDesign: UploadFile,
    interfaceDef: UploadFile,
    databaseDesign: UploadFile,
):
    """
    여러 명세 관련 엑셀 파일을 파싱하여 SpecUploadRequest 객체로 통합 포맷팅하는 함수

    :param requirementFile: 요구사항정의서 엑셀 (UploadFile)
    :param interfaceDesign: 인터페이스설계서 엑셀 (UploadFile)
    :param interfaceDef: 인터페이스정의서 엑셀 (UploadFile)
    :param databaseDesign: 테이블정의서(컬럼) 엑셀 (UploadFile)
    :return: SpecUploadRequest 객체 (파싱된 결과가 모두 들어감)

    동작 순서:
      1. 요구사항 정의서 파싱 → 요구사항 리스트 추출
      2. 인터페이스 설계서 파싱 → API 설계 리스트 추출
      3. 인터페이스 정의서 + 2번 결과 매핑 → 요구사항ID 포함 API 리스트
      4. DB 설계서 파싱 → 테이블/컬럼 구조 리스트 추출
      5. 위 결과들을 SpecUploadRequest DTO에 통합
    """
    try:
        parser_req = RequirementParserService()
        req_result = await parser_req.parse_requirement_file(requirementFile)

        parser_impl = InterfaceImplParserService()
        impl_result = await parser_impl.parse_interface_file(interfaceDesign)

        parser_def = InterfaceDefParserService()
        def_result = await parser_def.map_req_ids_to_apis(interfaceDef, impl_result)

        parser_db = DbDesignParserService()
        db_result = await parser_db.parse_dbdesign_file(databaseDesign)

    except Exception as e:
        raise HTTPException(status_code=502, detail=f"정보저장API 호출 실패: {str(e)}")
    spec_upload_request = SpecUploadRequest(
        requirement=req_result, apiList=def_result, tableList=db_result
    )  # 요청파라미터와 맞춤

    return spec_upload_request

