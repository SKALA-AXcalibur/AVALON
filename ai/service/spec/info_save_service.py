import httpx
from fastapi import HTTPException

async def save_to_info_api(result: dict):
    """
    결과 딕셔너리를 정보저장 api로 전송하는 함수
    """
    url = "/api/project/v1/{projectId}" # 실제 서비스 주소로 변경 필요 (정보저장 api 경로)
    async with httpx.AsyncClient() as client:
        try:
            response = await client.post(url, json=result)
            response.raise_for_status()
            return response.json()
        except httpx.RequestError as e:
            raise HTTPException(status_code=502, detail=f"정보저장API 호출 실패: {str(e)}")
        except httpx.HTTPStatusError as e:
            raise HTTPException(status_code=response.status_code, detail=f"정보저장API 응답 오류: {response.text}")
