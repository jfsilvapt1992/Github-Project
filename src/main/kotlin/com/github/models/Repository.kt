package com.github.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Repository(
    val id: String,
    val name: String,
    val owner: User,
    val fork: Boolean,
    var branches: List<Branch>?
)