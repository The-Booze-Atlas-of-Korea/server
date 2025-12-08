package com.ssafy.sulmap.share.result.error;

import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.Optional;

public interface ResultError {
    /** 에러 코드 (e.g. VALIDATION_ERROR, NOT_FOUND …) */
    int getCode();

    /** 사용자/로그용 메시지 */
    String getMessage();

    /** 부가 정보 (필드명, 요청 값 등) */
    Map<String, Object> getMetadata();

    /** 원인이 되는 예외가 있으면 넣어도 됨 */
    default Throwable getCause() {
        return null;
    }

    default HttpStatus getStatus() {
        return HttpStatus.valueOf(getCode());
    }
}