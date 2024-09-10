package com.example.navigationtest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    private var productList: List<String>,
    private var onItemClick: (Int) -> Unit // Function to handle item clicks
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var filteredList: List<String> = productList.toList()

    // Function to update the list and apply filtering
    fun updateList(newList: List<String>) {
        productList = newList.toList()
        filteredList = productList.toList()
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClick = listener
    }

    fun filterByCategory(category: String) {
        filteredList = if (category.isEmpty()) {
            productList.toList() // Copy the productList to filteredList
        } else {
            productList.filter { it.split(" / ")[2] == category }
        }
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        // Ensure position is within bounds
        if (position < 0 || position >= filteredList.size) return

        val productInfo = filteredList[position].split(" / ")
        // Check if the split result has the expected number of parts
        if (productInfo.size >= 3) {
            holder.productName.text = productInfo[0]
            holder.productPrice.text = productInfo[1]
            holder.productCategory.text = productInfo[2]
        } else {
            // Handle invalid data or insufficient parts after splitting
            // Display a default value or handle it as needed
            holder.productName.text = "Invalid Data"
            holder.productPrice.text = "-"
            holder.productCategory.text = "-"
        }

        holder.itemView.setOnClickListener {
            onItemClick.invoke(position)
        }
    }


    override fun getItemCount(): Int {
        return filteredList.size
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.textProductName)
        val productPrice: TextView = itemView.findViewById(R.id.textProductPrice)
        val productCategory: TextView = itemView.findViewById(R.id.textProductCategory)
    }


}
