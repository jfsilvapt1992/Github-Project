package com.github.controllers

import com.github.interactors.GetUserRepositoriesInteractor
import com.github.models.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
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
		Mockito.`when`(interactor.getRepositories(user, headers))
			.thenReturn(listOf(notForkRepository, notForkRepository2))

		val response = controller.getUserRepositories(headers, user)
		val body =  response.body as List<Repository>

		assertEquals(HttpStatus.OK.toString(), response.statusCode.toString())
		assertTrue(body.size == 2)
	}

	@Test
	fun `get user repositories with success without results`(): Unit = runBlocking {
		Mockito.`when`(interactor.getRepositories(user, headers))
			.thenReturn(emptyList())

		val response = controller.getUserRepositories(headers, user)
		val body =  response.body as List<Repository>

		assertEquals(HttpStatus.OK.toString(), response.statusCode.toString())
		assertTrue(body.isEmpty())
	}

	@Test
	fun `get user repositories without github-token`(): Unit = runBlocking {
		Mockito.`when`(interactor.getRepositories(user, emptyMap()))
			.thenThrow(ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid token"))

		val exception: ResponseStatusException = Assertions.assertThrows(ResponseStatusException::class.java) {
			runBlocking {
				controller.getUserRepositories(emptyMap(), user)
			}
		}

		assertEquals( "Invalid token", exception.reason)
		assertEquals( HttpStatus.FORBIDDEN, exception.statusCode)
	}

	@Test
	fun `get user repositories with invalid github username`(): Unit = runBlocking {
		Mockito.`when`(interactor.getRepositories(user, headers))
			.thenThrow(ResponseStatusException(HttpStatus.NOT_FOUND, "User or repository not found"))

		val exception: ResponseStatusException = Assertions.assertThrows(ResponseStatusException::class.java) {
			runBlocking {
				controller.getUserRepositories(headers, user)
			}
		}

		assertEquals(HttpStatus.NOT_FOUND.toString(), exception.statusCode.toString())
		assertEquals("User or repository not found", exception.reason)
	}

	companion object {
		const val user = "jfsilva"
		const val token = "ghp_xjSljoa36sZM9Ab666sTq4T04itJJV1P9999"
		private val login =  User("534953122", user)
		private val stagingBranch = Branch("main", Commit("4795c14a0b89636aaa6d7addc4dfd78f112c4a56","https:github.com/commit01"))
		private val masterBranch = Branch("master", Commit("4795c14a0b89636aaa6d7addc4dfd78f112c4a56","https:github.com/commit01"))
		val notForkRepository = Repository("330391333", "Performance Tests", login, false, listOf(masterBranch))
		val notForkRepository2 = Repository("330391333", "Integration Tests", login, false, listOf(stagingBranch, masterBranch))
		val headers = mapOf("github-token" to token)
	}
}
