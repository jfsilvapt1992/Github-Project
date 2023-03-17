package com.github.controllers

import com.github.interactors.GetUserRepositoriesInteractor
import com.github.models.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException


@SpringBootTest
class RepositoriesControllerTests {

	@MockBean
	private lateinit var interactor: GetUserRepositoriesInteractor

	@Autowired
	private lateinit var controller: RepositoriesController

	@Test
	fun `get user repositories with success`(): Unit = runBlocking {
		Mockito.`when`(interactor.getRepositories(user, token))
			.thenReturn(listOf(notForkRepository, notForkRepository2))

		val response = controller.getUserRepositories(headers, user)
		val body =  response.body as List<RepositoryDto>

		assertEquals(HttpStatus.OK.toString(), response.statusCode.toString())
		assertTrue(body.size == 2)
	}

	@Test
	fun `get user repositories with success without results`(): Unit = runBlocking {
		Mockito.`when`(interactor.getRepositories(user, token))
			.thenReturn(emptyList())

		val response = controller.getUserRepositories(headers, user)
		val body =  response.body as List<RepositoryDto>

		assertEquals(HttpStatus.OK.toString(), response.statusCode.toString())
		assertTrue(body.isEmpty())
	}

	@Test
	fun `get user repositories without github-token`(): Unit = runBlocking {
		val response = controller.getUserRepositories(emptyMap(), user)
		val body =  response.body as ErrorMessage

		assertEquals(HttpStatus.FORBIDDEN.toString(), response.statusCode.toString())
		assertEquals("403 FORBIDDEN", body.status )
		assertEquals("Invalid token", body.message)
	}

	@Test
	fun `get user repositories with invalid github username`(): Unit = runBlocking {
		Mockito.`when`(interactor.getRepositories(user, token))
			.thenThrow(ResponseStatusException(HttpStatus.NOT_FOUND, "User or repository not found"))

		val response = controller.getUserRepositories(headers, user)
		val body =  response.body as ErrorMessage

		assertEquals(HttpStatus.NOT_FOUND.toString(), response.statusCode.toString())
		assertEquals("404 NOT_FOUND",body.status )
		assertEquals("User or repository not found", body.message )
	}

	companion object {
		const val user = "jfsilva"
		const val token = "ghp_xjSljoa36sZM9Ab666sTq4T04itJJV1P9999"
		private val login =  UserDto("534953122", user)
		private val stagingBranch = BranchDto("main", CommitDto("4795c14a0b89636aaa6d7addc4dfd78f112c4a56","https:github.com/commit01"))
		private val masterBranch = BranchDto("master", CommitDto("4795c14a0b89636aaa6d7addc4dfd78f112c4a56","https:github.com/commit01"))
		val notForkRepository = RepositoryDto("330391333", "Performance Tests", login, false, listOf(masterBranch))
		val notForkRepository2 = RepositoryDto("330391333", "Integration Tests", login, false, listOf(stagingBranch, masterBranch))
		val headers = mapOf("github-token" to token)
	}
}
