package com.github.controllers

import com.github.interactors.GetUserRepositoriesInteractor
import com.github.models.ErrorMessage
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException


@RestController
class RepositoriesController (
    private val getUserRepositoriesInteractor: GetUserRepositoriesInteractor
){

    @GetMapping(
        value = ["/users/{userName}/repositories"],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun getUserRepositories(@RequestHeader headers: Map<String, String>, @PathVariable userName: String): ResponseEntity<Any> {
        return try {
            ResponseEntity
                .ok()
                .body(getUserRepositoriesInteractor.getRepositories(userName, headers.getToken()))
        }catch(e: ResponseStatusException){
            ResponseEntity
                .status(e.statusCode)
                .body(ErrorMessage(e.statusCode.toString(), e.reason!!))
        }catch(ex: Exception){
            val statusCode = HttpStatus.INTERNAL_SERVER_ERROR
            ResponseEntity
                .status(statusCode)
                .body(ErrorMessage(statusCode.toString(), ex.message!!))
        }
    }

    private fun Map<String,String>.getToken(): String {
        val token = get("github-token")
        if(token.isNullOrBlank()) throw ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid token")
        return token
    }
}