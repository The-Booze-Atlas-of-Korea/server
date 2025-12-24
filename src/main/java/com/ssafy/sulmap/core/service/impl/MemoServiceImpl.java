package com.ssafy.sulmap.core.service.impl;

import com.ssafy.sulmap.core.model.MemoModel;
import com.ssafy.sulmap.core.model.command.UpsertMemoCommand;
import com.ssafy.sulmap.core.repository.MemoRepository;
import com.ssafy.sulmap.core.service.MemoService;
import com.ssafy.sulmap.share.result.error.impl.SimpleError;
import com.ssafy.sulmap.share.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 메모 서비스 구현
 */
@Service
@RequiredArgsConstructor
public class MemoServiceImpl implements MemoService {

    private final MemoRepository _memoRepository;

    @Override
    public Result<MemoModel> upsertMemo(UpsertMemoCommand command) {
        // Validation
        if (command.content() == null || command.content().isBlank()) {
            return Result.fail(new SimpleError(HttpStatus.BAD_REQUEST.value(), "메모 내용은 필수입니다."));
        }

        MemoModel memo = MemoModel.builder()
                .userId(command.userId())
                .barId(command.barId())
                .content(command.content())
                .build();

        MemoModel saved = _memoRepository.upsert(memo);
        return Result.ok(saved);
    }

    @Override
    public Result<MemoModel> getMemo(Long userId, Long barId) {
        Optional<MemoModel> memo = _memoRepository.findByUserIdAndBarId(userId, barId);

        if (memo.isEmpty()) {
            return Result.fail(new SimpleError(HttpStatus.NOT_FOUND.value(), "메모를 찾을 수 없습니다."));
        }

        return Result.ok(memo.get());
    }
}
