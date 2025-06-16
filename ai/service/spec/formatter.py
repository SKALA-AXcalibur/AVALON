# ai/service/spec/formatter.py

from service.spec.requirement_parser import RequirementParserService
from service.spec.interface_impl_parser import InterfaceImplParserService
from service.spec.interface_def_parser import InterfaceDefParserService

from dto.request.spec.spec_upload_request import SpecUploadRequest

def formatter(
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

    # db_result = await parse_db_design(databaseDesign)
    
    spec_upload_request = SpecUploadRequest(requirement=requirements, apiList=def_result, )
    return spec_upload_request
