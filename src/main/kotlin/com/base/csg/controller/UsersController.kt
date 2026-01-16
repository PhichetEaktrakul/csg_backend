package com.base.csg.controller

import com.base.csg.model.CreateUserDTO
import com.base.csg.model.LoginRequest
import com.base.csg.service.AuthService
import com.base.csg.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UsersController(
        private val authService: AuthService,
        private val userService: UserService,
        private val jdbcTemplate: JdbcTemplate
) {

    // ================= User Login =======================================
    @PostMapping("/login") fun loginUser(@RequestBody req: LoginRequest) = authService.login(req)

    // ================= User Register ====================================
    @PostMapping("/register") fun createUser(@RequestBody dto: CreateUserDTO) = userService.createUser(dto)

    // ================= Get all User except role manager =================
    @GetMapping("/all")
    fun getUsersAll(): ResponseEntity<Any> {
        val sql =
                """
                SELECT * FROM Users
                WHERE is_delete = 0
                AND role <> 'manager'
                ORDER BY created_at DESC
                """
                .trimIndent()

        return try {
            val result = jdbcTemplate.queryForList(sql)
            return ResponseEntity.ok(result)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("error" to e.message))
        }
    }

    // ================= Delete User by id ================================
    @DeleteMapping("/{id}")
    fun deleteUserByID(@PathVariable id: Int): ResponseEntity<Any> {
        val sql = "UPDATE Users SET is_active = 0, is_delete = 1 WHERE user_id = ?"

        val rows = jdbcTemplate.update(sql, id)
        return if (rows > 0) {
            ResponseEntity.ok(mapOf("message" to "User deleted successfully"))
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
}
