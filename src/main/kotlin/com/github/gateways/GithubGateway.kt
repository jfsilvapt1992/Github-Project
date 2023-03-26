package com.github.gateways

import com.github.models.Branch
import com.github.models.Repository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.*
import org.springframework.web.server.ResponseStatusException


@Component
class GithubGateway (
    private val githubClient: WebClient
) {
    var logger: Logger = LoggerFactory.getLogger(GithubGateway::class.java)

    suspend fun getUserRepositories(userName: String, token: String): List<Repository> {
        logger.info("Getting user $userName github repositories..")

        return githubClient
            .get()
            .uri("/users/$userName/repos")
            .header("Authorization", "Bearer $token")
            .awaitExchangeOrNull {
                    response -> if(response.statusCode() != HttpStatus.OK){
                        logger.warn("GET /users/$userName/repos - ${response.statusCode()}")
                        response.throwException("GET /users/$userName/repos", response.statusCode().toString(), "User")
                    }
                response.awaitBody()
            } ?: emptyList()
    }

    suspend fun getRepositoryBranches(user:String, repository: String, token: String): List<Branch> {
        logger.info("Getting repository $repository branches list..")
        return githubClient
            .get()
            .uri("repos/$user/$repository/branches")
            .header("Authorization", "Bearer $token")
            .awaitExchangeOrNull {
                    response -> if(response.statusCode() != HttpStatus.OK){
                        logger.warn("GET repos/$user/$repository/branches - ${response.statusCode()}")
                        response.throwException("GET repos/$user/$repository/branches", response.statusCode().toString(), "User or repository")
                    }
                response.awaitBody()
            } ?: emptyList()
    }

    private fun ClientResponse.throwException(endpoint: String, statusCode: String, missingParameter: String?) {
        logger.warn("$endpoint - $statusCode")
        if(statusCode() == HttpStatus.NOT_FOUND){
            throw ResponseStatusException(statusCode(), "$missingParameter not found")
        } else if (statusCode() == HttpStatus.FORBIDDEN) {
            throw ResponseStatusException(statusCode(), "Bad Credentials" )
        } else if (statusCode() == HttpStatus.UNAUTHORIZED) {
            throw ResponseStatusException(statusCode(), "Unauthorized, invalid token (verify permissions)" )
       } else {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error occurred")
        }

    }
}