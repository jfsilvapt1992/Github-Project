package com.github.exceptions

import com.github.models.ErrorMessage
import com.google.gson.Gson
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.server.NotAcceptableStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@ControllerAdvice
class CustomRestExceptionHandler: ResponseEntityExceptionHandler() {

    override fun handleNotAcceptableStatusException(ex: NotAcceptableStatusException, headers: HttpHeaders, status: HttpStatusCode, exchange: ServerWebExchange): Mono<ResponseEntity<Any?>?> {
        return handleExceptionInternal(ex!!, getErrorMessage(status, headers) , headers, status!!, exchange!!)
    }

    private fun getErrorMessage(status: HttpStatusCode, headers:HttpHeaders) = Gson().toJson(ErrorMessage(status.toString(), "Unsupported media type, must be ${headers.accept}"))
}