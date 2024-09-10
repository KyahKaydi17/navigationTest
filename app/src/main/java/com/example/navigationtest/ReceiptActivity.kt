package com.example.navigationtest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.navigationtest.databinding.ActivityReceiptBinding
import com.google.firebase.database.FirebaseDatabase
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ReceiptActivity : AppCompatActivity() {

    private var username: String? = null
    private lateinit var bitmap: Bitmap
    private lateinit var binding: ActivityReceiptBinding
    private lateinit var currentDate: String
    private lateinit var currentTime: String
    private val calendar: Calendar = Calendar.getInstance()
    private var dateFormat: SimpleDateFormat = SimpleDateFormat("MMM dd, yyyy")
    private val timeFormat: SimpleDateFormat = SimpleDateFormat("hh:mm a")


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)




        // Retrieve data from intent
        val productNames = intent.getStringArrayListExtra("selectedProducts")
        val subtotal = intent.getDoubleExtra("subtotal", 0.0)
        val paymentMethod = intent.getStringExtra("paymentMethod")
        val prices = intent.getDoubleArrayExtra("prices") ?: DoubleArray(0)
        username = intent.getStringExtra("username")




        // Display receipt in TextView
        val receiptEntries = generateReceipt(productNames, subtotal, paymentMethod, prices)
        populateReceipt(receiptEntries, subtotal, paymentMethod, prices)
        fillDateAndTime()
        getUsername()

        binding.saveButton.setOnClickListener {
            // After generating the receipt data
            // Save receipt data using SharedPreferences
            val sharedPreferences = getSharedPreferences("receipt_data", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

// Convert receiptEntries to JSON or another suitable format for storage
            val receiptEntriesJson = receiptEntries.joinToString(",")

            editor.putString("receipt_entries", receiptEntriesJson)
            editor.apply()


            val database = FirebaseDatabase.getInstance()
            val receiptRef = database.getReference("transactions")

// Create a unique key for the receipt
            val receiptKey = receiptRef.push().key

// Prepare data to be stored
            val receiptData = mapOf(
                "productNames" to productNames.orEmpty(),
                "subtotal" to subtotal,
                "paymentMethod" to paymentMethod.orEmpty(),
                "prices" to prices.toList(),
                "username" to username.toString(),
                "currentTime" to currentTime,
                "currentDate" to currentDate
            )


// Store receipt data in Firebase with the unique key
            receiptRef.child(receiptKey ?: "").setValue(receiptData)

            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("USERNAME", username)
            }
            startActivity(intent)



            Toast.makeText(this, "Receipt saved in transactions.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateReceipt(selectedProducts: List<String>?, subtotal: Double, paymentMethod: String?, prices: DoubleArray): List<String> {
        if (selectedProducts.isNullOrEmpty() || prices.isEmpty() || selectedProducts.size != prices.size) {
            return listOf("No items purchased.")
        }

        val productEntries = mutableListOf<String>()


        for (i in selectedProducts.indices) {
            val productName = selectedProducts[i].split("-")[0].trim()
            val quantity = 1
            val price = prices[i]

            val entry = "$quantity x $productName -> $price (${String.format("$%.2f", price)})"
            productEntries.add(entry)
        }

        return productEntries
    }

    private fun fillDateAndTime() {
        val dateTextView: TextView = findViewById(R.id.dateAndTimeTextView)


        currentDate = dateFormat.format(calendar.time)
        currentTime = timeFormat.format(calendar.time)

        dateTextView.text = "Date: $currentDate - $currentTime"

    }

    private fun populateReceipt(receiptEntries: List<String>, subtotal: Double, paymentMethod: String?, prices: DoubleArray) {
        val productMap = mutableMapOf<String, Int>() // Map to store quantity per product

        for (i in receiptEntries.indices) {
            val entry = receiptEntries[i]
            val parts = entry.split("->")
            if (parts.size == 2) {
                val quantityProductName = parts[0].trim()
                val productName = quantityProductName.substringAfterLast("x").trim() // Extract product name
                val quantity = quantityProductName.substringBefore("x").trim().toIntOrNull() ?: 0 // Extract quantity

                if (productMap.containsKey(productName)) {
                    val currentQuantity = productMap[productName] ?: 0
                    productMap[productName] = currentQuantity + quantity
                } else {
                    productMap[productName] = quantity
                }
            }
        }

        for ((productName, quantity) in productMap) {
            val productCard = layoutInflater.inflate(R.layout.product_card_layout, null)
            val productNameTextView = productCard.findViewById<TextView>(R.id.productNameTextView)
            val totalPriceTextView = productCard.findViewById<TextView>(R.id.totalPriceTextView)
            val quantityPriceTextView = productCard.findViewById<TextView>(R.id.quantityPriceTextView)

            productNameTextView.text = productName
            val price = prices.firstOrNull { it != 0.0 } ?: 0.0 // Extract the first non-zero price

            val quantityPriceText = "$quantity x ${String.format("₱%.2f", price)}"
            quantityPriceTextView.text = quantityPriceText

            val totalPrice = price * quantity
            totalPriceTextView.text = String.format("₱%.2f", totalPrice)

            binding.productEntriesLinearLayout.addView(productCard) // Add the populated CardView to your layout
        }

        // Set Subtotal and Payment Method TextViews
        val subtotalTextView: TextView = findViewById(R.id.subtotalTextView)
        val paymentMethodTextView: TextView = findViewById(R.id.paymentMethodTextView)

        subtotalTextView.text = String.format("Subtotal: ₱%.2f", subtotal)
        paymentMethodTextView.text = String.format("Payment Method: %s", paymentMethod)
    }

    fun getUsername(){
      var employeeTextView :TextView = findViewById(R.id.employee)
      employeeTextView.text = "Employee: $username"
    }


    private fun extractPrice(productInfo: String): Double {
        val components = productInfo.split("-")
        if (components.size >= 2) {
            val priceString = components[1].trim()
            return priceString.toDoubleOrNull() ?: 0.0
        }
        return 0.0
    }

}







