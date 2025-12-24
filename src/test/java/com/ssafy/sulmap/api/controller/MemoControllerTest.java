package com.ssafy.sulmap.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.sulmap.api.dto.request.UpsertMemoRequest;
import com.ssafy.sulmap.api.security.model.UserDetail;
import com.ssafy.sulmap.core.model.MemoModel;
import com.ssafy.sulmap.core.model.UserModel;
import com.ssafy.sulmap.core.service.MemoService;
import com.ssafy.sulmap.share.result.Result;
import com.ssafy.sulmap.share.result.error.impl.SimpleError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemoController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("MemoController 통합 테스트")
class MemoControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private MemoService memoService;

        private UserDetail userDetail;
        private MemoModel mockMemo;

        @BeforeEach
        void setUp() {
                UserModel userModel = UserModel.builder()
                                .id(1L)
                                .loginId("testuser")
                                .name("테스트유저")
                                .build();

                userDetail = new UserDetail(userModel);

                mockMemo = MemoModel.builder()
                                .id(1L)
                                .userId(1L)
                                .barId(1L)
                                .content("테스트 메모")
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
        }

        @Test
        @WithMockUser
        @DisplayName("메모 생성/수정 성공")
        void upsertMemo_Success() throws Exception {
                // given
                UpsertMemoRequest request = new UpsertMemoRequest("테스트 메모");
                when(memoService.upsertMemo(any())).thenReturn(Result.ok(mockMemo));

                // when & then
                mockMvc.perform(put("/api/bars/1/memo")
                                .with(user(userDetail))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.content").value("테스트 메모"));
        }

        @Test
        @WithMockUser
        @DisplayName("메모 내용이 비어있으면 400 Bad Request")
        void upsertMemo_EmptyContent_BadRequest() throws Exception {
                // given
                UpsertMemoRequest request = new UpsertMemoRequest("");

                // when & then
                mockMvc.perform(put("/api/bars/1/memo")
                                .with(user(userDetail))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("메모 조회 성공")
        void getMemo_Success() throws Exception {
                // given
                when(memoService.getMemo(eq(1L), eq(1L))).thenReturn(Result.ok(mockMemo));

                // when & then
                mockMvc.perform(get("/api/bars/1/memo")
                                .with(user(userDetail)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.content").value("테스트 메모"));
        }

        @Test
        @WithMockUser
        @DisplayName("메모가 없으면 404 Not Found")
        void getMemo_NotFound() throws Exception {
                // given
                when(memoService.getMemo(eq(1L), eq(1L)))
                                .thenReturn(Result
                                                .fail(new SimpleError(HttpStatus.NOT_FOUND.value(), "메모를 찾을 수 없습니다.")));

                // when & then
                mockMvc.perform(get("/api/bars/1/memo")
                                .with(user(userDetail)))
                                .andExpect(status().isNotFound());
        }
}
