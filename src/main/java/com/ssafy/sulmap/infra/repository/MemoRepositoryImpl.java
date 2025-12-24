package com.ssafy.sulmap.infra.repository;

import com.ssafy.sulmap.core.model.MemoModel;
import com.ssafy.sulmap.core.repository.MemoRepository;
import com.ssafy.sulmap.infra.mapper.MemoMapper;
import com.ssafy.sulmap.infra.model.MemoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 메모 저장소 구현
 */
@Repository
@RequiredArgsConstructor
public class MemoRepositoryImpl implements MemoRepository {

    private final MemoMapper _memoMapper;

    @Override
    @Transactional
    public MemoModel upsert(MemoModel memo) {
        // 타임스탬프 설정
        if (memo.getCreatedAt() == null) {
            memo.setCreatedAt(LocalDateTime.now());
        }
        memo.setUpdatedAt(LocalDateTime.now());

        // 엔티티 변환 및 Upsert
        MemoEntity entity = MemoEntity.fromModel(memo);
        _memoMapper.upsert(entity);

        // 자동 생성된 ID를 모델에 설정
        memo.setId(entity.getId());

        return memo;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MemoModel> findByUserIdAndBarId(Long userId, Long barId) {
        MemoEntity entity = _memoMapper.selectByUserIdAndBarId(userId, barId);
        if (entity == null) {
            return Optional.empty();
        }

        return Optional.of(entity.toModel());
    }
}
