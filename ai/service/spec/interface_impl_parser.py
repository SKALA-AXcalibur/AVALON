# ai/service/spec/interface_impl_parser.py
import pandas as pd
from fastapi import UploadFile
from typing import List, Dict, Optional
from io import BytesIO
import logging

from config.config import (
    IMPL_PARAM,
    PATH_QUERY_PARAM,
    REQUEST_PARAM,
    RESPONSE_PARAM,
    IMPL_FIELD_MAP,
)
from dto.request.spec.api import Api
from dto.request.spec.param import Param


class InterfaceImplParserService:
    """
    인터페이스 설계서 파일을 파싱하여 API 정보 리스트로 변환 수행하는 서비스 클래스
    """

    async def parse_interface_file(self, upload_file: UploadFile) -> List[Api]:
        """
        FastAPI UploadFile 객체를 입력받아 모든 유효 시트를 파싱하여 Api 객체 리스트로 반환하는 함수
        """
        contents = await upload_file.read()
        excel_bytes = BytesIO(contents)
        xls = pd.ExcelFile(excel_bytes)

        sheet_starts = self.find_valid_sheet_starts(xls)
        parsed_apis = []

        for sheet_name in sheet_starts.keys():
            df = xls.parse(sheet_name=sheet_name, header=None)
            api = self.parse_interface_sheet(df)
            if api:
                parsed_apis.append(api)

        return parsed_apis

    def find_valid_sheet_starts(self, xls: pd.ExcelFile) -> Dict[str, int]:
        """
        각 시트에서 '*** 입출력 파라미터 명세 ***'가 위치한 행 번호를 탐색하는 함수(실제 API 명세 시트 구분 위함)
        """
        valid_starts = {}

        for sheet_name in xls.sheet_names:
            df = xls.parse(sheet_name=sheet_name, header=None)
            for i, row in df.iterrows():
                if any(isinstance(cell, str) and IMPL_PARAM in cell for cell in row):
                    valid_starts[sheet_name] = i
                    break

        return valid_starts

    def find_value_to_right(
        self, df: pd.DataFrame, keyword: str, offset: int = 1
    ) -> str:
        """
        특정 키워드가 포함된 셀의 오른쪽 지정 offset만큼 떨어진 셀의 값을 반환하는 함수
        """
        for i, row in df.iterrows():
            for j, cell in enumerate(row):
                if isinstance(cell, str) and keyword in cell:
                    try:
                        value = row[j + offset]
                        return str(value).strip() if pd.notna(value) else ""
                    except IndexError:
                        return ""
        return ""

    def find_keyword_row(self, df: pd.DataFrame, keyword: str) -> int:
        """
        특정 키워드가 포함된 행의 인덱스를 반환하는 함수
        """
        for i, row in df.iterrows():
            if any(isinstance(cell, str) and keyword in cell for cell in row):
                return i
        return -1

    def _safe_int_conversion(self, value, default: int = 0) -> int:
        """
        안전한 정수 변환 함수
        """
        if pd.isna(value):
            return default
        try:
            return int(value)
        except (ValueError, TypeError):
            return default

    def parse_param_block(self, df: pd.DataFrame, start: int, end: int) -> List[Param]:
        """
        파라미터 블록 영역을 순회하며 Param 객체 리스트로 파싱하는 함수
        """
        params = []
        for i in range(start, end):
            row = df.iloc[i]
            if pd.isna(row[0]) or pd.isna(row[1]):
                break
            try:
                param = Param(
                    nameKo=str(row[1]).strip(),
                    name=str(row[2]).strip(),
                    itemType=str(row[3]).strip(),
                    step=self._safe_int_conversion(row[4], 0),
                    dataType=str(row[5]).strip(),
                    length=(
                        self._safe_int_conversion(row[6], None)
                        if not pd.isna(row[6])
                        else None
                    ),
                    format=str(row[7]).strip() if not pd.isna(row[7]) else None,
                    defaultValue=(str(row[8]).strip() if not pd.isna(row[8]) else None),
                    required=str(row[9]).strip().upper() == "Y",
                    upper=str(row[10]).strip() if not pd.isna(row[10]) else None,
                    desc=str(row[11]).strip() if not pd.isna(row[11]) else None,
                )
                params.append(param)
            except (ValueError, IndexError, AttributeError) as e:
                logging.warning(f"[파싱 실패] {i}행 - {type(e).__name__}: {e}")
                continue
        return params

    def parse_interface_sheet(self, df: pd.DataFrame) -> Optional[Api]:
        """
        하나의 시트에서 API 메타 정보 및 파라미터 블록을 파싱하여 Api 객체로 반환하는 함수
        """
        values = {}
        missing_fields = []

        for key, (keyword, offset) in IMPL_FIELD_MAP.items():
            val = self.find_value_to_right(df, keyword, offset)
            values[key] = val
            if not val and key in {"id", "name", "method", "path"}:
                missing_fields.append(keyword)

        if missing_fields:
            logging.warning(
                f"필수 필드 누락: {', '.join(missing_fields)} "
                f"(ID={values['id']}, name={values['name']}, method={values['method']}, path={values['path']})"
            )
            return None

        path_row = self.find_keyword_row(df, PATH_QUERY_PARAM)
        request_row = self.find_keyword_row(df, REQUEST_PARAM)
        response_row = self.find_keyword_row(df, RESPONSE_PARAM)

        if path_row == -1 or request_row == -1 or response_row == -1:
            logging.warning(
                f"필수 파라미터 블록 위치를 찾지 못함 (Path: {path_row}, Req: {request_row}, Res: {response_row})"
            )
            return None

        path_params = self.parse_param_block(df, path_row + 1, request_row)
        req_params = self.parse_param_block(df, request_row + 1, response_row)
        res_params = self.parse_param_block(df, response_row + 1, len(df))

        return Api(
            id=values["id"],
            name=values["name"],
            desc=values.get("desc", ""),
            method=values["method"],
            url=values.get("url", None),
            path=values["path"],
            reqId=None,  # reqId는 정의서에서 매핑됨
            pathQuery=path_params,
            request=req_params,
            response=res_params,
        )
