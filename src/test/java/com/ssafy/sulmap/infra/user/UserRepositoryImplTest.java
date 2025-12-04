package com.ssafy.sulmap.infra.user;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.ssafy.sulmap.core.model.UserModel;
import com.ssafy.sulmap.core.model.enums.UserAuthProvider;
import com.ssafy.sulmap.core.model.enums.UserGender;
import com.ssafy.sulmap.core.model.enums.UserProfileVisitVisibility;
import com.ssafy.sulmap.core.model.enums.UserStatus;
import com.ssafy.sulmap.infra.mapper.UserMapper;
import com.ssafy.sulmap.infra.model.UserEntity;
import com.ssafy.sulmap.infra.repository.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UserRepositoryImpl 유닛 테스트
 *  - MyBatis UserMapper 는 mock 으로 대체
 *  - DB 없이 Repository 로직만 검증
 */
@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    //faker
    private FixtureMonkey _fixtureMonkey;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserRepositoryImpl userRepository;

    @BeforeEach
    void setUp() {
        _fixtureMonkey = FixtureMonkey.create();
    }
    // ====== save() 테스트 ======

    @Test
    @DisplayName("save - id가 null이면 insert를 호출하고, 매핑된 PK를 리턴한다")
    void save_insert_whenIdIsNull() {
        // given
        UserModel userModel = createUserModel(null);

        // MyBatis insert() 가 PK를 entity.id 에 세팅해주는 동작을 mock 으로 시뮬레이션
        doAnswer(invocation -> {
            UserEntity arg = invocation.getArgument(0);
            arg.setId(1L);        // DB에서 생성된 PK라고 가정
            return 1;             // 영향받은 row 수
        }).when(userMapper).insert(any(UserEntity.class));

        // when
        Long savedId = userRepository.save(userModel);

        // then
        assertThat(savedId).isEqualTo(1L);
        // insert가 1번 호출되고, update는 호출되지 않아야 함
        verify(userMapper, times(1)).insert(any(UserEntity.class));
        verify(userMapper, never()).update(any(UserEntity.class));
    }

    @Test
    @DisplayName("save - id가 있으면 update를 호출하고, 기존 id를 그대로 리턴한다")
    void save_update_whenIdIsNotNull() {
        // given
        Long existingId = 10L;
        UserModel userModel = createUserModel(existingId);

        when(userMapper.update(any(UserEntity.class))).thenReturn(1);

        // when
        Long savedId = userRepository.save(userModel);

        // then
        assertThat(savedId).isEqualTo(existingId);
        verify(userMapper, times(1)).update(any(UserEntity.class));
        verify(userMapper, never()).insert(any(UserEntity.class));
    }

    // ====== findById() 테스트 ======

    @Test
    @DisplayName("findById - 엔티티가 존재하면 UserModel을 Optional로 감싸서 반환한다")
    void findById_returnsUser_whenFound() {
        // given
        Long userId = 1L;
        UserEntity entity = createUserEntity(userId, "testLogin");

        when(userMapper.selectById(userId)).thenReturn(entity);

        // when
        Optional<UserModel> result = userRepository.findById(userId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(userId);
        assertThat(result.get().getLoginId()).isEqualTo("testLogin");
    }

    @Test
    @DisplayName("findById - 엔티티가 없으면 Optional.empty()를 반환한다")
    void findById_returnsEmpty_whenNotFound() {
        // given
        Long userId = 1L;
        when(userMapper.selectById(userId)).thenReturn(null);

        // when
        Optional<UserModel> result = userRepository.findById(userId);

        // then
        assertThat(result).isEmpty();
    }

    // ====== findByLoginId() 테스트 ======

    @Test
    @DisplayName("findByLoginId - 로그인 아이디로 조회가 되면 UserModel을 Optional로 감싸서 반환한다")
    void findByLoginId_returnsUser_whenFound() {
        // given
        String loginId = "tester";
        UserEntity entity = createUserEntity(5L, loginId);

        when(userMapper.selectByLoginId(loginId)).thenReturn(entity);

        // when
        Optional<UserModel> result = userRepository.findByLoginId(loginId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(5L);
        assertThat(result.get().getLoginId()).isEqualTo(loginId);
    }

    @Test
    @DisplayName("findByLoginId - 없으면 Optional.empty()를 반환한다")
    void findByLoginId_returnsEmpty_whenNotFound() {
        // given
        String loginId = "no-user";
        when(userMapper.selectByLoginId(loginId)).thenReturn(null);

        // when
        Optional<UserModel> result = userRepository.findByLoginId(loginId);

        // then
        assertThat(result).isEmpty();
    }

    // ====== 테스트용 헬퍼 메서드 ======

    private UserModel createUserModel(Long id) {
        var model = _fixtureMonkey.giveMeOne(UserModel.class);
        model.setGender(_fixtureMonkey.giveMeOne(UserGender.class));
        model.setAuthProvider(_fixtureMonkey.giveMeOne(UserAuthProvider.class));
        model.setStatus(_fixtureMonkey.giveMeOne(UserStatus.class));
        model.setVisitVisibilitySetting(_fixtureMonkey.giveMeOne(UserProfileVisitVisibility.class));
        model.setId(id);
        return model;
    }

    private UserEntity createUserEntity(Long id, String loginId) {
        var entity = _fixtureMonkey.giveMeOne(UserEntity.class);
        entity.setGender(_fixtureMonkey.giveMeOne(UserGender.class).toString());
        entity.setAuthProvider(_fixtureMonkey.giveMeOne(UserAuthProvider.class).toString());
        entity.setStatus(_fixtureMonkey.giveMeOne(UserStatus.class).toString());
        entity.setVisitVisibilitySetting(_fixtureMonkey.giveMeOne(UserProfileVisitVisibility.class).toString());
        entity.setId(id);
        entity.setLoginId(loginId);
        return entity;
    }
}
