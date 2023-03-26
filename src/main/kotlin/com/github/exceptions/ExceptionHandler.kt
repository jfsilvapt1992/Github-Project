package com.github.exceptions

import com.github.models.ErrorMessage
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler () {

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(
        exception: ResponseStatusException
    ): ResponseEntity<ErrorMessage> {
        val errorMessage = ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.toString(), exception.message)
        return ResponseEntity.status(exception.statusCode).body(errorMessage)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(
        exception: Exception
    ): ResponseEntity<ErrorMessage> {
        val errorMessage = ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.toString(), exception.message)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage)
    }
}