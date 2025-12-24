package com.ssafy.sulmap.core.service;

import com.ssafy.sulmap.core.model.MemoModel;
import com.ssafy.sulmap.core.model.command.UpsertMemoCommand;
import com.ssafy.sulmap.share.result.Result;

/**
 * 메모 서비스 인터페이스
 */
public interface MemoService {
    /**
     * 메모 생성 또는 수정 (Upsert)
     */
    Result<MemoModel> upsertMemo(UpsertMemoCommand command);

    /**
     * 메모 조회
     */
    Result<MemoModel> getMemo(Long userId, Long barId);
}
