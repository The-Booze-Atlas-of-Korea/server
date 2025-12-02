package com.ssafy.sulmap.share.result.error.impl;

import java.util.Map;

public final class NotFoundError extends SimpleError {
    public NotFoundError(String resourceName, Object key) {
        super(
                404,
                resourceName + " not found for key: " + key,
                Map.of("resource", resourceName, "key", key)
        );
    }
}
