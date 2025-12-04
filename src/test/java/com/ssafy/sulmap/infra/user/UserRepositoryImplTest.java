package com.ssafy.sulmap.infra.user;

import com.ssafy.sulmap.core.command.CreateUserCommand;
import com.ssafy.sulmap.core.command.UpdateUserCommand;
import com.ssafy.sulmap.core.query.FindUserResult;
import com.ssafy.sulmap.infra.mapper.UserMapper;
import com.ssafy.sulmap.infra.model.UserEntity;
import com.ssafy.sulmap.infra.repository.UserRepositoryImpl;
import com.ssafy.sulmap.share.result.error.exception.ResultException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryImplTest {
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserRepositoryImpl userRepository;

    private CreateUserCommand createUserCommand;

    @BeforeEach
    void setUp(){
        createUserCommand = CreateUserCommand.builder()
                .loginId("tester")
                .passwordHash("hashed")
                .name("홍길동")
                .email("tester@example.com")
                .phone("01012345678")
                .address("Seoul")
                .birthday(new Date())
                .gender("M")
                .build();
    }

    @Test
    void create_shouldMapAndReturnGenerateId(){
        ArgumentCaptor<UserEntity> entityCaptor = ArgumentCaptor.forClass(UserEntity.class);
        doAnswer(invocation -> {
            UserEntity captured = invocation.getArgument(0);
            captured.setId(10L);
            return 1;
        }).when(userMapper).insert(any(UserEntity.class));

        Long createdId = userRepository.create(createUserCommand);

        verify(userMapper).insert(entityCaptor.capture());
        UserEntity saved = entityCaptor.getValue();
        assertThat(saved.getLoginId()).isEqualTo(createUserCommand.getLoginId());
        assertThat(saved.getPasswordHash()).isEqualTo(createUserCommand.getPasswordHash());
        assertThat(saved.getBirthday()).isEqualTo(createUserCommand.getBirthday());
        assertThat(saved.getStatus()).isEqualTo("ACTIVE");
        assertThat(saved.getVisitVisibilitySetting()).isEqualTo("PRIVATE");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(createdId).isEqualTo(10L);
    }

    @Test
    void update_withoutId_shouldThrowResultException() {
        UpdateUserCommand command = UpdateUserCommand.builder()
                .name("변경")
                .build();

        assertThatThrownBy(() -> userRepository.update(command))
                .isInstanceOf(ResultException.class);
    }

    @Test
    void findByLoginId_shouldReturnMappedResult() {
        Date now = new Date();
        UserEntity entity = UserEntity.builder()
                .id(1L)
                .loginId("tester")
                .passwordHash("hash")
                .name("홍길동")
                .email("tester@example.com")
                .phone("01012345678")
                .address("Seoul")
                .birthday(now)
                .gender("M")
                .profileImageUrl("https://example.com/profile.png")
                .status("ACTIVE")
                .visitVisibilitySetting("PUBLIC")
                .createdAt(now)
                .updatedAt(now)
                .deletedAt(null)
                .lastLoginAt(now)
                .build();
        when(userMapper.selectByLoginId(eq("tester"))).thenReturn(entity);

        FindUserResult result = userRepository.findByLoginId("tester");

        assertThat(result).isNotNull();
        assertThat(result.getLoginId()).isEqualTo(entity.getLoginId());
        assertThat(result.getName()).isEqualTo(entity.getName());
        assertThat(result.getVisit_visibility()).isEqualTo(entity.getVisitVisibilitySetting());
        assertThat(result.getLastLogin()).isEqualTo(entity.getLastLoginAt());
    }

    @Test
    void delete_shouldReturnMapperOutcome() {
        when(userMapper.softDelete(5L)).thenReturn(1);

        boolean deleted = userRepository.delete(5L);

        verify(userMapper).softDelete(5L);
        assertThat(deleted).isTrue();
    }
}
