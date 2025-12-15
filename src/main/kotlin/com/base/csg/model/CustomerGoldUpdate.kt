package com.base.csg.model

data class CustomerGoldUpdate(
        val customerId: String,
        val goldType: Int,
        val weight: Double,
        val method: String
)
