package com.base.csg.model

data class RedeemApproveRequest(
        val transactionId: String,
        val pledgeId: String,
        val goldType: Int,
        val intPaid: Double,
        val prinPaid: Double,
        val weight: Double,
        val custId: String,
        val method: String
)
