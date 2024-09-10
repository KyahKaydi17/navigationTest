package com.example.navigationtest

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.navigationtest.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var username: String?= null
    private lateinit var productNames: MutableList<String>
    private var _binding: FragmentHomeBinding? = null
    private var products: MutableList<String> = mutableListOf()
    lateinit var searchView: SearchView
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var subtotalTextView: TextView
    private lateinit var selectedItemCountTextView: TextView
    private lateinit var recyclerView : RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private val binding get() = _binding!!
    private var selectedItemCount =0
    private var currentSubtotal = 0.0
    private val selectedProducts = mutableListOf<String>()




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize database helper
        databaseHelper = DatabaseHelper(requireContext())
        products.addAll(databaseHelper.getAllProducts())

        // Initialize UI components
        subtotalTextView = binding.subtotalTextView
        selectedItemCountTextView = binding.selectedItemCountTextView
        recyclerView = binding.recyclerViewProducts
        searchView = binding.searchView
        username = arguments?.getString("USERNAME")
        // Set up RecyclerView layout manager
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set up RecyclerView adapter (replace this with your custom adapter)
        val adapter = ProductAdapter(databaseHelper.getAllProducts()) { position ->
            val selectedProduct = databaseHelper.getAllProducts()[position]

        }

        adapter.setOnItemClickListener { position ->
            val selectedProduct = databaseHelper.getAllProducts()[position]
            updateSubtotal(position)
            addSelectedProduct(selectedProduct)

        }

        recyclerView.adapter = adapter
        productAdapter = adapter // Initialize productAdapter here

        // Set up RecyclerView item click listener

        // Other button click listeners remain unchanged
        binding.clearBtn.setOnClickListener {
            clearSubtotal()
        }

        binding.chargeBtn.setOnClickListener {

            val intent = Intent(requireContext(), PaymentActivity::class.java).apply {
                productNames = mutableListOf<String>() // Retrieve your product names
                val prices = mutableListOf<Double>() // Retrieve your prices

                    // Separate selectedProducts into product names and prices
                    for (productInfo in selectedProducts) {
                        val components = productInfo.split("/")
                        val productName = components[0].trim() // Extract product name
                        val price = components[1].trim().toDoubleOrNull()
                            ?: 0.0 // Extract and convert price

                        productNames.add(productName)
                        prices.add(price)
                    }

                    // Pass product names as string array and prices as double array

                    putStringArrayListExtra("productNames", ArrayList(productNames))
                    putExtra("prices", prices.toDoubleArray())
                    putExtra("subtotal", currentSubtotal)
                putExtra("username" , username)
                }
            if (productNames.isEmpty()) {
                Toast.makeText(requireContext(), "Please select item first", Toast.LENGTH_SHORT)
                    .show()

            } else {
                startActivity(intent)
            }
        }


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search query submission (if needed)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle search query text change
                newText?.let {
                    filterProducts(it) // Call function to search the database with the entered text
                }
                return true
            }
        })

        return root

    }


    private fun filterProducts(query: String) {
        val filteredProducts = products.filter { product ->
            product.contains(query, ignoreCase = true)
        }
        productAdapter.updateList(filteredProducts)
    }

    private fun extractStock(productInfo: String): Int {
        val components = productInfo.split("/")
        if (components.size >= 3) {
            return components[2].trim().toIntOrNull() ?: 0
        }
        return 0
    }




    private fun updateSubtotal(position: Int) {
        val selectedProduct = databaseHelper.getAllProducts()[position]
        val price = extractPrice(selectedProduct)

        // If currentSubtotal is 0, just show the current price
        if (currentSubtotal == 0.0) {
            currentSubtotal = price
        } else {
            // Otherwise, add the current price to the subtotal
            currentSubtotal += price
        }

        subtotalTextView.text = "Subtotal: ₱$currentSubtotal"

        // Update selectedItemCount and display it in the textView
        selectedItemCount++
        selectedItemCountTextView.text = "Selected Items: $selectedItemCount"
    }




    private fun calculateSubtotal(): Double {
        // Calculate subtotal by summing up the prices of selected products
        return selectedProducts.map { extractPrice(it) }.sum()
    }

    private fun extractPrice(productInfo: String): Double {
        // Split the product information into components
        val components = productInfo.split("/")

        // Ensure the product information has at least two components (Name-Price-Category)
        if (components.size >= 2) {
            // Extract the price (assuming it's the second component)
            val priceString = components[1].trim()

            // Convert the price string to Double or return 0.0 if conversion fails
            return priceString.toDoubleOrNull() ?: 0.0
        }

        // Return 0.0 if the product information doesn't match the expected format
        return 0.0
    }

    // Function to add a selected product to the list
    private fun addSelectedProduct(productInfo: String) {
        val components = productInfo.split("/")
        selectedProducts.add(productInfo)
    }

    // Function to remove a selected product from the list
    private fun removeSelectedProduct(productInfo: String) {
        selectedProducts.remove(productInfo)
    }

    private fun clearSubtotal() {
        // Clear the selected products list
        selectedProducts.clear()
        currentSubtotal = 0.0
        // Reset the subtotalTextView to 0.0
        subtotalTextView.text = "Subtotal: ₱$currentSubtotal"
        selectedItemCount = 0
        selectedItemCountTextView.text = "Selected Items: $selectedItemCount"


    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
