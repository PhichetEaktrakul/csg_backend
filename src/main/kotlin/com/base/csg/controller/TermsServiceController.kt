package com.base.csg.controller

import com.base.csg.model.CustomerOuter
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tos")
class TermsServiceController(private val jdbcTemplate: JdbcTemplate) {

    // ================= Check if Customer already accept TOS ===================
    @GetMapping("/check/{id}")
    fun checkCustomerTOS(@PathVariable id: String): ResponseEntity<Boolean> {
        val sql = "SELECT COUNT(*) FROM Customers WHERE customer_id = ?"
        val count = jdbcTemplate.queryForObject(sql, Int::class.java, id)
        val exists = count != null && count > 0
        return ResponseEntity.ok(exists)
    }

    // ================= Add Customer after Customer accept TOS =================
    @PostMapping("/accept")
    fun acceptCustomerTOS(@RequestBody custOuter: CustomerOuter): ResponseEntity<Any> {
        return try {
            val config =
                    jdbcTemplate.queryForMap(
                            "SELECT TOP 1 loan_percent, interest_rate, num_pay FROM Init_Config ORDER BY id DESC"
                    )
            val res =
                    jdbcTemplate.update(
                            """
                            INSERT INTO Customers
                            (customer_id, first_name, last_name, phone_number, id_card_number, address, loan_percent, interest_rate, num_pay)
                            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                            """
                            .trimIndent(),
                            custOuter.customerId,
                            custOuter.firstname,
                            custOuter.lastname,
                            custOuter.phonenumber,
                            custOuter.idcard,
                            custOuter.address,
                            config["loan_percent"],
                            config["interest_rate"],
                            config["num_pay"]
                    )

            ResponseEntity.ok(if (res > 0) "Customer add success" else "Customer add failed")
        } catch (e: Exception) {
            ResponseEntity.status(500).body(e.message ?: "Unknown error")
        }
    }
    
}
