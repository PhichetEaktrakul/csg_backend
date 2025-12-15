package com.base.csg.controller

import com.base.csg.model.TokenRequest
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("token")
class TokenController(@Value("\${myapp.api.url}") private val apiURL: String) {

    // ================= Encode JSON to Token =================
    @PostMapping("/encode")
    fun encodeToken(@RequestBody token: TokenRequest): ResponseEntity<Any> {
        val customerId = token.customerId
        val firstname = token.firstname
        val lastname = token.lastname
        val phone = token.phonenumber
        val idcard = token.idcard
        val address = token.address
        val source = token.source

        val rawToken = "customerId=$customerId&firstname=$firstname&lastname=$lastname&phone=$phone&idcard=$idcard&address=$address&source=$source"
        val keyString = "5dkoaldjcmsldkwo75dd52s5d6d3v5a7"

        val encryptedToken = encrypt(keyString, rawToken)
        val url = "$apiURL?token=${URLEncoder.encode(encryptedToken, StandardCharsets.UTF_8)}"

        return ResponseEntity.ok(url)
    }

    // ================= Decode Token to JSON =================
    @GetMapping("/decode")
    fun decodeToken(@RequestParam token: String): ResponseEntity<Any> {
        return try {
            println("Received token (raw): $token")

            val key = "5dkoaldjcmsldkwo75dd52s5d6d3v5a7"
            val decrypted = decrypt(key, token)

            println("Decrypted: $decrypted")

            val result =
                    decrypted
                            .split("&")
                            .mapNotNull {
                                val parts = it.split("=")
                                if (parts.size == 2) parts[0] to parts[1] else null
                            }
                            .toMap()

            ResponseEntity.ok(result)
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.badRequest().body("Invalid token or decryption error.")
        }
    }

    private fun encrypt(key: String, plainText: String): String {
        val iv = "vy1sDUUiXplyTJbB".toByteArray(StandardCharsets.UTF_8)
        val keyBytes = key.toByteArray(StandardCharsets.UTF_8)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val secretKey = SecretKeySpec(keyBytes, "AES")
        val ivSpec = IvParameterSpec(iv)

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
        val encrypted = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))

        return Base64.getEncoder().encodeToString(encrypted)
    }

    companion object {
        @JvmStatic
        fun decrypt(key: String, cipherText: String): String {
            val iv = "vy1sDUUiXplyTJbB".toByteArray(StandardCharsets.UTF_8)
            val keyBytes = key.toByteArray(StandardCharsets.UTF_8)
            val cipherBytes = Base64.getDecoder().decode(cipherText)

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val secretKey = SecretKeySpec(keyBytes, "AES")
            val ivSpec = IvParameterSpec(iv)

            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
            val decrypted = cipher.doFinal(cipherBytes)
            return String(decrypted, StandardCharsets.UTF_8)
        }
    }

}
