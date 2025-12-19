package com.ssafy.sulmap.core.service.impl;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.instantiator.Instantiator;
import com.ssafy.sulmap.api.dto.request.UpdateUserRequest;
import com.ssafy.sulmap.core.model.BarListItemModel;
import com.ssafy.sulmap.core.model.BarModel;
import com.ssafy.sulmap.core.model.command.CreateUserCommand;
import com.ssafy.sulmap.core.model.command.LoginUserCommand;
import com.ssafy.sulmap.core.model.command.UpdateUserProfileCommand;
import com.ssafy.sulmap.core.model.UserModel;
import com.ssafy.sulmap.core.model.enums.UserGender;
import com.ssafy.sulmap.core.model.enums.UserProfileVisitVisibility;
import com.ssafy.sulmap.core.model.query.NearbyBarsQuery;
import com.ssafy.sulmap.core.repository.BarRepository;
import com.ssafy.sulmap.core.repository.UserRepository;
import com.ssafy.sulmap.share.result.Result;
import com.ssafy.sulmap.share.result.error.impl.ConflictError;
import com.ssafy.sulmap.share.result.error.impl.NotFoundError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;   // ⬅ 추가
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 순수 유닛 테스트:
 * - 스프링 컨텍스트 없이
 * - UserServiceImpl이 UserRepository, PasswordHasher를 어떻게 호출하는지 검증
 */
@DisplayName("BarServiceImplTest 유닛 테스트")
@ExtendWith(MockitoExtension.class)
class BarServiceImplTest {

    //faker
    private FixtureMonkey _fixtureMonkey;

    @Mock
    private BarRepository _barRepository;


    @InjectMocks
    private BarServiceImpl _barService; // UserService 구현체

    @BeforeEach
    void setUp() {
        _fixtureMonkey = FixtureMonkey.builder()
                .defaultNotNull(true)
                .build();
    }

    private NearbyBarsQuery createNearbyBarsQuery() {
        return _fixtureMonkey.giveMeBuilder(NearbyBarsQuery.class)
                .instantiate(Instantiator.constructor()
                        .parameter(Double.class)
                        .parameter(Double.class)
                        .parameter(Integer.class)
                        .parameter(Integer.class)
                        .parameter(String.class)
                        .parameter(String.class)
                        .parameter(String.class)
                )
                .sample();
    }

    //현재 위치 기준으로 주변 술집 검색
    @Test
    @DisplayName("현재 위치 기준으로 주변 술집 검색 성공")
    void findUserByIdForViewer_success() {
        var query = createNearbyBarsQuery();
        var barListItemModel = _fixtureMonkey.giveMeOne(BarListItemModel.class);

        when(_barRepository.findNearby(query)).thenReturn(List.of(barListItemModel));

        var result = _barService.findNearbyBars(query);

        assertTrue(result.isSuccess(), "성공");
        assertTrue(result.getValue().isPresent());
        assertFalse(result.getValue().get().isEmpty());
    }

    //find single bar by bar id
    @Test
    @DisplayName("find single bar by bar id - success")
    void findBarById_success() {
        var barId = 1L;
        var barModel = _fixtureMonkey.giveMeOne(BarModel.class);
        barModel.setId(barId);
        barModel.setDeletedAt(null);

        when(_barRepository.findById(barId)).thenReturn(Optional.of(barModel));

        var result =  _barService.findBarById(barId);

        assertTrue(result.isSuccess());
        assertTrue(result.getValue().isPresent());
        assertEquals(barModel, result.getValue().get());
        assertEquals(barId, result.getValue().get().getId());
    }

    //find single bar by bar id
    @Test
    @DisplayName("find single bar by bar id - fail - not found bar")
    void findBarById_NotFound_returnNotFoundError() {
        var barId = 1L;

        when(_barRepository.findById(barId)).thenReturn(Optional.empty());

        var result =  _barService.findBarById(barId);

        assertTrue(result.isFailure(), "Must be fail");
        assertNotNull(result.getErrors());
        assertFalse(result.getErrors().isEmpty(), "Error list must be not empty.");
        assertInstanceOf(NotFoundError.class, result.getErrors().get(0), "Error list must contains NotFound.");
    }

    //find single bar by bar id
    @Test
    @DisplayName("find single bar by bar id - fail - deleted bar")
    void findBarById_DeletedBar_returnNotFoundError() {
        var barId = 1L;
        var barModel = _fixtureMonkey.giveMeOne(BarModel.class);
        barModel.setId(barId);
        barModel.setDeletedAt(new Date());

        when(_barRepository.findById(barId)).thenReturn(Optional.of(barModel));

        var result =  _barService.findBarById(barId);

        assertTrue(result.isFailure(), "Must be fail");
        assertNotNull(result.getErrors());
        assertFalse(result.getErrors().isEmpty(), "Error list must be not empty.");
        assertInstanceOf(NotFoundError.class, result.getErrors().get(0), "Error list must contains NotFound.");
    }

}
