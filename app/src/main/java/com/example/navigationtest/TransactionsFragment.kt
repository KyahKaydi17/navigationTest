package com.example.navigationtest

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.navigationtest.databinding.FragmentTransactionsBinding
import com.google.firebase.database.*

class TransactionsFragment : Fragment() {

    private lateinit var paymentMethod: String
    private lateinit var prices: DoubleArray
    private var subtotal: Double = 0.0
    private lateinit var productNames: java.util.ArrayList<String>
    private lateinit var transaction: Transaction
    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!
    private var username: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize RecyclerView
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        transactionAdapter = TransactionAdapter(ArrayList()) { position ->
            // Handle item click on RecyclerView item
            // Retrieve transaction details and start ReceiptActivity
            transaction = transactionAdapter.getItem(position)
            // Retrieve transaction details like subtotal, product names, etc. from the transaction object

            subtotal = transaction.subtotal // Replace with your transaction object's subtotal field
            productNames = transaction.productNames // Replace with your transaction object's product names field
            prices = transaction.prices // Replace with your transaction object's prices field
            paymentMethod = transaction.paymentMethod // Replace with your transaction object's payment method field
            username = transaction.username
            // Start ReceiptActivity with retrieved details
            startReceiptActivity(subtotal, productNames, prices, paymentMethod, username)
        }
        recyclerView.adapter = transactionAdapter

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference.child("transactions")

        // Fetch transaction history from Firebase and populate RecyclerView
       refreshTransactionListFromFirebase()


        return root
    }

    private fun fetchTransactionHistory() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val transactions = mutableListOf<Transaction>()

                for (data in snapshot.children) {
                    // Retrieve data for each transaction
                    val productNames = data.child("productNames").value as ArrayList<String>
                    val subtotalData = data.child("subtotal").value
                    val subtotal = if (subtotalData is Long) {
                        subtotalData.toDouble()
                    } else {
                        (subtotalData as? Double) ?: 0.0 // Replace 0.0 with the default value if needed
                    }
                    val paymentMethod = data.child("paymentMethod").value as String
                    val prices = (data.child("prices").value as ArrayList<*>).map {
                        when (it) {
                            is Long -> it.toDouble()
                            is Double -> it
                            else -> 0.0 // or handle the unknown type in a different way
                        }
                    }.toDoubleArray()

                    val currentDate = data.child("currentDate").value as String
                    val currentTime = data.child("currentTime").value as String

                    val transaction = Transaction(productNames, subtotal, paymentMethod, prices,username ,currentDate, currentTime)
                    transactions.add(transaction)
                }

                // Update RecyclerView with transaction history
                transactionAdapter.updateTransactions(transactions)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun startReceiptActivity(
        subtotal: Double,
        productNames: ArrayList<String>,
        prices: DoubleArray,
        paymentMethod: String,
        username: String?
    ) {
        val intent = Intent (requireContext(), Transaction_Receipt::class.java).apply {
            putExtra("subtotal", subtotal)
            putStringArrayListExtra("selectedProducts", productNames)
            putExtra("prices",prices)
            putExtra("paymentMethod", paymentMethod)
            putExtra("username",username)
        }
        startActivity(intent)
    }
    private fun refreshTransactionListFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val transactions = mutableListOf<Transaction>()

                for (data in snapshot.children) {
                    // Retrieve data for each transaction
                    val productNames = data.child("productNames").value as ArrayList<String>
                    val subtotalData = data.child("subtotal").value
                    val subtotal = if (subtotalData is Long) {
                        subtotalData.toDouble()
                    } else {
                        (subtotalData as? Double) ?: 0.0 // Replace 0.0 with the default value if needed
                    }
                    val paymentMethod = data.child("paymentMethod").value as String
                    val prices = (data.child("prices").value as ArrayList<*>).map {
                        when (it) {
                            is Long -> it.toDouble()
                            is Double -> it
                            else -> 0.0 // or handle the unknown type in a different way
                        }
                    }.toDoubleArray()
                    val username = data.child("username").value as String?
                    val currentDate = data.child("currentDate").value as String
                    val currentTime = data.child("currentTime").value as String

                    val transaction = Transaction(productNames, subtotal, paymentMethod, prices, username, currentDate, currentTime)
                    transactions.add(transaction)
                }

                // Update RecyclerView with updated transaction history
                transactionAdapter.updateTransactions(transactions)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}