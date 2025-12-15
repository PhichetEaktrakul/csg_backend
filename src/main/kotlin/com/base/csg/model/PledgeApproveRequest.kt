package com.base.csg.model

data class PledgeApproveRequest(
        val transactionId: String,
        val pledgeId: String,
        val customerId: String,
        val goldType: Int,
        val weight: Double,
        val loanAmount: Double,
        val method: String
)
