package com.base.csg.service

import com.base.csg.model.LoginRequest
import com.base.csg.util.JwtUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
        private val userService: UserService,
        private val jwtUtil: JwtUtil,
        private val passwordEncoder: PasswordEncoder
) {

        fun login(req: LoginRequest): ResponseEntity<Any> {
                val user =
                        userService.findByUsername(req.username)
                                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(mapOf("message" to "Invalid username or password"))

                if (!user.isActive || user.isDelete) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mapOf("message" to "User is inactive or deleted"))
                }

                if (!passwordEncoder.matches(req.password, user.passwordHash)) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("message" to "Invalid username or password"))
                }

                val token = jwtUtil.generateToken(user.username, user.role)
                return ResponseEntity.ok(mapOf("token" to token))
        }
        
}
