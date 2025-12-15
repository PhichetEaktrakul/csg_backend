package com.base.csg.model

data class InterestApproveRequest(
        val interestId: String,
        val transactionId: String,
        val pledgeId: String,
        val dueDate: String,
        val endDate: String,
        val interestAmount: Double,
        val loanAmount: Double,
        val intRate: Double,
        val method: String
)
