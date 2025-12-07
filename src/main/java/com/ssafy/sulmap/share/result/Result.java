package com.ssafy.sulmap.share.result;

import com.ssafy.sulmap.share.result.error.ResultError;
import com.ssafy.sulmap.share.result.error.exception.ResultException;
import com.ssafy.sulmap.share.result.error.impl.SimpleError;
import lombok.Getter;
import lombok.ToString;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@ToString
@Getter
public final class Result<T> {

    private final T value;
    private final boolean success;
    private final List<ResultError> errors;

    private Result(T value, boolean success, List<ResultError> errors) {
        this.value = value;
        this.success = success;
        this.errors = Collections.unmodifiableList(errors);
    }

    /* ---------- 정적 팩토리 메서드 ---------- */

    public static <T> Result<T> ok(T value) {
        return new Result<>(value, true, List.of());
    }

    /** 값이 필요 없는 경우: C#의 Result.Ok()에 해당 (Result<Void>로 표현) */
    public static Result<Void> ok() {
        return new Result<>(null, true, List.of());
    }

    public static <T> Result<T> fail(ResultError error) {
        Objects.requireNonNull(error, "error must not be null");
        return new Result<>(null, false, List.of(error));
    }

    public static <T> Result<T> fail(List<ResultError> errors) {
        Objects.requireNonNull(errors, "errors must not be null");
        if (errors.isEmpty()) {
            throw new IllegalArgumentException("errors must not be empty");
        }
        return new Result<>(null, false, List.copyOf(errors));
    }

    public static <T> Result<T> fail(int code, String message) {
        return fail(new SimpleError(code, message));
    }

    /* ----- 상태 조회 ----- */


    public boolean isFailure() {
        return !success;
    }

    public Optional<T> getValue() {
        return Optional.ofNullable(value);
    }

    public T getOrThrow() {
        if (isFailure()) {
            throw new ResultException(errors);
        }
        return value;
    }

    public ResultError getSingleErrorOrThrow() {
        if (errors.isEmpty()) {
            throw new NoSuchElementException("no result");
        }
        return errors.get(0);
    }

    public T getOrThrow(RuntimeException exception) {
        if (isFailure()) {
            throw exception;
        }
        return value;
    }

    public T getOrElse(T other) {
        return isSuccess() ? value : other;
    }

    public T getOrElseGet(Function<List<ResultError>, T> fallbackSupplier) {
        Objects.requireNonNull(fallbackSupplier, "fallbackSupplier must not be null");
        return isSuccess() ? value : fallbackSupplier.apply(errors);
    }

    /* ---------- Fluent 조합 연산 ---------- */

    public <U> Result<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "mapper must not be null");
        if (isFailure()) {
            // 실패 상태 유지 + 타입만 U로
            return Result.fail(errors);
        }
        return Result.ok(mapper.apply(value));
    }

    public <U> Result<U> flatMap(Function<? super T, Result<U>> binder) {
        Objects.requireNonNull(binder, "binder must not be null");
        if (isFailure()) {
            return Result.fail(errors);
        }
        return binder.apply(value);
    }

    /** 성공 시 부수효과 실행 (logging 등). this 반환해서 체이닝 가능 */
    public Result<T> onSuccess(Consumer<? super T> action) {
        Objects.requireNonNull(action, "action must not be null");
        if (isSuccess()) {
            action.accept(value);
        }
        return this;
    }

    /** 실패 시 부수효과 실행 */
    public Result<T> onFailure(Consumer<List<ResultError>> action) {
        Objects.requireNonNull(action, "action must not be null");
        if (isFailure()) {
            action.accept(errors);
        }
        return this;
    }

    /* ---------- 에러를 fluent하게 추가 ---------- */

    /** 새 Error를 추가한 새로운 실패 Result를 돌려줌 (immutable) */
    public Result<T> withError(ResultError error) {
        Objects.requireNonNull(error, "error must not be null");
        List<ResultError> newErrors = new ArrayList<>(this.errors);
        newErrors.add(error);
        return new Result<>(value, false, newErrors);
    }

    public Result<T> withErrors(List<ResultError> additionalErrors) {
        Objects.requireNonNull(additionalErrors, "additionalErrors must not be null");
        if (additionalErrors.isEmpty()) {
            return this;
        }
        List<ResultError> newErrors = new ArrayList<>(this.errors);
        newErrors.addAll(additionalErrors);
        return new Result<>(value, false, newErrors);
    }

    /** 실패인 경우 예외를 던지고, 성공이면 그대로 this 반환 */
    public Result<T> throwIfFailure() {
        if (isFailure()) {
            throw new ResultException(errors);
        }
        return this;
    }

}

