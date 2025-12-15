package com.base.csg.model

data class RedeemCreateRequest(
        val transactionId: String,
        val pledgeId: String,
        val principalPay: Double,
        val interestPay: Double
)
