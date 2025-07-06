import { SuccessResponse, ErrorResponse } from "@/types/api";

export function isSuccessResponse<T>(
  response: unknown
): response is SuccessResponse<T> {
  return (
    typeof response === "object" &&
    response !== null &&
    "data" in response &&
    "status" in response &&
    "message" in response
  );
}

export function isErrorResponse(response: unknown): response is ErrorResponse {
  return (
    typeof response === "object" &&
    response !== null &&
    "status" in response &&
    "divisionCode" in response &&
    "resultMsg" in response &&
    "errors" in response &&
    "reason" in response
  );
}

export function handleApiResponse<T>(
  response: SuccessResponse<T> | ErrorResponse
): T {
  if (isErrorResponse(response)) {
    throw new Error(response.resultMsg || "알 수 없는 오류가 발생했습니다.");
  }

  if (isSuccessResponse<T>(response)) {
    return response.data;
  }

  throw new Error("잘못된 응답 형식입니다.");
}
