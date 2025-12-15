package com.base.csg.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.util.*
import org.springframework.stereotype.Component

@Component
class JwtUtil {
        private val secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
        private val expirationMs = 86400000 // 1 day

        fun generateToken(username: String, role: String): String =
                Jwts.builder()
                        .setSubject(username)
                        .claim("role", role)
                        .setIssuedAt(Date())
                        .setExpiration(Date(System.currentTimeMillis() + expirationMs))
                        .signWith(secretKey)
                        .compact()
}
