package com.base.csg.model

data class TokenRequest(
        val customerId: String,
        val firstname: String,
        val lastname: String,
        val phonenumber: String,
        val idcard: String,
        val address: String,
        val source: String
)
