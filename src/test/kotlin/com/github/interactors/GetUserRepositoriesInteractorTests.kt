package com.github.interactors

import com.github.controllers.RepositoriesControllerTests
import com.github.gateways.GithubGateway
import com.github.models.Branch
import com.github.models.Commit
import com.github.models.Repository
import com.github.models.User
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean


@SpringBootTest
class GetUserRepositoriesInteractorTests {

	@MockBean
	private lateinit var gateway: GithubGateway

	@Autowired
	private lateinit var interactor: GetUserRepositoriesInteractor

	@Test
	fun `get repositories information when github api returns all repos and this filter the forks`(): Unit = runBlocking {
		Mockito.`when`(gateway.getUserRepositories(user, token))
			.thenReturn(listOf(notForkRepository, forkedRepository))

		Mockito.`when`(gateway.getRepositoryBranches(user, forkedRepository.name, token))
			.thenReturn(listOf(stagingBranch))

		Mockito.`when`(gateway.getRepositoryBranches(user, notForkRepository.name, token))
			.thenReturn(listOf(stagingBranch, masterBranch))

		val repositories = interactor.getRepositories(user, headers)

		assertEquals(1, repositories.size)
		assertEquals(false, repositories.first().fork)
		assertEquals(2, repositories.first().branches?.size)
	}

	@Test
	fun `get repositories information when github api returns only forks`(): Unit = runBlocking {
		Mockito.`when`(gateway.getUserRepositories(user, token))
			.thenReturn(listOf(forkedRepository))

		Mockito.`when`(gateway.getRepositoryBranches(user, forkedRepository.name, token))
			.thenReturn(listOf(masterBranch, stagingBranch))

		val repositories = interactor.getRepositories(user, headers)

		assertEquals(0, repositories.size)
	}

	@Test
	fun `get repositories information when github api returns only not forks`(): Unit = runBlocking {
		Mockito.`when`(gateway.getUserRepositories(user, token))
			.thenReturn(listOf(notForkRepository, notForkRepository2))

		Mockito.`when`(gateway.getRepositoryBranches(user, notForkRepository.name, token))
			.thenReturn(listOf(masterBranch, stagingBranch))

		Mockito.`when`(gateway.getRepositoryBranches(user, notForkRepository2.name, token))
			.thenReturn(listOf(masterBranch, stagingBranch))

		val repositories = interactor.getRepositories(user, headers)

		assertEquals(2, repositories.size)
		assertEquals(2, repositories.first().branches?.size)
		assertEquals(2, repositories.last().branches?.size)
	}

	@Test
	fun `get repositories information when github api returns empty result`(): Unit = runBlocking {
		Mockito.`when`(gateway.getUserRepositories(user, token))
			.thenReturn(emptyList())

		val repositories = interactor.getRepositories(user, headers)

		assertEquals(0, repositories.size)
	}


	companion object {
		const val user = "jfsilva"
		const val token = "ghp_xjSljoa36sZM9Ab666sTq4T04itJJV1P9999"
		private val login =  User("534953122", user)
		val headers = mapOf("github-token" to token)

		val stagingBranch = Branch("main", Commit("4795c14a0b89636aaa6d7addc4dfd78f112c4a56","https:github.com/commit01"))
		val masterBranch = Branch("master", Commit("4795c14a0b89636aaa6d7addc4dfd78f112c4a56","https:github.com/commit01"))

		val notForkRepository = Repository("330391333", "Performance Tests", login, false, emptyList())
		val notForkRepository2 = Repository("330391333", "Integration Tests", login, false, emptyList())
		val forkedRepository = Repository("330391333", "Github Apis", login, true, emptyList())

	}
}
