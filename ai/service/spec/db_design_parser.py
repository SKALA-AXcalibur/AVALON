# ai/service/spec/requirement_parser.py

import openpyxl
from fastapi import UploadFile
from io import BytesIO
from dto.request.spec.db import DbDesignDto, ColumnDto 
from typing import List


class DbDesignParserService:

    def find_custom_header_row(self, sheet, required_keywords):
        for idx, row in enumerate(sheet.iter_rows(values_only=True), start=1):
            row_values = [str(cell) if cell is not None else "" for cell in row]
            if all(any(keyword in cell for cell in row_values) for keyword in required_keywords):
                return idx, row
        return None, None

    def get_header_map(self, header_row):
        header_map = {
            "테이블 한글명": "name",
            "컬럼 한글명": "col_name",
            "데이터타입": "type",
            "길이": "length",
            "PK": "isPk",
            "FK": "fk",
            "Null여부": "isNull",
            "제약 조건": "constraint",
            "설명": "desc"
        }
        return {
            i: header_map.get(str(header_row[i]), str(header_row[i]))
            for i in range(len(header_row))
        }

    async def parse_dbdesign_file(self, upload_file: UploadFile) -> DbDesignDto:
        contents = await upload_file.read()
        workbook = openpyxl.load_workbook(BytesIO(contents), data_only=True)
        sheet = workbook["테이블정의서(컬럼)"]

        required_keywords = ["Null여부", "데이터타입"]
        header_idx, header_row = self.find_custom_header_row(sheet, required_keywords)
        if header_row is None:
            raise ValueError("헤더를 찾을 수 없습니다.")

        header_map = self.get_header_map(header_row)

        columns: List[ColumnDto] = []
        table_name = None

        for idx, row in enumerate(sheet.iter_rows(values_only=True), start=1):
            if idx <= header_idx:
                continue
            if all(cell is None for cell in row):
                continue

            row_dict = {header_map[i]: row[i] for i in range(len(header_row))}

            # 테이블명은 첫 행 기준으로만 추출
            if table_name is None:
                table_name = str(row_dict.get("name", "UNKNOWN_TABLE"))

            column_data = {
                "col_name": str(row_dict.get("col_name") or ""),
                "type": str(row_dict.get("type") or ""),
                "length": str(row_dict.get("length")) if row_dict.get("length") is not None else None,
                "isPk": "Y" if str(row_dict.get("isPk")).strip().upper() == "Y" else "N",
                "fk": str(row_dict.get("fk")) if row_dict.get("fk") else None,
                "isNull": "Y" if str(row_dict.get("isNull")).strip().upper() != "N" else "N",
                "constraint": str(row_dict.get("constraint")) if row_dict.get("constraint") else None,
                "desc": str(row_dict.get("desc")) if row_dict.get("desc") else None
            }

            columns.append(ColumnDto(**column_data))

        return DbDesignDto(name=table_name, columns=columns)