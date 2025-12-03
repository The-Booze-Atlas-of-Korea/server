package com.ssafy.sulmap.share.result.error.impl;

import java.util.Map;

public class ServerError extends SimpleError{
    public ServerError(String resourceName, Object key) {
        super(
                500,
                resourceName + " something wrong with: " + key,
                Map.of("resource", resourceName, "key", key)
        );
    }
}
