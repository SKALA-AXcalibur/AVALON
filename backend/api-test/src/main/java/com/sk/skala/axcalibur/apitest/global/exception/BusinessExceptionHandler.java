package com.sk.skala.axcalibur.apitest.global.exception;

import com.sk.skala.axcalibur.apitest.global.code.ErrorCode;
import lombok.Builder;
import lombok.Getter;

/**
 * 에러를 사용하기 위한 구현체
 */

public class BusinessExceptionHandler extends RuntimeException {
	@Getter
	private final ErrorCode errorCode;

	@Builder
	public BusinessExceptionHandler(String message, ErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	@Builder
	public BusinessExceptionHandler(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	@Builder
	public BusinessExceptionHandler(String message, ErrorCode errorCode, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

}
