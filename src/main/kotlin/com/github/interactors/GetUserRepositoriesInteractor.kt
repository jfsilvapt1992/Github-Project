package com.github.interactors

import com.github.gateways.GithubGateway
import com.github.models.RepositoryDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GetUserRepositoriesInteractor(
    private val gateway: GithubGateway
) {
    val logger: Logger = LoggerFactory.getLogger(GetUserRepositoriesInteractor::class.java)

    suspend fun getRepositories(userName: String, token: String): List<RepositoryDto> {
        logger.info("INIT [getRepositories] interactor user $userName")

        val repositories = gateway.getUserRepositories(userName, token)
          .filter {
              it.isNotFork()
          }
          .map {  repository ->
              repository.branches = gateway.getRepositoryBranches(repository.owner.login, repository.name, token)
              repository
          }

        logger.info("END [getRepositories] interactor user $userName")
        return repositories
    }

    private fun RepositoryDto.isNotFork() = !fork
}

