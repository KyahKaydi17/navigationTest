package com.example.navigationtest

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.navigationtest.databinding.ActivityViewBinding


class ViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewBinding
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var productAdapter: ProductAdapter
    private var products: MutableList<String> = mutableListOf()
    private var filteredProducts: MutableList<String> = mutableListOf()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)
        products.addAll(databaseHelper.getAllProducts())

        // Set up RecyclerView
        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(this)

        binding.buttonFilter.setOnClickListener {
            val selectedCategory = binding.spinnerCategory.selectedItem.toString()
            if (selectedCategory != "All") {
                filteredProducts = products.filter { it.split(" / ")[2] == selectedCategory }.toMutableList()
                productAdapter.updateList(filteredProducts)
            } else {
                filteredProducts.clear()
                filteredProducts.addAll(products)
                productAdapter.updateList(products)
            }
        }

        val categories = databaseHelper.getAllCategories().toMutableList()
        categories.add(0, "All")
        setupSpinner(categories)
        setupProductAdapter()
    }
    private fun filterProducts(query: String) {
        val filteredProducts = products.filter { product ->
            product.contains(query, ignoreCase = true)
        }
        productAdapter.updateList(filteredProducts)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        val searchItem = menu?.findItem(R.id.search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterProducts(it) }
                return true
            }
        })

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                // Handle search icon click here
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    private fun setupProductAdapter() {
        productAdapter = ProductAdapter(products) { position ->
            val selectedItem = if (filteredProducts.isEmpty()) {
                products[position]
            } else {
                filteredProducts[position]
            }
            val index = filteredProducts.indexOf(selectedItem)
            val intent = Intent(this@ViewActivity, EditProductActivity::class.java).apply {
                putExtra("selectedItem", selectedItem)
                putExtra("position", index)
            }
            startActivityForResult(intent, EDIT_PRODUCT_REQUEST_CODE)
        }
        binding.recyclerViewProducts.adapter = productAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PRODUCT_REQUEST_CODE && resultCode == RESULT_OK) {
            // Refresh the product list if changes are made
            products.clear()
            products.addAll(databaseHelper.getAllProducts())
            setupProductAdapter()
        }
    }

    private fun setupSpinner(categories: MutableList<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    companion object {
        const val EDIT_PRODUCT_REQUEST_CODE = 1
    }


}
