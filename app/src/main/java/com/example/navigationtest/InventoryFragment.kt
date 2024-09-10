package com.example.navigationtest


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.navigationtest.databinding.FragmentInventoryBinding
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class InventoryFragment : Fragment() {


    private var sharedPrefs: SharedPreferences? = null
    private lateinit var dbHelper: DatabaseHelper

    private var _binding: FragmentInventoryBinding? = null
    private lateinit var spinnerCategory: Spinner
    private val categories = mutableListOf<String>()
    private lateinit var productAdapter : ProductAdapter

    private val binding get() = _binding!!

    companion object {
        private const val READ_REQUEST_CODE: Int = 42
        private const val CATEGORY_PREF_KEY = "Categories"
        const val EDIT_PRODUCT_REQUEST_CODE = 1
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        dbHelper = DatabaseHelper(requireContext())

        binding.buttonAdd.setOnClickListener {
            addProduct()
            binding.editTextProductName
        }

        binding.buttonViewAll.setOnClickListener {
            val intent = Intent(activity, ViewActivity::class.java)
            startActivity(intent)
        }

        binding.buttonImportCSV.setOnClickListener {
            openFileManager()
        }
        binding.deleteAll.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Delete Records")
            builder.setMessage("Are you sure you want to delete the records?")
            builder.setPositiveButton("Delete") { _, _ ->
                // Handle deletion here
                dbHelper.clearAllProducts()
                clearSpinnerAdapter()

                Toast.makeText(requireContext(), "Records deleted", Toast.LENGTH_SHORT).show()
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            val alertDialog = builder.create()
            alertDialog.show()

        }
        binding.addCategory.setOnClickListener {
            showAddCategoryDialog()
        }

        spinnerCategory = binding.spinnerCategory

            loadCategories() // Load saved categories from shared preferences

        setupSpinner()



        return root
    }

    private fun openFileManager() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"

        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                try {
                    val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
                    inputStream?.let {
                        val reader = BufferedReader(InputStreamReader(it))
                        var line: String?
                        val importedData = mutableListOf<String>()
                        while (reader.readLine().also { line = it } != null) {
                            processCsvLine(line)
                            importedData.add(line!!)
                        }

                        val viewIntent = Intent(activity, ViewActivity::class.java)
                        viewIntent.putStringArrayListExtra("importedData", ArrayList(importedData))
                        startActivity(viewIntent)

                        Toast.makeText(requireContext(), "CSV file imported successfully", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Error reading CSV file", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun processCsvLine(line: String?) {
        try {
            line?.let {
                val values = it.split(",").map { field -> field.trim() }

                if (values.size == 4) {
                    val name = values[0]
                    val category = values[1]
                    val price = values[2].toDoubleOrNull()

                    if (name.isNotEmpty() && category.isNotEmpty() && price != null ) {
                        dbHelper.insertProduct(name, price, category)

                        if (!categories.contains(category)) {
                            addNewCategory(category)
                        } 
                    } else {
                        Toast.makeText(requireContext(),"Invalid Type, Try Again",Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Log or handle cases where the number of fields is not as expected
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addProduct() {
        val productName = binding.editTextProductName.text.toString()
        val productPrice = binding.editTextProductPrice.text.toString()
        val category = spinnerCategory.selectedItem.toString()

        if (productName.isNotEmpty() && productPrice.isNotEmpty() && category.isNotEmpty()) {
            dbHelper.insertProduct(productName, productPrice.toDouble(), category)
            Toast.makeText(requireContext(), "Product added successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "All fields must be filled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = categories[position]
                // Handle the selection of the category
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle when nothing is selected
            }
        }
    }

    private fun clearSpinnerAdapter() {
        val adapter = spinnerCategory.adapter as ArrayAdapter<String>
        adapter.clear()
        adapter.notifyDataSetChanged()
    }


    private fun addNewCategory(newCategory: String) {
        if (!categories.contains(newCategory)) {
            categories.add(newCategory)
            setupSpinner()
            saveCategories() // Save the updated categories to shared preferences
        }
    }

    private fun loadCategories() {
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val savedCategories = sharedPreferences.getStringSet(CATEGORY_PREF_KEY, emptySet()) ?: emptySet()
        categories.clear()
        categories.addAll(savedCategories)
    }

    private fun saveCategories() {
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet(CATEGORY_PREF_KEY, categories.toSet())
        editor.apply()
    }

    private fun showAddCategoryDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_category, null)
        val editTextCategory = dialogView.findViewById<EditText>(R.id.editTextNewCategory)

        builder.setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val newCategory = editTextCategory.text.toString()
                if (newCategory.isNotEmpty()) {
                    addNewCategory(newCategory)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
