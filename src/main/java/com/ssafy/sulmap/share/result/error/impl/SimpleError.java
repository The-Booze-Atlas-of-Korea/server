package com.ssafy.sulmap.share.result.error.impl;

import com.ssafy.sulmap.share.result.error.ResultError;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Getter
@ToString
public class SimpleError implements ResultError {
    private final String code;
    private final String message;
    private final Map<String, Object> metadata;
    private final Throwable cause;

    public SimpleError(String code, String message) {
        this(code, message, Collections.emptyMap(), null);
    }

    public SimpleError(String code, String message, Map<String, Object> metadata) {
        this(code, message, metadata, null);
    }

    public SimpleError(String code, String message, Map<String, Object> metadata, Throwable cause) {
        this.code = code;
        this.message = message;
        this.metadata = metadata == null ? Collections.emptyMap() : Map.copyOf(metadata);
        this.cause = cause;
    }
}