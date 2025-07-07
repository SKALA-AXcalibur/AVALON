package com.sk.skala.axcalibur.spec.global.response;

import com.sk.skala.axcalibur.spec.global.code.SuccessCode;
import lombok.Builder;
import lombok.Getter;

/**
 *  API Response 결과의 반환 값을 관리
 */
@Getter
public class SuccessResponse<T> {
	// API 응답 결과 Response
	private final T data;

	// API 응답 코드 Response
	private final SuccessCode status;

	// API 응답 코드 Message
	private final String message;

	@Builder
	// public SuccessResponse(final T data, final int status, final String message) {
	public SuccessResponse(final T data, final SuccessCode status, final String message) {
		this.data = data;
		this.status = status;
		this.message = message;
	}
}
