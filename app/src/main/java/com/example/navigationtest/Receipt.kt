package com.example.navigationtest

data class Receipt(
    val productNames: List<String>,
    val subtotal: Double,
    val paymentMethod: String,
    val prices: DoubleArray,
    val username: String?,
    val currentTime: String,
    val currentDate: String
)
