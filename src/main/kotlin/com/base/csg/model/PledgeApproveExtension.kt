package com.base.csg.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class PledgeApproveExtension(
        val pledgeId: String,
        val transactionId: String,
        val customerId: String,
        val startDate: LocalDateTime,
        val newEndDate: LocalDateTime,
        val interestRate: BigDecimal,
        val loanPercent: BigDecimal,
        val newLoanAmount: BigDecimal,
        val goldType: Int,
        val refPrice: BigDecimal,
        val weight: BigDecimal,
        val extend: Int,
        val method: String // "approve" or "reject"
)
