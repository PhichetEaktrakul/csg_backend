package com.base.csg.controller

import com.base.csg.model.PledgeApproveExtension
import com.base.csg.model.PledgeApproveRequest
import com.base.csg.model.PledgeExtensionRequest
import com.base.csg.model.PledgeRequest
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcCall
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/pledge")
class PledgeController(private val jdbcTemplate: JdbcTemplate) {

    // ========================================================================
    @PostMapping("/create")
    fun newPledgeTransaction(@RequestBody req: PledgeRequest): ResponseEntity<Any> {
        val sql = "{call New_Pledge_Transaction(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}"

        return try {
            val result =
                    jdbcTemplate.queryForMap(
                            sql,
                            req.customerId,
                            req.weight,
                            req.goldType,
                            req.refPrice,
                            req.loanPercent,
                            req.loanAmount,
                            req.interestRate,
                            req.startDate,
                            req.endDate,
                            req.transactionType
                    )

            ResponseEntity.ok(result["pledge_id"])
        } catch (e: Exception) {
            ResponseEntity.badRequest().body("Error: ${e.message}")
        }
    }

    // ========================================================================
    @GetMapping("/history/{id}")
    fun getPledgeHistoryByID(@PathVariable id: String): ResponseEntity<Any> {
        val sql =
                """
                SELECT * FROM View_Pledge_History
                WHERE customer_id = ?
                ORDER BY transaction_date DESC
                """.trimIndent()

        return ResponseEntity.ok(jdbcTemplate.queryForList(sql, id))
    }

    // ========================================================================
    @GetMapping("/history/all")
    fun getPledgeHistoryAll(): ResponseEntity<Any> {
        val sql = "SELECT * FROM View_Pledge_History ORDER BY transaction_date DESC"
        return ResponseEntity.ok(jdbcTemplate.queryForList(sql))
    }

    // ========================================================================
    @PostMapping("/approve/status")
    fun approvePledgeTransaction(@RequestBody req: PledgeApproveRequest): ResponseEntity<Any> {
        val sql = "EXEC Approve_Pledge_Transaction ?, ?, ?, ?, ?, ?, ?"

        return try {
            jdbcTemplate.update(
                    sql,
                    req.transactionId,
                    req.pledgeId,
                    req.customerId,
                    req.goldType,
                    req.weight,
                    req.loanAmount,
                    req.method
            )
            ResponseEntity.ok("Update successfully")
        } catch (e: Exception) {
            ResponseEntity.badRequest().body("Error: ${e.message}")
        }
    }

    // ========================================================================
    @GetMapping("/extend/all")
    fun getPledgeExtendRequest(): ResponseEntity<Any> {
        val sql = "SELECT * FROM View_Extend_History ORDER BY create_at DESC"
        return ResponseEntity.ok(jdbcTemplate.queryForList(sql))
    }

    // ========================================================================
    @PostMapping("/extend")
    fun newPledgeExtendRequest(@RequestBody req: PledgeExtensionRequest): ResponseEntity<Any> {
        return try {
            val result =
                    SimpleJdbcCall(jdbcTemplate)
                            .withProcedureName("New_Pledge_Extension_Request")
                            .execute(
                                    mapOf(
                                            "pledge_id" to req.pledgeId,
                                            "customer_id" to req.customerId,
                                            "start_date" to req.startDate,
                                            "old_end_date" to req.oldEndDate,
                                            "extend" to req.extend,
                                            "interest_rate" to req.interestRate,
                                            "loan_percent" to req.loanPercent,
                                            "payment_in" to req.paymentIn,
                                            "payment_out" to req.paymentOut,
                                            "gold_type" to req.goldType,
                                            "ref_price" to req.refPrice,
                                            "weight" to req.weight,
                                            "new_loan_amount" to req.newLoanAmount
                                    )
                            )

            val row =
                    result["#result-set-1"].let {
                        if (it is List<*>) {
                            @Suppress("UNCHECKED_CAST")
                            (it as? List<Map<String, Any>>)?.firstOrNull()
                        } else {
                            null
                        }
                    }

            ResponseEntity.ok(
                    mapOf(
                            "status" to row?.get("status"),
                            "requestId" to row?.get("request_id"),
                            "transactionId" to row?.get("transaction_id"),
                            "newEndDate" to row?.get("new_end_date"),
                            "message" to row?.get("message")
                    )
            )
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("status" to "error", "message" to e.message))
        }
    }

    // ========================================================================
    @PostMapping("/extend/approve/status")
    fun approvePledgeExtension(@RequestBody req: PledgeApproveExtension): ResponseEntity<Any> {

        return try {
            val call = SimpleJdbcCall(jdbcTemplate).withProcedureName("Approve_Pledge_Extension")

            val params =
                    mapOf(
                            "pledge_id" to req.pledgeId,
                            "transaction_id" to req.transactionId,
                            "customer_id" to req.customerId,
                            "start_date" to req.startDate,
                            "new_end_date" to req.newEndDate,
                            "interest_rate" to req.interestRate,
                            "loan_percent" to req.loanPercent,
                            "new_loan_amount" to req.newLoanAmount,
                            "gold_type" to req.goldType,
                            "ref_price" to req.refPrice,
                            "weight" to req.weight,
                            "extend" to req.extend,
                            "method" to req.method
                    )

            call.execute(params)

            ResponseEntity.ok(
                    mapOf("status" to "success", "message" to "Operation completed: ${req.method}")
            )
        } catch (e: Exception) {
            ResponseEntity.status(500)
                    .body(mapOf("status" to "error", "message" to (e.message ?: "Unknown error")))
        }
    }
}
