package com.ssafy.sulmap.share.result.error.impl;

public final class ConflictError extends SimpleError {
    public ConflictError(String message) {
        super(407, message);
    }
}
