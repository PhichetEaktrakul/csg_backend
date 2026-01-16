package com.base.csg.controller

import com.base.csg.model.RedeemApproveRequest
import com.base.csg.model.RedeemCreateRequest
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/redeem")
class RedeemController(private val jdbcTemplate: JdbcTemplate) {

    // ================= Get list of Redeemable pledge by id ==================
    @GetMapping("/list/{id}")
    fun getRedeemablePledgeByID(@PathVariable id: String): ResponseEntity<Any> {
        val sql =
                """
                SELECT * FROM View_Redeemable_Pledge
                WHERE customer_id = ?
                ORDER BY end_date ASC
                """
                .trimIndent()

        return try {
            val result = jdbcTemplate.queryForList(sql, id)

            ResponseEntity.ok(result)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("error" to e.message))
        }
    }

    // ================= Create new Redeem transaction ========================
    @PostMapping("/create")
    fun newRedeemTransaction(@RequestBody req: RedeemCreateRequest): ResponseEntity<Any> {
        val sql = "EXEC New_Redeem_Transaction ?, ?, ?, ?"

        return try {
            jdbcTemplate.update(
                    sql,
                    req.transactionId,
                    req.pledgeId,
                    req.principalPay,
                    req.interestPay
            )

            ResponseEntity.ok(mapOf("success" to "New Redeem transaction created successfully!"))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    // ================= Get Redeem transaction history by id =================
    @GetMapping("/history/{id}")
    fun getRedeemHistoryByID(@PathVariable id: String): ResponseEntity<Any> {
        val sql =
                """
                SELECT * FROM View_Redeem_History
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

    // ================= Get all Redeem transaction history ===================
    @GetMapping("/history/all")
    fun getRedeemHistoryAll(): ResponseEntity<Any> {
        val sql = "SELECT * FROM View_Redeem_History ORDER BY transaction_date DESC"

        return try {
            ResponseEntity.ok(jdbcTemplate.queryForList(sql))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("error" to e.message))
        }
    }

    // ================= Approve or Reject Redeem transaction =================
    @PostMapping("/approve/status")
    fun approveRedeemTransaction(@RequestBody req: RedeemApproveRequest): ResponseEntity<Any> {
        val sql = "EXEC Approve_Redeem_Transaction ?, ?, ?, ?, ?, ?, ?, ?"

        return try {
            jdbcTemplate.update(
                    sql,
                    req.transactionId,
                    req.pledgeId,
                    req.goldType,
                    req.intPaid,
                    req.prinPaid,
                    req.weight,
                    req.custId,
                    req.method
            )

            ResponseEntity.ok(mapOf("success" to "Update successfully"))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
    
}
