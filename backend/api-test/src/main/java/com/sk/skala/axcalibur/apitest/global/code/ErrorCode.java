package com.sk.skala.axcalibur.apitest.global.code;

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
	 * ******************************* Global Error CodeList
	 * ***************************************
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

	// 인증되지 않은 사용자로 요청을 보낸 경우
	UNAUTHORIZED_ERROR(401, "G001", "Unauthorized Exception"),

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

	// Validated으로 검사한 값이 유효하지 않은 경우
	CONSTRAINT_VIOLATION_ERROR(400, "G015", "Validated으로 검사한 값이 유효하지 않은 경우"),

	// 서버가 처리 할 방법을 모르는 경우 발생
	INTERNAL_SERVER_ERROR(500, "G999", "Internal Server Error Exception"),

	/**
	 * ******************************* Custom Error CodeList
	 * ***************************************
	 */
	SAMPLE_INPUT_ERROR(400, "SM001", "에러 샘플 1"),
	SAMPLE_PROCESSING_ERROR(500, "SM002", "에러 샘플 2"),

	SCENARIO_NOT_FOUND_ERROR(404, "TR001", "시나리오를 찾을 수 없음"),
	DESERIALIZE_ERROR(400, "TR002", "ApiTaskDto 역직렬화 실패"),
	UNVALID_TESTCASE_ERROR(404, "TR003", "유효하지 않은 테스트 케이스"),
	PRECONDITION_PARSE_ERROR(500, "TR004", "사전 조건 파싱 실패")

	; // End

	/**
	 * ******************************* Error Code Constructor
	 * ***************************************
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
