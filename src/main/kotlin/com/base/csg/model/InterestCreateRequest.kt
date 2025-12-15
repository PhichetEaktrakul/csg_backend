package com.base.csg.model

data class InterestCreateRequest(
        val interestId: String,
        val pledgeId: String,
        val payInterest: Double,
        val payLoan: Double
)
