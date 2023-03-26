package com.github.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class Branch (
    val name: String,
    val commit: Commit
)

