package com.github.configurations

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class ExternalRequestsConfigurations {

    @Bean
    fun githubClient(): WebClient = WebClient.builder()
        .baseUrl("https://api.github.com")
        .defaultHeader("Accept", "application/json")
        .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
        .build()
}
