package com.ssafy.sulmap.infra.utils;

import com.ssafy.sulmap.core.model.BarListItemModel;
import com.ssafy.sulmap.core.model.UserModel;
import com.ssafy.sulmap.core.model.query.GetRecommenedBarsQuery; // 패키지 맞춰서 수정

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public final class GptBatchTextBuilder {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private GptBatchTextBuilder() {}

    /**
     * CTX|g={G}|a={A}|ts={ISO8601}|w={W}|md={MAX_DIST_M}|q={USER_PROMPT}
     */
    public static String buildCtx(UserModel user, GetRecommenedBarsQuery query, ZonedDateTime requestTime) {
        Objects.requireNonNull(query, "query");
        Objects.requireNonNull(requestTime, "requestTime");

        String g = genderCode(user != null ? user.getGender() : null);
        String a = ageString(user != null ? user.getBirthday() : null, requestTime);
        String ts = ISO_FMT.format(requestTime);

        String w = sanitize(nullToDash(query.getWeatherKey()), 32);
        String q = sanitize(nullToEmpty(query.getUserPrompt()), 180);

        int md = query.getMaxDistance() == null ? 0 : query.getMaxDistance();

        return "CTX"
                + "|g=" + g
                + "|a=" + a
                + "|ts=" + ts
                + "|w=" + w
                + "|md=" + md
                + "|q=" + q;
    }

    /**
     * 후보 라인(B|...)을 여러 줄로 생성.
     * 현재 BarListItemModel에 거리/영업여부가 없으니,
     * baseCategoryName + openInformation + name(짧게) 중심으로 넣는다.
     *
     * 포맷:
     * B|id={id}|c={baseCategory}|oi={openInfo}|n={name}
     */
    public static String buildBatchLines(List<BarListItemModel> batchModels) {
        if (batchModels == null || batchModels.isEmpty()) return "";

        return batchModels.stream()
                .map(GptBatchTextBuilder::buildOneBarLine)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining("\n"));
    }

    /**
     * 전체 입력 텍스트(CTX + B라인들)
     */
    public static String buildInputText(List<BarListItemModel> models, UserModel user, GetRecommenedBarsQuery query) {
        String ctx = buildCtx(user, query, ZonedDateTime.now(ZONE));
        String batch = buildBatchLines(models);
        return ctx + "\n" + batch;
    }

    private static String buildOneBarLine(BarListItemModel bar) {
        if (bar == null || bar.getId() == null) return "";

        String c = sanitize(nullToDash(bar.getBaseCategoryName()), 10);

        // openInformation은 길어질 수 있으니 아주 짧게 잘라서 사용
        // (원문을 그대로 쓰면 토큰 폭발 위험)
        String oi = sanitize(nullToDash(bar.getOpenInformation()), 60);

        // 이름은 추천 품질에 도움은 되지만 길어질 수 있어 제한
        String n = sanitize(nullToDash(bar.getName()), 20);

        String menu = sanitize(nullToDash(bar.getMenuJsonString()), 60);

        return "B|id=" + bar.getId()
                + "|c=" + c
                + "|oi=" + oi
                + "|n=" + n
                + "|menu=" + menu;
    }

    // -------------------------
    // Utils
    // -------------------------

    private static String genderCode(Object genderEnum) {
        if (genderEnum == null) return "U";
        String s = genderEnum.toString().toUpperCase(Locale.ROOT);
        if (s.contains("MALE") || s.equals("M")) return "M";
        if (s.contains("FEMALE") || s.equals("F")) return "F";
        return "U";
    }

    private static String ageString(Date birthday, ZonedDateTime requestTime) {
        if (birthday == null) return "?";
        LocalDate birth = birthday.toInstant().atZone(requestTime.getZone()).toLocalDate();
        LocalDate today = requestTime.toLocalDate();
        int years = Period.between(birth, today).getYears();
        if (years < 0 || years > 130) return "?";
        return Integer.toString(years);
    }

    private static String sanitize(String s, int maxLen) {
        if (s == null) return "";
        String out = s.replace('|', ' ')
                .replace('\n', ' ')
                .replace('\r', ' ')
                .trim();
        if (out.length() > maxLen) out = out.substring(0, maxLen);
        return out;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private static String nullToDash(String s) {
        return s == null || s.isBlank() ? "-" : s;
    }

}

