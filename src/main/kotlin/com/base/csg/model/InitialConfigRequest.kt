package com.base.csg.model

import java.math.BigDecimal

data class InitialConfigRequest(
        val customerId: String,
        val loanPercent: BigDecimal?,
        val interestRate: BigDecimal?,
        val numPay: Int?
)
