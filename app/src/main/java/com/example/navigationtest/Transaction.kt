package com.example.navigationtest
import java.util.*
import kotlin.collections.ArrayList

data class Transaction(
    val productNames: ArrayList<String>,
    val subtotal: Double,
    val paymentMethod: String,
    val prices: DoubleArray,
    val username:String?,
    val currentTime: String,
    val currentDate: String,
    val transactionId: String = UUID.randomUUID().toString() // Unique identifier for the transaction
)
