package com.example.navigationtest

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.navigationtest.databinding.ActivityPaymentBinding
class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private var username : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve subtotal from intent
        val productNames = intent.getStringArrayListExtra("productNames")
        val prices = intent.getDoubleArrayExtra("prices")
        val subtotal = intent.getDoubleExtra("subtotal", 0.0)
        val paymentMethod = intent.getStringExtra("paymentMethod")
        username = intent.getStringExtra("username")

        // Pass the username to ReceiptActivity


        // Display subtotal in TextView
        val subtotalTextView: TextView = binding.subtotalTextView
        subtotalTextView.text = "Subtotal: ₱$subtotal "

        // Set up RadioGroup
        val paymentMethodGroup: RadioGroup = binding.paymentMethodGroup


        // Set up Confirm Payment button
        val confirmPaymentBtn = binding.confirmPaymentBtn
        confirmPaymentBtn.setOnClickListener {
            // Get the selected payment method
            val selectedPaymentMethod = when (paymentMethodGroup.checkedRadioButtonId) {
                R.id.radioCash -> "Cash"
                R.id.radioGrabPay -> "Grab Pay"
                R.id.radioCreditCard5 -> "Credit Card"
                R.id.radioGcash -> "Gcash"
                else -> ""
            }
            if (selectedPaymentMethod.isEmpty()) {
                Toast.makeText(this, "Select method first.", Toast.LENGTH_SHORT).show()
            } else {
                // Start ReceiptActivity
                startReceiptActivity(subtotal, productNames, prices, selectedPaymentMethod,username)
            }
        }
    }

    private fun startReceiptActivity(
        subtotal: Double,
        selectedProducts: ArrayList<String>?,
        prices: DoubleArray?, // Include prices here
        paymentMethod: String?,
        username : String?
    ) {

        val intent = Intent(this, ReceiptActivity::class.java).apply {
            putExtra("subtotal", subtotal)
            putStringArrayListExtra("selectedProducts", selectedProducts)
            putExtra("prices",prices)
            putExtra("paymentMethod", paymentMethod)
            putExtra("username",username)
        }


        startActivity(intent)
    }
}
