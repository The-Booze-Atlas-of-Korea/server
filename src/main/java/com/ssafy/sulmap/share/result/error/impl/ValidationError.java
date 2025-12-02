package com.ssafy.sulmap.share.result.error.impl;

public final class ValidationError extends SimpleError {
    public ValidationError(String message) {
        super(400, message);
    }
}