package com.base.csg.model

data class CreateUserDTO(
    val username: String,
    val passwordHash: String,
    val role: String
)
