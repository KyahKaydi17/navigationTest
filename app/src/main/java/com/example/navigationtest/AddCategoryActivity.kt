package com.example.navigationtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class AddCategoryActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        databaseHelper = DatabaseHelper(this)

        val addButton = findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener {
            val categoryName = findViewById<EditText>(R.id.editTextCategoryName).text.toString()
            if (categoryName.isNotEmpty()) {
                saveCategoryToDatabase(categoryName)
                // Handle success/failure, maybe show a toast
                finish()
            } else {
                // Show error message
            }
        }
    }

    private fun saveCategoryToDatabase(categoryName: String) {
        databaseHelper.insertCategory(categoryName)
    }
}
