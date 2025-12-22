package com.ssafy.sulmap.infra;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.FailoverIntrospector;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.ssafy.sulmap.core.model.BarListItemModel;
import com.ssafy.sulmap.core.model.BarModel;
import com.ssafy.sulmap.core.model.query.NearbyBarsQuery;
import com.ssafy.sulmap.infra.external.elastic.BarSearchElasticClient;
import com.ssafy.sulmap.infra.mapper.BarMapper;
import com.ssafy.sulmap.infra.model.BarCategoryEntity;
import com.ssafy.sulmap.infra.model.BarEntity;
import com.ssafy.sulmap.infra.model.BarSearchElasticEntity;
import com.ssafy.sulmap.infra.repository.BarRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BarRepositoryImplTest {

    @Mock
    private BarMapper _barMapper;

    @Mock
    private BarSearchElasticClient _barSearchElasticClient;

    @InjectMocks
    private BarRepositoryImpl _barRepository;

    /**
     * record/immutable(세터 없음)에서도 잘 생성되도록 Failover Introspector 구성
     * - ConstructorPropertiesArbitraryIntrospector: 생성자 기반
     * - FieldReflectionArbitraryIntrospector: 필드 리플렉션 기반 fallback
     */
    private final FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(new FailoverIntrospector(List.of(
                    ConstructorPropertiesArbitraryIntrospector.INSTANCE,
                    FieldReflectionArbitraryIntrospector.INSTANCE
            )))
            .build();

    @Test
    @DisplayName("findById: bar가 없으면 Optional.empty를 반환하고 카테고리 조회는 하지 않는다")
    void findById_notFound_returnsEmpty_andDoesNotQueryCategories() {
        // arrange
        long barId = fixtureMonkey.giveMeOne(Long.class);

        when(_barMapper.selectById(barId)).thenReturn(Optional.empty());

        // act
        Optional<BarModel> result = _barRepository.findById(barId);

        // assert
        assertTrue(result.isEmpty());

        verify(_barMapper).selectById(barId);
        verify(_barMapper, never()).selectBarCategory(anyLong());
        verifyNoInteractions(_barSearchElasticClient);
    }

    @Test
    @DisplayName("findById: bar가 있으면 카테고리를 함께 조회하고 BarModel로 변환해 반환한다")
    void findById_found_returnsModel_withCategories() {
        // arrange
        long barId = fixtureMonkey.giveMeOne(Long.class);

        // BarEntity는 toBarModel 로직 자체를 테스트하지 않기 위해 mock 처리
        BarEntity barEntity = mock(BarEntity.class);

        // 카테고리 목록은 FixtureMonkey로 생성
        List<BarCategoryEntity> categoryEntities = fixtureMonkey.giveMe(BarCategoryEntity.class, 3);

        // 반환될 BarModel도 FixtureMonkey로 생성 (record여도 위 설정이면 생성됨)
        BarModel expectedModel = fixtureMonkey.giveMeOne(BarModel.class);

        when(_barMapper.selectById(barId)).thenReturn(Optional.of(barEntity));
        when(_barMapper.selectBarCategory(barId)).thenReturn(categoryEntities);
        when(barEntity.toBarModel(categoryEntities)).thenReturn(expectedModel);

        // act
        Optional<BarModel> result = _barRepository.findById(barId);

        // assert
        assertTrue(result.isPresent());
        assertSame(expectedModel, result.get());

        verify(_barMapper).selectById(barId);
        verify(_barMapper).selectBarCategory(barId);
        verify(barEntity).toBarModel(categoryEntities);
        verifyNoInteractions(_barSearchElasticClient);
    }

    @Test
    @DisplayName("findNearby: Elastic 결과를 BarListItemModel 리스트로 매핑해서 반환한다")
    void findNearby_mapsElasticEntitiesToBarListItemModels() throws IOException {
        // arrange
        double lat = fixtureMonkey.giveMeOne(Double.class);
        double lon = fixtureMonkey.giveMeOne(Double.class);
        int radius = 700;
        int count = 2;

        // NearbyBarsQuery가 record라면 직접 new 하는 게 제일 안전함(값은 fixture로 생성)
        NearbyBarsQuery query = new NearbyBarsQuery(
                lat, lon, radius, count,
                "이자카야", "주점", "distance"
        );

        BarSearchElasticEntity e1 = mock(BarSearchElasticEntity.class);
        BarSearchElasticEntity e2 = mock(BarSearchElasticEntity.class);

        BarListItemModel m1 = fixtureMonkey.giveMeOne(BarListItemModel.class);
        BarListItemModel m2 = fixtureMonkey.giveMeOne(BarListItemModel.class);

        when(_barSearchElasticClient.findNearby(query)).thenReturn(List.of(e1, e2));
        when(e1.toBarListItemModel()).thenReturn(m1);
        when(e2.toBarListItemModel()).thenReturn(m2);

        // act
        List<BarListItemModel> result = _barRepository.findNearby(query);

        // assert
        assertEquals(2, result.size());
        assertEquals(List.of(m1, m2), result);

        verify(_barSearchElasticClient).findNearby(query);
        verify(e1).toBarListItemModel();
        verify(e2).toBarListItemModel();
        verifyNoInteractions(_barMapper);
    }

    @Test
    @DisplayName("findNearby: ElasticClient에서 IOException이 발생하면 그대로 전파한다")
    void findNearby_whenElasticThrows_propagatesIOException() throws IOException {
        // arrange
        double lat = fixtureMonkey.giveMeOne(Double.class);
        double lon = fixtureMonkey.giveMeOne(Double.class);

        NearbyBarsQuery query = new NearbyBarsQuery(
                lat, lon, 500, 10,
                null, null, "distance"
        );

        when(_barSearchElasticClient.findNearby(query))
                .thenThrow(new IOException("boom"));

        // act & assert
        assertThrows(IOException.class, () -> _barRepository.findNearby(query));

        verify(_barSearchElasticClient).findNearby(query);
        verifyNoInteractions(_barMapper);
    }
}

