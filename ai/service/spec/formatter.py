# ai/service/spec/formatter.py

from fastapi import UploadFile
from service.spec.requirement_parser import RequirementParserService
from service.spec.interface_impl_parser import InterfaceImplParserService
from service.spec.interface_def_parser import InterfaceDefParserService
from service.spec.db_design_parser import DbDesignParserService

from dto.request.spec.spec_upload_request import SpecUploadRequest

async def formatter(
    requirementFile: UploadFile,
    interfaceDesign: UploadFile,
    interfaceDef: UploadFile,
    databaseDesign: UploadFile):

    parser_req = RequirementParserService()
    req_result = await parser_req.parse_requirement_file(requirementFile)

    parser_impl = InterfaceImplParserService()
    impl_result = await parser_impl.parse_interface_file(interfaceDesign)

    parser_def = InterfaceDefParserService()
    def_result = await parser_def.map_req_ids_to_apis(interfaceDef, impl_result)

    parser_db = DbDesignParserService()
    db_result = await parser_db.parse_dbdesign_file(databaseDesign)
    
    spec_upload_request = SpecUploadRequest(requirement=req_result, apiList=def_result, db_design=[db_result])
    return spec_upload_request
