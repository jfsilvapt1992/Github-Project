package com.github.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class RepositoryDto(
    val id: String,
    val name: String,
    val owner: UserDto,
    val fork: Boolean,
    var branches: List<BranchDto>?
)