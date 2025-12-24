package com.ssafy.sulmap.core.repository;

import com.ssafy.sulmap.core.model.MemoModel;

import java.util.Optional;

/**
 * 메모 저장소 인터페이스
 */
public interface MemoRepository {
    /**
     * 메모 저장 (생성 또는 수정)
     */
    MemoModel upsert(MemoModel memo);

    /**
     * 사용자와 술집으로 메모 조회
     */
    Optional<MemoModel> findByUserIdAndBarId(Long userId, Long barId);
}
