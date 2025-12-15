package com.base.csg.model

import java.time.LocalDateTime

data class PledgeRequest(
        val customerId: String,
        val weight: Double,
        val goldType: Int,
        val refPrice: Double,
        val loanPercent: Double,
        val loanAmount: Double,
        val interestRate: Double,
        val startDate: LocalDateTime,
        val endDate: LocalDateTime,
        val transactionType: String
)
