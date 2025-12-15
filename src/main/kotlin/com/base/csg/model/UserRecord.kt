package com.base.csg.model

data class UserRecord(
        val username: String,
        val passwordHash: String,
        val role: String,
        val isActive: Boolean,
        val isDelete: Boolean
)
