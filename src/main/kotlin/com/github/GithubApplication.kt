package com.github

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(
	info= Info(
		title = "Github Challenge",
		version = "1.0",
		description = "Github Challenge Api Documention"
	)
)
class GithubApplication

fun main(args: Array<String>) {
	runApplication<GithubApplication>(*args)
}
