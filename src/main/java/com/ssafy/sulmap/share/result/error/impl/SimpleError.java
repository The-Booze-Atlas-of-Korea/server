package com.ssafy.sulmap.share.result.error.impl;

import com.ssafy.sulmap.share.result.error.ResultError;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Builder
@Getter
@ToString
@RequiredArgsConstructor
public class SimpleError implements ResultError {
    private final int code;
    private final String message;
    private final Map<String, Object> metadata;
    private final Throwable cause;

    public SimpleError(int code, String message) {
        this(code, message, Collections.emptyMap(), null);
    }

    public SimpleError(int code, String message, Map<String, Object> metadata) {
        this(code, message, metadata, null);
    }

}