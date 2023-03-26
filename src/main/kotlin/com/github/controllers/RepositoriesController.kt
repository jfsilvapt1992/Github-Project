package com.github.controllers

import com.github.interactors.GetUserRepositoriesInteractor
import com.github.models.ErrorMessage
import com.github.models.Repository
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
    suspend fun getUserRepositories(@RequestHeader headers: Map<String, String>, @PathVariable userName: String): ResponseEntity<List<Repository>> {
        return ResponseEntity
            .ok()
            .body(getUserRepositoriesInteractor.getRepositories(userName, headers))
    }
}