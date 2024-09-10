// EditProductActivity.kt
package com.example.navigationtest

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.navigationtest.databinding.ActivityEditProductBinding


class EditProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProductBinding
    private lateinit var databaseHelper: DatabaseHelper
    private var position: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)
        position = intent.getIntExtra("position", -1)


        if (position != -1) {
            initializeViews()
        } else {
            finish()
        }
    }

    private fun initializeViews() {
        val selectedItem = intent.getStringExtra("selectedItem")
        val selectedInfo = selectedItem?.split(" / ")
        val currentProductName = selectedInfo?.get(0) ?: ""
        val currentProductPrice = selectedInfo?.get(1) ?: ""
        val selectedProductName = intent.getStringExtra("selectedProductName")

        // Fetch the stock value for the selected product from the database


        // Set the retrieved stock value to the stockEditText
        binding.editTextNewProductName.setText(currentProductName)
        binding.editTextNewProductPrice.setText(currentProductPrice)

        val currentIndex = position + 1
        binding.textViewIndex.text = "Index: $currentIndex"

        binding.buttonSave.setOnClickListener {
            val newName = binding.editTextNewProductName.text.toString()
            val newPrice = binding.editTextNewProductPrice.text.toString()
            val newStock = binding.editTextStock.text.toString()
            if (newName.isNotEmpty() || newPrice.isNotEmpty()) {
                updateProduct(currentProductName, newName, newPrice.toDouble())
                setResult(RESULT_OK)
                finish()
            } else {
                // Show a toast or handle the case where both fields are empty
            }
        }

        binding.buttonDelete.setOnClickListener {
            showDeleteDialog(currentProductName)
        }

        binding.buttonCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun updateProduct(oldName: String, newName: String, newPrice: Double) {
        databaseHelper.updateProduct(oldName, newName, newPrice)
    }

    private fun showDeleteDialog(productName: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Product")
        builder.setMessage("Do you want to delete this product?")
        builder.setPositiveButton("Yes") { _, _ ->
            deleteProduct(productName)
            setResult(RESULT_OK)
            finish()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun deleteProduct(productName: String) {
        databaseHelper.deleteProduct(productName)
    }
}
