# ai\service\spec\requirement_parser.py
import openpyxl
from fastapi import UploadFile
from io import BytesIO
from dto.request.spec.requirement import Requirement
from dto.request.spec.spec_upload_request import SpecUploadRequest


def find_custom_header_row(sheet, required_keywords):
    for idx, row in enumerate(sheet.iter_rows(values_only=True), start=1):
        row_values = [str(cell) if cell is not None else "" for cell in row]
        if all(
            any(keyword in cell for cell in row_values) for keyword in required_keywords
        ):
            return idx, row
    return None, None


def get_header_map(header_row):
    # 엑셀 헤더명과 Requirement 필드명 매핑
    header_map = {
        "요구사항 ID": "id",
        "요구사항 명": "name",
        "요구사항 설명": "desc",
        "중요도": "priority",
        "대분류": "major",
        "중분류": "middle",
        "소분류": "sub",
    }
    # 실제 헤더에 존재하는 것만 매핑
    return {
        i: header_map.get(str(header_row[i]), str(header_row[i]))
        for i in range(len(header_row))
    }


async def parse_requirement_file(upload_file: UploadFile):
    contents = await upload_file.read()
    workbook = openpyxl.load_workbook(BytesIO(contents), data_only=True)
    sheet = workbook["요구사항정의서"]

    required_keywords = ["대분류", "중분류", "소분류"]
    header_idx, header_row = find_custom_header_row(sheet, required_keywords)
    if header_row is None:
        raise ValueError("헤더(대분류, 중분류, 소분류 포함)를 찾을 수 없습니다.")

    header_map = get_header_map(header_row)
    requirements = []
    for idx, row in enumerate(sheet.iter_rows(values_only=True), start=1):
        if idx <= header_idx:
            continue
        if all(cell is None for cell in row):
            continue
        # 매핑된 필드명으로 딕셔너리 생성
        row_dict = {header_map[i]: row[i] for i in range(len(header_row))}
        # Requirement 객체 생성
        requirement_data = {
            field: row_dict.get(field, None)
            for field in ["id", "name", "desc", "priority", "major", "middle", "sub"]
        }
        requirements.append(Requirement(**requirement_data))

    # spec_upload_request = SpecUploadRequest(requirement=requirements)
    # return spec_upload_request
    return requirements



