# ai/service/spec/interface_impl_parser.py
import pandas as pd
from fastapi import UploadFile
from typing import List, Dict, Optional
from pydantic import BaseModel
from io import BytesIO
from loguru import logger

from dto.request.spec.api import Api
from dto.request.spec.param import Param

class SpecParserService:
    async def parse_interface_file(self, upload_file: UploadFile) -> List[Api]:
        contents = await upload_file.read()
        excel_bytes = BytesIO(contents)
        xls = pd.ExcelFile(excel_bytes)

        sheet_starts = self.find_valid_sheet_starts(xls)
        parsed_apis = []

        for sheet_name, start_row in sheet_starts.items():
            df = xls.parse(sheet_name=sheet_name, header=None)
            api = self.parse_interface_sheet(df, start_row)
            if api:
                parsed_apis.append(api)

        return parsed_apis

    def find_valid_sheet_starts(self, xls: pd.ExcelFile) -> Dict[str, int]:
        valid_starts = {}

        for sheet_name in xls.sheet_names:
            df = xls.parse(sheet_name=sheet_name, header=None)
            for i, row in df.iterrows():
                if any(isinstance(cell, str) and "*** 입출력 파라미터 명세 ***" in cell for cell in row):
                    valid_starts[sheet_name] = i
                    break

        return valid_starts

    def find_value_to_right(self, df: pd.DataFrame, keyword: str, offset: int = 1) -> str:
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
        for i, row in df.iterrows():
            if any(isinstance(cell, str) and keyword in cell for cell in row):
                return i
        return -1

    def parse_param_block(self, df: pd.DataFrame, start: int, end: int) -> List[Param]:
        params = []
        for i in range(start, end):
            row = df.iloc[i]
            if pd.isna(row[0]) or pd.isna(row[1]):
                break
            try:
                param = Param(
                    korName=str(row[1]).strip(),
                    name=str(row[2]).strip(),
                    itemType=str(row[3]).strip(),
                    step=int(row[4]) if not pd.isna(row[4]) else 0,
                    dataType=str(row[5]).strip(),
                    length=int(row[6]) if not pd.isna(row[6]) else None,
                    format=str(row[7]).strip() if not pd.isna(row[7]) else None,
                    default=str(row[8]).strip() if not pd.isna(row[8]) else None,
                    required=str(row[9]).strip().upper() == 'Y',
                    upper=str(row[10]).strip() if not pd.isna(row[10]) else None,
                    desc=str(row[11]).strip() if not pd.isna(row[11]) else None,
                )
                params.append(param)
            except Exception:
                logger.warning(f"[파싱 실패] {i}행 - {e}")
                continue
        return params

    def parse_interface_sheet(self, df: pd.DataFrame, start_row: int) -> Optional[Api]:
        try:
            id_ = self.find_value_to_right(df, "인터페이스ID", offset=2)
            name = self.find_value_to_right(df, "인터페이스명", offset=2)
            desc = self.find_value_to_right(df, "설명", offset=2)
            method = self.find_value_to_right(df, "HTTP Method", offset=1)
            path = self.find_value_to_right(df, "Path", offset=2)
            url = self.find_value_to_right(df, "URL", offset=2)
        
            if not all([id_, name, method, path]):
                logger.warning(f"필수 필드 누락: ID={id_}, name={name}, method={method}, path={path}")
                return None

        except (IndexError, KeyError, ValueError) as e:
            logger.warning(f"API 메타정보 파싱 실패: {e}")
            return None

        path_row = self.find_keyword_row(df, "*** Path / Query 파라미터 항목 ***")
        request_row = self.find_keyword_row(df, "*** 요청(Request) 파라미터 항목 ***")
        response_row = self.find_keyword_row(df, "*** 응답(Response) 파라미터 항목 ***")

        if path_row == -1 or request_row == -1 or response_row == -1:
            logger.warning(f"필수 파라미터 블록 위치를 찾지 못함 (Path: {path_row}, Req: {request_row}, Res: {response_row})")
            return None

        try:
            path_params = self.parse_param_block(df, path_row + 1, request_row)
            req_params = self.parse_param_block(df, request_row + 1, response_row)
            res_params = self.parse_param_block(df, response_row + 1, len(df))

        except Exception as e:
            logger.warning(f"파라미터 블록 파싱 실패: {e}")
            return None
        
        return Api(
            id=id_,
            name=name,
            desc=desc,
            method=method,
            url=url,
            path=path,
            pathQuery=path_params,
            request=req_params,
            response=res_params
        )
