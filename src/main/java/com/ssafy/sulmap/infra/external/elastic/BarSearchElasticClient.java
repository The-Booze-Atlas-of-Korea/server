package com.ssafy.sulmap.infra.external.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.DistanceUnit;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.sulmap.core.model.query.NearbyBarsQuery;
import com.ssafy.sulmap.infra.model.BarSearchElasticEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BarSearchElasticClient {

    private final ElasticsearchClient _elasticsearchClient;
    private final ObjectMapper _objectMapper;
    private final String _indexName = "bars";


    public List<BarSearchElasticEntity> findNearby(NearbyBarsQuery query) throws IOException {
        int size = Math.max(1, query.count());
        String distance = Math.max(1, query.radiusMeters()) + "m";

        SearchResponse<Map> resp = _elasticsearchClient.search(s -> s
                        .index(_indexName)
                        .size(size)
                        .query(q -> q.bool(b -> {
                            // 1) geo_distance filter (반경)
                            b.filter(f -> f.geoDistance(g -> g
                                    .field("location")
                                    .distance(distance)
                                    .location(loc -> loc.latlon(ll -> ll
                                            .lat(query.latitude())
                                            .lon(query.longitude())
                                    ))
                            ));

                            // 2) soft delete 제외 (deleted_at 존재하면 제외)
                            b.mustNot(mn -> mn.exists(e -> e.field("deleted_at")));

                            // 3) category 필터 (base_category_name)
                            if (hasText(query.category())) {
                                b.filter(f -> f.term(t -> t
                                        .field("base_category_name")
                                        .value(query.category())
                                ));
                            }

                            // 4) keyword 검색 (name/address/open_information)
                            if (hasText(query.keyword())) {
                                b.must(m -> m.multiMatch(mm -> mm
                                        .query(query.keyword())
                                        .fields("name^3", "address^1.5", "open_information")
                                        .operator(Operator.And)
                                ));
                            }

                            return b;
                        }))
                        // 정렬
                        .sort(sort -> {
                            String sortKey = query.sort() == null ? "distance" : query.sort().trim().toLowerCase();

                            if ("distance".equals(sortKey)) {
                                // 거리 오름차순
                                sort.geoDistance(g -> g
                                        .field("location")
                                        .location(loc -> loc.latlon(ll -> ll
                                                .lat(query.latitude())
                                                .lon(query.longitude())
                                        ))
                                        .order(SortOrder.Asc)
                                        .unit(DistanceUnit.Meters)
                                );
                            } else {
                                // keyword 있으면 _score 우선
                                sort.score(sc -> sc.order(SortOrder.Desc));

                                // keyword 없으면 보조로 거리라도 안정적으로
                                if (!hasText(query.keyword())) {
                                    sort.geoDistance(g -> g
                                            .field("location")
                                            .location(loc -> loc.latlon(ll -> ll
                                                    .lat(query.latitude())
                                                    .lon(query.longitude())
                                            ))
                                            .order(SortOrder.Asc)
                                            .unit(DistanceUnit.Meters)
                                    );
                                }
                            }
                            return sort;
                        }),
                Map.class
        );

        List<BarSearchElasticEntity> out = new ArrayList<>();
        for (Hit<Map> hit : resp.hits().hits()) {
            Map<String, Object> source = hit.source();
            if (source == null) continue;

            BarSearchElasticEntity entity = mapSourceToEntity(source);

            // distanceMeters: geo_distance sort 걸었을 때 hit.sort() 첫 값으로 보통 들어옴
            entity.setDistanceMeters(extractDistanceMeters(hit));

            out.add(entity);
        }

        return out;
    }

    private BarSearchElasticEntity mapSourceToEntity(Map<String, Object> s) {
        BarSearchElasticEntity e = new BarSearchElasticEntity();

        e.setId(asLong(s.get("id")));
        e.setName(asString(s.get("name")));
        e.setAddress(asString(s.get("address")));

        // ES: base_category_name -> Java: baseCategoryName
        e.setBaseCategoryName(asString(s.get("base_category_name")));

        // ES: open_information -> Java: openInfo
        e.setOpenInfo(asString(s.get("open_information")));

        e.setLatitude(asDouble(s.get("latitude")));
        e.setLongitude(asDouble(s.get("longitude")));

        // menu: object(enabled:false) 이지만 _source에는 남아있음 -> JSON 문자열로 변환
        Object menu = s.get("menu");
        e.setMenuJsonString(toJsonString(menu));

        // created_at / updated_at / deleted_at
        e.setCreatedAt(parseToDate(s.get("created_at")));
        e.setUpdatedAt(parseToDate(s.get("updated_at")));
        e.setDeletedAt(parseToDate(s.get("deleted_at")));

        return e;
    }

    private Double extractDistanceMeters(Hit<?> hit) {
        if (hit.sort() == null || hit.sort().isEmpty()) return null;
        Object v = hit.sort().get(0);
        if (v instanceof Number n) return n.doubleValue();
        return null;
    }

    private String toJsonString(Object v) {
        if (v == null) return null;
        try {
            return _objectMapper.writeValueAsString(v);
        } catch (Exception ex) {
            // 혹시 serialize 실패하면 fallback
            return String.valueOf(v);
        }
    }

    private static boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static String asString(Object v) {
        return v == null ? null : String.valueOf(v);
    }

    private static Long asLong(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        try { return Long.parseLong(String.valueOf(v)); } catch (Exception e) { return null; }
    }

    private static Double asDouble(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(String.valueOf(v)); } catch (Exception e) { return null; }
    }

    private static final DateTimeFormatter FMT1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static Date parseToDate(Object v) {
        if (v == null) return null;

        // epoch millis
        if (v instanceof Number n) {
            return new Date(n.longValue());
        }

        String s = String.valueOf(v).trim();
        if (s.isEmpty() || "null".equalsIgnoreCase(s)) return null;

        // "yyyy-MM-dd HH:mm:ss"
        try {
            LocalDateTime ldt = LocalDateTime.parse(s, FMT1);
            return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        } catch (DateTimeParseException ignore) {}

        // ISO-8601 계열 (strict_date_optional_time 등)
        try {
            Instant inst = Instant.parse(s);
            return Date.from(inst);
        } catch (DateTimeParseException ignore) {}

        // 마지막 fallback: ZonedDateTime / OffsetDateTime
        try {
            return Date.from(OffsetDateTime.parse(s).toInstant());
        } catch (Exception ignore) {}

        return null;
    }
}

