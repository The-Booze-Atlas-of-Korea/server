package com.ssafy.sulmap.infra.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

public final class MenuJsonHelper {

    private static final ObjectMapper OM = new ObjectMapper();
    private static final TypeReference<List<String>> LIST_STRING = new TypeReference<>() {};

    private MenuJsonHelper() {}

    /**
     * menuJsonString: 예) ["a","b","c"]
     * @return 파싱 실패/빈값이면 빈 리스트 반환
     */
    public static List<String> parseMenuJsonString(String menuJsonString) {
        if (menuJsonString == null) return Collections.emptyList();
        String s = menuJsonString.trim();
        if (s.isEmpty() || "null".equalsIgnoreCase(s)) return Collections.emptyList();

        try {
            List<String> result = OM.readValue(s, LIST_STRING);
            return result == null ? Collections.emptyList() : result;
        } catch (Exception e) {
            // 여기서 예외를 던지고 싶으면 throw new IllegalArgumentException(...) 로 바꿔도 됨
            return Collections.emptyList();
        }
    }

    /**
     * 역방향도 필요하면 사용: List<String> -> ["a","b","c"]
     */
    public static String toMenuJsonString(List<String> menuList) {
        if (menuList == null) return "[]";
        try {
            return OM.writeValueAsString(menuList);
        } catch (Exception e) {
            return "[]";
        }
    }
}

