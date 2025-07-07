package com.sk.skala.axcalibur.apitest.global.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HttpStartLine {

  /**
   * HTTP Start Line을 생성합니다.
   * @param req HttpServletRequest 객체
   * @return HTTP Start Line 문자열
   */
  public static String get(HttpServletRequest req) {
    StringBuilder sb = new StringBuilder();

    // HTTP Method
    sb.append(req.getMethod()).append(" ");

    // Request URI + query string
    sb.append(req.getRequestURI());
    if (req.getQueryString() != null) {
      sb.append("?").append(req.getQueryString());
    }
    sb.append(" ");

    // HTTP Version
    sb.append(req.getProtocol());

    return sb.toString();
  }

  public static String get(HttpServletRequest req, HttpServletResponse res) {
    StringBuilder sb = new StringBuilder();

    // HTTP Version
    sb.append(req.getProtocol()).append(" ");

    // Status Code
    sb.append(res.getStatus()).append(" ");

    // URI + query string
    sb.append(req.getRequestURI());
    if (req.getQueryString() != null) {
      sb.append("?").append(req.getQueryString());
    }

    return sb.toString();
  }

}
