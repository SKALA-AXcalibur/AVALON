package com.sk.skala.axcalibur.spec.global.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * [공통 코드] API 통신에 대한 '에러 코드'를 Enum 형태로 관리를 한다.
 * Global Error CodeList : 전역으로 발생하는 에러코드를 관리한다.
 * Custom Error CodeList : 업무 페이지에서 발생하는 에러코드를 관리한다
 * Error Code Constructor : 에러코드를 직접적으로 사용하기 위한 생성자를 구성한다.
 *
 * @author dig0214
 */
@Getter
public enum ErrorCode {
	/**
	 * ******************************* Global Error CodeList ***************************************
	 * HTTP Status Code
	 * 400 : Bad Request
	 * 401 : Unauthorized
	 * 403 : Forbidden
	 * 404 : Not Found
	 * 500 : Internal Server Error
	 * *********************************************************************************************
	 */
	// 잘못된 서버 요청
	BAD_REQUEST_ERROR(400, "G001", "Bad Request Exception"),

	// @RequestBody 데이터 미 존재
	REQUEST_BODY_MISSING_ERROR(400, "G002", "Required request body is missing"),

	// 유효하지 않은 타입
	INVALID_TYPE_VALUE(400, "G003", " Invalid Type Value"),

	// Request Parameter 로 데이터가 전달되지 않을 경우
	MISSING_REQUEST_PARAMETER_ERROR(400, "G004", "Missing Servlet RequestParameter Exception"),

	// 입력/출력 값이 유효하지 않음
	IO_ERROR(400, "G005", "I/O Exception"),

	// com.google.gson JSON 파싱 실패
	JSON_PARSE_ERROR(400, "G006", "JsonParseException"),

	// com.fasterxml.jackson.core Processing Error
	JACKSON_PROCESS_ERROR(400, "G007", "JsonProcessingException"),

	// 권한이 없음
	FORBIDDEN_ERROR(403, "G008", "Forbidden Exception"),

	// 서버로 요청한 리소스가 존재하지 않음
	NOT_FOUND_ERROR(404, "G009", "Not Found Exception"),

	// NULL Point Exception 발생
	NULL_POINT_ERROR(404, "G010", "Null Point Exception"),

	// @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
	NOT_VALID_ERROR(400, "G011", "handle Validation Exception"),

	// @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
	NOT_VALID_HEADER_ERROR(404, "G012", "Header에 데이터가 존재하지 않는 경우 "),

	// @Cookie 값이 유효하지 않는 경우
	NOT_VALID_COOKIE_ERROR(404, "G013", "Cookie에 데이터가 존재하지 않는 경우 "),

	// MultipartFile이 유효하지 않는 경우
	NOT_VALID_MULTIPART_ERROR(400, "G014", "MultipartFile에 데이터가 존재하지 않는 경우 "),

	// 서버가 처리 할 방법을 모르는 경우 발생
	INTERNAL_SERVER_ERROR(500, "G999", "Internal Server Error Exception"),



	/**
	 * ******************************* Custom Error CodeList ***************************************
	 */
	// 파일 관련 오류
	FILE_EMPTY(400, "FILE001", "저장할 파일이 비어 있습니다."),
    INVALID_FILE_NAME(400, "FILE002", "유효하지 않은 파일 이름입니다."),
    FILE_STORAGE_ERROR(500, "FILE003", "파일 저장 또는 처리 중 오류가 발생했습니다."),
    FILE_DELETE_FAILED(500, "FILE004", "파일 삭제에 실패했습니다."),
    ACCESS_DENIED(403, "FILE005", "파일 접근 권한이 없습니다."),

	// 프로젝트 관련 오류
    PROJECT_NOT_FOUND(404, "PJT001", "프로젝트를 찾을 수 없습니다."),

	// 데이터베이스 작업 실패
	DATABASE_OPERATION_FAILED(500, "DB001", "Database Connection Exception"),

	SAMPLE_INPUT_ERROR(400, "SM001", "에러 샘플 1"),
	SAMPLE_PROCESSING_ERROR(500, "SM002", "에러 샘플 2")







	; // End
	/**
	 * ******************************* Error Code Constructor ***************************************
	 */
	// 에러 코드의 '코드 상태'을 반환한다.
	private final int status;

	// 에러 코드의 '코드간 구분 값'을 반환한다.
	private final String divisionCode;

	// 에러 코드의 '코드 메시지'을 반환한다.
	private final String message;

	// 생성자 구성
	ErrorCode(final int status, final String divisionCode, final String message) {
		this.status = status;
		this.divisionCode = divisionCode;
		this.message = message;
	}
}
