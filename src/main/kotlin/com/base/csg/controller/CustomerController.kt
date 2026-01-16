package com.base.csg.controller

import com.base.csg.model.CustomerGoldUpdate
import com.base.csg.model.InitialConfigRequest
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/customer")
class CustomerController(private val jdbcTemplate: JdbcTemplate) {

    // ================= Get data of customer-outer By ID ================================================
    @GetMapping("/outer/{id}")
    fun getCustomerOuterByID(@PathVariable id: String): ResponseEntity<Any> {
        val sql = "SELECT * FROM Customers_Outer WHERE customer_id = ?"

        return try {
            val result = jdbcTemplate.queryForMap(sql, id)

            ResponseEntity.ok(result)
        } catch (e: EmptyResultDataAccessException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mapOf("error" to "Customer not found!"))
        }
    }

    // ================= Get gold data of customer-outer By ID ===========================================
    @GetMapping("/outer/{id}/gold")
    fun getCustomerOuterGoldByID(@PathVariable id: String): ResponseEntity<Any> {
        val sql = "SELECT balance96, balance99 FROM Customers_Outer WHERE customer_id = ?"

        return try {
            val result = jdbcTemplate.queryForMap(sql, id)

            ResponseEntity.ok(result)
        } catch (e: EmptyResultDataAccessException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mapOf("error" to "Customer not found!"))
        }
    }

    // ================= Add or Subtract customer-outer gold balance =====================================
    @PostMapping("/outer/goldupdate")
    fun updateCustomerOuterBalance(@RequestBody req: CustomerGoldUpdate): ResponseEntity<Any> {
        val sql = "EXEC AddSubtract_Customer_Balance ?, ?, ?, ?"

        return try {
            jdbcTemplate.update(
                    sql,
                    req.customerId,
                    req.goldType,
                    req.weight,
                    req.method
            )

            ResponseEntity.ok(mapOf("success" to "Outer balance updated successfully!"))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to "Unknown error"))
        }
    }

    // ================= Get initial data (Loan%, Interest rate, Num of Installments) ====================
    @GetMapping("/initial/{id}")
    fun getCustomerInitialByID(@PathVariable id: String): ResponseEntity<Any> {
        val sql = "SELECT loan_percent, interest_rate, num_pay FROM Customers WHERE customer_id = ?"

        return try {
            val initial = jdbcTemplate.queryForMap(sql, id)

            ResponseEntity.ok(initial)
        } catch (e: EmptyResultDataAccessException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mapOf("error" to "Customer not found!"))
        }
    }

    // ================= Update Initial data (Loan%, Interest rate, Num of Installments) =================
    @PutMapping("/initial")
    fun updateCustomerInitial(@RequestBody req: InitialConfigRequest): ResponseEntity<Any> {          
        val params = mutableListOf<Any>()
        val updates = mutableListOf<String>()

        if (req.loanPercent != null) {
            updates.add("loan_percent = ?")
            params.add(req.loanPercent)
        }
        if (req.interestRate != null) {
            updates.add("interest_rate = ?")
            params.add(req.interestRate)
        }
        if (req.numPay != null) {
            updates.add("num_pay = ?")
            params.add(req.numPay)
        }
        if (updates.isEmpty()) return ResponseEntity.badRequest().body(mapOf("error" to "No fields to update"))

        var sql = "UPDATE Customers SET "
        sql += updates.joinToString(", ") + " WHERE customer_id = ?"
        params.add(req.customerId)

        val rows = jdbcTemplate.update(sql, *params.toTypedArray())

        return if (rows > 0) 
        ResponseEntity.ok(mapOf("success" to "Initial updated successfully!"))
        else
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to "Customer not found or update failed!"))
    }

}
