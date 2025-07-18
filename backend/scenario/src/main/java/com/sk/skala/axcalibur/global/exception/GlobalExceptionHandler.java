package com.sk.skala.axcalibur.global.exception;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.response.ErrorResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Controller 내에서 발생하는 Exception 대해서 Catch 하여 응답값(Response)을 보내주는 기능 수행
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * [Exception] API 호출 시 '객체' 혹은 '파라미터' 데이터 값이 유효하지 않은 경우
	 *
	 * @param ex MethodArgumentNotValidException
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		log.error("handleMethodArgumentNotValidException", ex);
		BindingResult bindingResult = ex.getBindingResult();
		StringBuilder stringBuilder = new StringBuilder();
		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			stringBuilder.append(fieldError.getField()).append(":");
			stringBuilder.append(fieldError.getDefaultMessage());
			stringBuilder.append(", ");
		}
		final ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_VALID_ERROR, String.valueOf(stringBuilder));
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	/**
	 * [Exception] API 호출 시 'Header' 내에 데이터 값이 유효하지 않은 경우
	 *
	 * @param ex MissingRequestHeaderException
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(MissingRequestHeaderException.class)
	protected ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
		log.error("MissingRequestHeaderException", ex);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.REQUEST_BODY_MISSING_ERROR, ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	/**
	 * [Exception] 클라이언트에서 Body로 '객체' 데이터가 넘어오지 않았을 경우
	 *
	 * @param ex HttpMessageNotReadableException
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
		HttpMessageNotReadableException ex) {
		log.error("HttpMessageNotReadableException", ex);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.REQUEST_BODY_MISSING_ERROR, ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	/**
	 * [Exception] 클라이언트에서 request로 '파라미터로' 데이터가 넘어오지 않았을 경우
	 *
	 * @param ex MissingServletRequestParameterException
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	protected ResponseEntity<ErrorResponse> handleMissingRequestHeaderExceptionException(
		MissingServletRequestParameterException ex) {
		log.error("handleMissingServletRequestParameterException", ex);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.MISSING_REQUEST_PARAMETER_ERROR, ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}


	/**
	 * [Exception] 잘못된 서버 요청일 경우 발생한 경우
	 *
	 * @param e HttpClientErrorException
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(HttpClientErrorException.BadRequest.class)
	protected ResponseEntity<ErrorResponse> handleBadRequestException(HttpClientErrorException e) {
		log.error("HttpClientErrorException.BadRequest", e);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.BAD_REQUEST_ERROR, e.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}


	/**
	 * [Exception] 잘못된 주소로 요청 한 경우
	 *
	 * @param e NoHandlerFoundException
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(NoHandlerFoundException.class)
	protected ResponseEntity<ErrorResponse> handleNoHandlerFoundExceptionException(NoHandlerFoundException e) {
		log.error("handleNoHandlerFoundExceptionException", e);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_FOUND_ERROR, e.getMessage());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}


	/**
	 * [Exception] NULL 값이 발생한 경우
	 *
	 * @param e NullPointerException
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(NullPointerException.class)
	protected ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException e) {
		log.error("handleNullPointerException", e);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.NULL_POINT_ERROR, e.getMessage());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	/**
	 * Input / Output 내에서 발생한 경우
	 *
	 * @param ex IOException
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(IOException.class)
	protected ResponseEntity<ErrorResponse> handleIOException(IOException ex) {
		log.error("handleIOException", ex);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.IO_ERROR, ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}


	/**
	 * com.fasterxml.jackson 내에 Exception 발생하는 경우
	 *
	 * @param ex JsonParseException
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(JsonParseException.class)
	protected ResponseEntity<ErrorResponse> handleJsonParseExceptionException(JsonParseException ex) {
		log.error("handleJsonParseExceptionException", ex);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.JSON_PARSE_ERROR, ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	/**
	 * com.fasterxml.jackson.core 내에 Exception 발생하는 경우
	 *
	 * @param ex JsonProcessingException
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(JsonProcessingException.class)
	protected ResponseEntity<ErrorResponse> handleJsonProcessingException(JsonProcessingException ex) {
		log.error("handleJsonProcessingException", ex);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.REQUEST_BODY_MISSING_ERROR, ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}


	/**
	 * URI는 존재하지만 적절한 자원(html 등)을 찾을 수 없는 경우
	 *
	 * @param ex NoResourceFoundException
	 * @return ResponseEntity<ErrorResponse>
	 * */
	@ExceptionHandler(NoResourceFoundException.class)
	protected ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
		log.error("NoResourceFoundException", ex);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_FOUND_ERROR, ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}


	/**
	 * cookie 값이 없을 때
	 *
	 * @param ex MissingRequestCookieException
	 * @return ResponseEntity<ErrorResponse>
	 * */
	@ExceptionHandler(MissingRequestCookieException.class)
	protected ResponseEntity<ErrorResponse> handleMissingRequestCookieException(MissingRequestCookieException ex) {
		log.error("MissingRequestCookieException", ex);
		final ErrorResponse res = ErrorResponse.of(ErrorCode.NOT_VALID_COOKIE_ERROR, ex.getMessage());
		return ResponseEntity.status(ErrorCode.NOT_VALID_COOKIE_ERROR.getStatus()).body(res);
	}

	/**
	 * jwt 키 오류
	 * @param ex SignatureException
	 * @return ResponseEntity<ErrorResponse>
	 * */
	// @ExceptionHandler(SignatureException.class)
	// protected ResponseEntity<ErrorResponse> handleSignatureException(SignatureException ex) {
	// 	log.error("SignatureException", ex);
	// 	final ErrorResponse response = ErrorResponse.of(ErrorCode.WRONG_TOKEN_ERROR, ex.getMessage());
	// 	return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	// }

	// @ExceptionHandler(MalformedJwtException.class)
	// protected ResponseEntity<ErrorResponse> handleMalformedJwtException(MalformedJwtException ex) {
	// 	log.error("MalformedJwtException", ex);
	// 	final ErrorResponse response = ErrorResponse.of(ErrorCode.SECURITY_TOKEN_ERROR, ex.getMessage());
	// 	return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	// }

	/**
	 * MultipartFile이 유효하지 않은 경우
	 *
	 * @param ex MultipartException
	 * @return ResponseEntity<ErrorResponse>
	 * */
	@ExceptionHandler(MultipartException.class)
	protected ResponseEntity<ErrorResponse> handleMultipartException(MultipartException ex) {
		log.error("MultipartException", ex);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_VALID_MULTIPART_ERROR, ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	// ==================================================================================================================

	/**
	 * [Exception] 모든 Exception 경우 발생
	 *
	 * @param ex Exception
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(Exception.class)
	protected final ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
		log.error("Exception", ex);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(BusinessExceptionHandler.class)
	public ResponseEntity<ErrorResponse> handleCustomException(BusinessExceptionHandler ex) {
		final ErrorResponse response = ErrorResponse.of(ex.getErrorCode(), ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
	}

}
