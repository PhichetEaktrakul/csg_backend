package com.base.csg.controller

import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("admin")
class AdminDashboardController(private val jdbcTemplate: JdbcTemplate) {

    // ===========================================================================================
    @GetMapping("/monitor/ticket/{id}")
    fun getMonitoringTicketsByID(@PathVariable id: String): ResponseEntity<Any> {
        val sql =
                """
                SELECT * FROM View_Consignment_Ticket
                WHERE status = 'active'
                AND end_date >= CAST(GETDATE() AS DATE)
                AND customer_id = ?
                ORDER BY transaction_date DESC
                """.trimIndent()

        val result = jdbcTemplate.queryForList(sql, id)
        return ResponseEntity.ok(result)
    }

    // ===========================================================================================
    @GetMapping("/monitor/ticket/all")
    fun getMonitoringTicketsAll(): ResponseEntity<Any> {
        val sql =
                """
                SELECT * FROM View_Consignment_Ticket
                WHERE status = 'active'
                AND end_date >= CAST(GETDATE() AS DATE)
                ORDER BY transaction_date DESC
                """.trimIndent()

        val result = jdbcTemplate.queryForList(sql)
        return ResponseEntity.ok(result)
    }

    // ==================== Get All Customer or Search Customer [Admin Panel] ====================
    @GetMapping("/monitor/customer", "/monitor/customer/{id}")
    fun getCustomers(@PathVariable(required = false) id: String?): ResponseEntity<Any> {
        return if (id == null) {
            val sql = "SELECT * FROM Customers"
            ResponseEntity.ok(jdbcTemplate.queryForList(sql))
        } else {
            val sql = "SELECT * FROM Customers WHERE customer_id = ?"
            ResponseEntity.ok(jdbcTemplate.queryForList(sql, id))
        }
    }

    // ===========================================================================================
    @GetMapping("/config/initial")
    fun getAllConfig(): List<Map<String, Any>> {
        val sql = "SELECT id, loan_percent, interest_rate, num_pay, extend_num_pay FROM Init_Config"
        return jdbcTemplate.queryForList(sql)
    }

    // ===========================================================================================
    @PutMapping("/config/initial")
    fun updateConfig(@RequestBody body: Map<String, Any>): ResponseEntity<Any> {
        val sql =
                """
            UPDATE Init_Config
            SET loan_percent = ?, interest_rate = ?, num_pay = ?, extend_num_pay = ?
            WHERE id = 1
        """.trimIndent()

        val loanPercent = body["loanPercent"]
        val interestRate = body["interestRate"]
        val numPay = body["numPay"]
        val extendNum = body["extendNum"]

        val rows = jdbcTemplate.update(sql, loanPercent, interestRate, numPay, extendNum)
        return if (rows > 0) {
            ResponseEntity.ok(mapOf("message" to "Updated successfully"))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    // ===========================================================================================
    @PostMapping("/order-switch")
    fun switchOrderStatus(@RequestBody payload: Map<String, Any>): ResponseEntity<Any> {
        val sql = "UPDATE Pledged_Gold SET pledge_order = ? WHERE pledge_id = ?"
        val pledgeIds = payload["pledgeIds"] as? List<String> ?: emptyList()
        val status =
                payload["status"] as? String
                        ?: return ResponseEntity.badRequest()
                                .body(mapOf("message" to "Missing 'status' field"))

        pledgeIds.forEach { id -> jdbcTemplate.update(sql, status, id) }

        return ResponseEntity.ok(mapOf("message" to "Status updated"))
    }

    // ===========================================================================================
    @PutMapping("/open-market")
    fun toggleMarket(): ResponseEntity<Any> {
        val sql =
                """
            UPDATE Open_Market
            SET market = CASE WHEN market = 1 THEN 0 ELSE 1 END
            WHERE id = 1
        """.trimIndent()
        val updated = jdbcTemplate.update(sql)
        return if (updated > 0) {
            ResponseEntity.ok(mapOf("message" to "Success! Open_Market toggled."))
        } else {
            ResponseEntity.status(404).body(mapOf("message" to "Open_Market not found with id = 1"))
        }
    }
    
}
