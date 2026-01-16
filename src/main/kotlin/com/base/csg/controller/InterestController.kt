package com.base.csg.controller

import com.base.csg.model.InterestApproveRequest
import com.base.csg.model.InterestCreateRequest
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/interest")
class InterestController(private val jdbcTemplate: JdbcTemplate) {

    // ================= Get List of Payable Interest by id =====================
    @GetMapping("/payable/{id}")
    fun getPayableInterestByID(@PathVariable id: String): ResponseEntity<Any> {
        val sql =
                """
                SELECT * FROM View_Payable_Interest
                WHERE customer_id = ? AND end_date >= CAST(GETDATE() AS DATE)
                ORDER BY due_date ASC
                """
                .trimIndent()

        return try {
            val result = jdbcTemplate.queryForList(sql, id)

            ResponseEntity.ok(result)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    // ================= Create new Interest transaction ========================
    @PostMapping("/create")
    fun newInterestTransaction(@RequestBody req: InterestCreateRequest): ResponseEntity<Any> {
        val sql = "EXEC Pay_Interest_Transaction ?, ?, ?, ?"

        return try {
            jdbcTemplate.update(
                    sql,
                    req.interestId,
                    req.pledgeId,
                    req.payInterest,
                    req.payLoan
            )

            ResponseEntity.ok(mapOf("success" to "New Interest transaction created successfully!"))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    // ================= Get Interest transaction history by id =================
    @GetMapping("/history/{id}")
    fun getInterestHistoryByID(@PathVariable id: String): ResponseEntity<Any> {
        val sql =
                """
                SELECT * FROM View_Interest_History
                WHERE customer_id = ?
                ORDER BY transaction_date DESC
                """
                .trimIndent()

        return try {
            val result = jdbcTemplate.queryForList(sql, id)

            ResponseEntity.ok(result)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("error" to e.message))
        }
    }

    // ================= Get all Interest transaction history ===================
    @GetMapping("/history/all")
    fun getInterestHistoryAll(): ResponseEntity<Any> {
        val sql = "SELECT * FROM View_Interest_History ORDER BY transaction_date DESC"

        return try {
            ResponseEntity.ok(jdbcTemplate.queryForList(sql))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("error" to e.message))
        }
    }

    // ================= Approve or Reject Interest transaction =================
    @PostMapping("/approve/status")
    fun approveInterestTransaction(@RequestBody req: InterestApproveRequest): ResponseEntity<Any> {
        val sql = "EXEC Approve_Interest_Transaction ?, ?, ?, ?, ?, ?, ?, ?, ?"

        return try {
            jdbcTemplate.update(
                    sql,
                    req.interestId,
                    req.transactionId,
                    req.pledgeId,
                    req.dueDate,
                    req.endDate,
                    req.interestAmount,
                    req.loanAmount,
                    req.intRate,
                    req.method
            )

            ResponseEntity.ok(mapOf("success" to "Update status successfully!"))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    // ================= Get Interest summary by id =============================
    @GetMapping("/summary/{id}")
    fun getInterestSummaryByID(@PathVariable id: String): ResponseEntity<Any> {
        val sql =
                """
                SELECT * FROM Interest_Summary
                WHERE pledge_id = ?
                """
                .trimIndent()

        return try {
            val result = jdbcTemplate.queryForMap(sql, id)

            ResponseEntity.ok(result)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("error" to e.message))
        }
    }

}
