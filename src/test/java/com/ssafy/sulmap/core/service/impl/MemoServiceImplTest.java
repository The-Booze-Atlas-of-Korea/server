package com.ssafy.sulmap.core.service.impl;

import com.ssafy.sulmap.core.model.MemoModel;
import com.ssafy.sulmap.core.model.command.UpsertMemoCommand;
import com.ssafy.sulmap.core.repository.MemoRepository;
import com.ssafy.sulmap.share.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemoServiceImpl 단위 테스트")
class MemoServiceImplTest {

    @Mock
    private MemoRepository memoRepository;

    @InjectMocks
    private MemoServiceImpl memoService;

    private UpsertMemoCommand validCommand;
    private MemoModel mockMemo;

    @BeforeEach
    void setUp() {
        validCommand = UpsertMemoCommand.builder()
                .userId(1L)
                .barId(1L)
                .content("테스트 메모입니다")
                .build();

        mockMemo = MemoModel.builder()
                .id(1L)
                .userId(1L)
                .barId(1L)
                .content("테스트 메모입니다")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("메모 생성 성공")
    void upsertMemo_Success() {
        // given
        when(memoRepository.upsert(any(MemoModel.class))).thenReturn(mockMemo);

        // when
        Result<MemoModel> result = memoService.upsertMemo(validCommand);

        // then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getOrThrow()).isNotNull();
        assertThat(result.getOrThrow().getContent()).isEqualTo("테스트 메모입니다");
        verify(memoRepository, times(1)).upsert(any(MemoModel.class));
    }

    @Test
    @DisplayName("메모 내용이 비어있으면 실패")
    void upsertMemo_EmptyContent_Fail() {
        // given
        UpsertMemoCommand emptyCommand = UpsertMemoCommand.builder()
                .userId(1L)
                .barId(1L)
                .content("")
                .build();

        // when
        Result<MemoModel> result = memoService.upsertMemo(emptyCommand);

        // then
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getSingleErrorOrThrow().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        verify(memoRepository, never()).upsert(any());
    }

    @Test
    @DisplayName("메모 내용이 null이면 실패")
    void upsertMemo_NullContent_Fail() {
        // given
        UpsertMemoCommand nullCommand = UpsertMemoCommand.builder()
                .userId(1L)
                .barId(1L)
                .content(null)
                .build();

        // when
        Result<MemoModel> result = memoService.upsertMemo(nullCommand);

        // then
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getSingleErrorOrThrow().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        verify(memoRepository, never()).upsert(any());
    }

    @Test
    @DisplayName("메모 조회 성공")
    void getMemo_Success() {
        // given
        when(memoRepository.findByUserIdAndBarId(1L, 1L)).thenReturn(Optional.of(mockMemo));

        // when
        Result<MemoModel> result = memoService.getMemo(1L, 1L);

        // then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getOrThrow()).isNotNull();
        assertThat(result.getOrThrow().getId()).isEqualTo(1L);
        verify(memoRepository, times(1)).findByUserIdAndBarId(1L, 1L);
    }

    @Test
    @DisplayName("메모가 존재하지 않으면 실패")
    void getMemo_NotFound_Fail() {
        // given
        when(memoRepository.findByUserIdAndBarId(1L, 1L)).thenReturn(Optional.empty());

        // when
        Result<MemoModel> result = memoService.getMemo(1L, 1L);

        // then
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getSingleErrorOrThrow().getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        verify(memoRepository, times(1)).findByUserIdAndBarId(1L, 1L);
    }
}
