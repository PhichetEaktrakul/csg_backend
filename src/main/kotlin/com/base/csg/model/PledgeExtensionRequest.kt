package com.base.csg.model

data class PledgeExtensionRequest(
        val pledgeId: String,
        val customerId: String,
        val startDate: String,
        val oldEndDate: String,
        val extend: Int,
        val interestRate: Double,
        val loanPercent: Double,
        val paymentIn: Double,
        val paymentOut: Double,
        val goldType: Int,
        val refPrice: Double,
        val weight: Double,
        val newLoanAmount: Double
)
