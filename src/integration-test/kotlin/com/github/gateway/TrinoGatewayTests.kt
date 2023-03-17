package com.github.gateway

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.gateways.GithubGateway
import com.github.models.BranchDto
import com.github.models.CommitDto
import com.github.models.RepositoryDto
import com.github.models.UserDto
import kotlinx.coroutines.runBlocking
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ResponseStatusException


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TrinoGatewayTests {

	@TestConfiguration
	internal class Config {
		@Bean
		fun webServer(): MockWebServer {
			return MockWebServer()
		}

		@Bean
		fun webClient(webServer: MockWebServer): WebClient {
			return WebClient.builder().baseUrl(webServer.url("").toString()).build()
		}

		@Bean
		fun gateway(webClient: WebClient): GithubGateway {
			return GithubGateway(webClient)
		}
	}

	@Autowired
	private lateinit var server: MockWebServer

	@Autowired
	private lateinit var gateway: GithubGateway

	private val mapper = ObjectMapper()

	@Test
	fun `GET repositories branches with response 200`(): Unit = runBlocking {
		val branches = listOf(stagingBranch, masterBranch)

		server.enqueue(
			MockResponse()
				.setResponseCode(200)
				.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.setBody(mapper.writeValueAsString(branches))
		)

		val repositories = gateway.getRepositoryBranches(user, forkedRepository.name, token)


		assertEquals(2, repositories.size)
	}

	@Test
	fun `GET user repos with response 200`(): Unit = runBlocking {
		val repos = listOf(notForkRepository, notForkRepository2, forkedRepository)

		server.enqueue(
			MockResponse()
				.setResponseCode(200)
				.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.setBody(mapper.writeValueAsString(repos))
		)

		val repositories = gateway.getUserRepositories(user, token)

		assertEquals(3, repositories.size)
	}

	@Test
	fun `GET repositories branches  with response 401`() {
		server.enqueue(
			MockResponse()
				.setResponseCode(401)
				.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.setBody(mapper.writeValueAsString("Not authenticated"))
		)

		val exception: ResponseStatusException = assertThrows(ResponseStatusException::class.java) {
			runBlocking {
				gateway.getRepositoryBranches(user, "dummy", token)
			}
		}

		assertTrue(exception.reason == "Unauthorized, invalid token (verify permissions)")
		assertTrue(exception.statusCode == HttpStatus.UNAUTHORIZED)
	}

	@Test
	fun `GET user repos with response 401`() {
		server.enqueue(
			MockResponse()
				.setResponseCode(401)
				.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.setBody(mapper.writeValueAsString("Not authenticated"))
		)

		val exception: ResponseStatusException = assertThrows(ResponseStatusException::class.java) {
			runBlocking {
				gateway.getUserRepositories(user, token)
			}
		}

		assertTrue(exception.reason == "Unauthorized, invalid token (verify permissions)")
		assertTrue(exception.statusCode == HttpStatus.UNAUTHORIZED)
	}

	@Test
	fun `GET repositories branches  with response 404`() {
		server.enqueue(
			MockResponse()
				.setResponseCode(404)
				.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.setBody(mapper.writeValueAsString("NOT FOUND"))
		)

		val exception: ResponseStatusException = assertThrows(ResponseStatusException::class.java) {
			runBlocking {
				gateway.getRepositoryBranches(user, "dummy", token)
			}
		}

		assertTrue(exception.reason == "User or repository not found")
		assertTrue(exception.statusCode == HttpStatus.NOT_FOUND)
	}

	companion object {
		val user = "jfsilva"
		val token = "ghp_xjSljoa36sZM9Ab666sTq4T04itJJV1P9999"
		val login =  UserDto("534953122", user)
		val stagingBranch = BranchDto("main", CommitDto("4795c14a0b89636aaa6d7addc4dfd78f112c4a56","https:github.com/commit01"))
		val masterBranch = BranchDto("master", CommitDto("4795c14a0b89636aaa6d7addc4dfd78f112c4a56","https:github.com/commit01"))

		val notForkRepository = RepositoryDto("330391333", "Performance Tests", login, false, emptyList())
		val notForkRepository2 = RepositoryDto("330391333", "Integration Tests", login, false, emptyList())
		val forkedRepository = RepositoryDto("330391333", "Github Apis", login, true, emptyList())
	}
}
