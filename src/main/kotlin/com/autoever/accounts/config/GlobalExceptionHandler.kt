package com.autoever.accounts.config

import com.autoever.accounts.common.exception.DuplicatedUserException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    data class ErrorBody(val code: String, val message: String)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorBody> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorBody("INVALID_ARGUMENT", ex.bindingResult.fieldErrors.firstOrNull()?.defaultMessage ?: "invalid"))

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArg(ex: IllegalArgumentException): ResponseEntity<ErrorBody> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorBody("INVALID_ARGUMENT", ex.message ?: "invalid"))

    @ExceptionHandler(DuplicatedUserException::class)
    fun handleDuplicate(ex: DuplicatedUserException): ResponseEntity<ErrorBody> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorBody("DUPLICATE", ex.message ?: "duplicate"))

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleConstraint(ex: DataIntegrityViolationException): ResponseEntity<ErrorBody> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorBody("CONSTRAINT_VIOLATION", "unique constraint violation"))
}
