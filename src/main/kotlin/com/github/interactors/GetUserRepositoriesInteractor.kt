package com.github.interactors

import com.github.gateways.GithubGateway
import com.github.models.Repository
import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import java.util.stream.Collectors

@Component
class GetUserRepositoriesInteractor(
    private val gateway: GithubGateway
) {
    val logger: Logger = LoggerFactory.getLogger(GetUserRepositoriesInteractor::class.java)

    suspend fun getRepositories(userName: String, headers: Map<String, String>): List<Repository> {
        logger.info("INIT [getRepositories] interactor user $userName")

        val token = headers.getToken()

        val repositories = gateway.getUserRepositories(userName, token)
            .filter {
                it.isNotFork()
            }

        repositories.fetchBranches(token)

        logger.info("END [getRepositories] interactor user $userName")
        return repositories
    }
    private fun Map<String,String>.getToken(): String {
        val token = get("github-token")
        if(token.isNullOrBlank()) throw ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid token")
        return token
    }
    private fun Repository.isNotFork() = !fork

    private suspend fun List<Repository>.fetchBranches(token: String) = withContext(Dispatchers.IO) {
        map{ repository ->
            async {
                repository.branches = gateway.getRepositoryBranches(repository.owner.login, repository.name, token)
                repository
            }
        }.awaitAll()
    }
}



