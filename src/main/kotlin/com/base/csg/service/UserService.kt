package com.base.csg.service

import com.base.csg.model.CreateUserDTO
import com.base.csg.model.UserRecord
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
        private val jdbcTemplate: JdbcTemplate,
        private val passwordEncoder: PasswordEncoder
) {

    fun usernameExists(username: String): Boolean {
        val sql = "SELECT COUNT(*) FROM Users WHERE username = ?"
        val count = jdbcTemplate.queryForObject(sql, Int::class.java, username)
        return count != null && count > 0
    }

    fun insertUser(dto: CreateUserDTO): Int {
        val sql =
                """
                INSERT INTO Users (username, password_hash, role, is_active, is_delete, created_at)
                VALUES (?, ?, ?, 1, 0, GETDATE())
                """.trimIndent()
        return jdbcTemplate.update(sql, dto.username, dto.passwordHash, dto.role)
    }

    fun createUser(dto: CreateUserDTO): ResponseEntity<Any> {
        if (usernameExists(dto.username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(mapOf("message" to "Username already exists"))
        }

        val hashedPassword = passwordEncoder.encode(dto.passwordHash)
        val userToSave = dto.copy(passwordHash = hashedPassword)
        val rows = insertUser(userToSave)
        return if (rows > 0) {
            ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            mapOf(
                                    "message" to "User created successfully",
                                    "user" to mapOf("username" to dto.username, "role" to dto.role)
                            )
                    )
        } else {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("message" to "Failed to create user"))
        }
    }

    fun findByUsername(username: String): UserRecord? {
        val sql =
                """
                SELECT username, password_hash, role, is_active, is_delete
                FROM Users
                WHERE username = ?
                """.trimIndent()

        return jdbcTemplate
                .query(sql, { ps -> ps.setString(1, username) }) { rs, _ ->
                    UserRecord(
                            username = rs.getString("username"),
                            passwordHash = rs.getString("password_hash"),
                            role = rs.getString("role"),
                            isActive = rs.getBoolean("is_active"),
                            isDelete = rs.getBoolean("is_delete")
                    )
                }
                .firstOrNull()
    }
    
}
