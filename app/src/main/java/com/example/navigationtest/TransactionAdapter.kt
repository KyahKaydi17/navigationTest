package com.example.navigationtest
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(
    private var transactions: List<Transaction>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transactions, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        val price = transaction.subtotal // Assuming 'prices' holds the price
        holder.priceTextView.text = "Subtotal: ₱$price"

        holder.dateTextView.text = transaction.currentDate
        holder.timeTextView.text = transaction.currentTime
        holder.indexTextView.text = "Index: $position"
        holder.itemView.setOnClickListener {
            onItemClick.invoke(position) // Trigger the onItemClick listener when an item is clicked
        }
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    fun updateTransactions(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Transaction {
        return transactions[position]
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val subtotalTextView: TextView = itemView.findViewById(R.id.subtotalTextView)
         val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
         val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
         val indexTextView: TextView = itemView.findViewById(R.id.indexTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.subtotalTextView)

        fun bind(transaction: Transaction) {
            subtotalTextView.text = "Subtotal: ₱${transaction.subtotal}"
            dateTextView.text = "Date: ${transaction.currentDate}"
            timeTextView.text = "Time: ${transaction.currentTime}"
            indexTextView.text = "Index: ${adapterPosition + 1}"
        }
    }
}
