package com.likelion.friendpass.global.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.NonUniqueResultException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400: 잘못된 입력 (IllegalArgument, Bean Validation)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException e, HttpServletRequest req) {
        return to(HttpStatus.BAD_REQUEST, e.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException e, HttpServletRequest req) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> formatFieldError(fe))
                .collect(Collectors.joining("; "));
        if (msg.isBlank()) msg = "요청 값이 올바르지 않습니다.";
        return to(HttpStatus.BAD_REQUEST, msg, req);
    }

    // 404: 리소스 없음
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(EntityNotFoundException e, HttpServletRequest req) {
        return to(HttpStatus.NOT_FOUND, e.getMessage(), req);
    }

    // 409: 상태 충돌 (중복/비즈니스 제약)
    @ExceptionHandler({
            IllegalStateException.class,              // “이미 가입된 이메일입니다” 등
            DataIntegrityViolationException.class,    // 유니크 제약 위반 등
            NonUniqueResultException.class            // 이메일 인증 중복 등
    })
    public ResponseEntity<ApiError> handleConflict(Exception e, HttpServletRequest req) {
        return to(HttpStatus.CONFLICT, messageOrDefault(e, "요청을 처리할 수 없습니다."), req);
    }

    // 403: 권한 없음
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException e, HttpServletRequest req) {
        return to(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.", req);
    }

    // 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleEtc(Exception e, HttpServletRequest req) {
        return to(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.", req);
    }

    private ResponseEntity<ApiError> to(HttpStatus status, String message, HttpServletRequest req) {
        ApiError body = ApiError.of(status.value(), status.getReasonPhrase(), message, req.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }

    private String formatFieldError(FieldError fe) {
        String field = fe.getField();
        String msg = fe.getDefaultMessage();
        Object rejected = fe.getRejectedValue();
        return rejected == null ? field + ": " + msg : field + "(" + rejected + "): " + msg;
    }

    private String messageOrDefault(Exception e, String def) {
        return (e.getMessage() == null || e.getMessage().isBlank()) ? def : e.getMessage();
    }
}