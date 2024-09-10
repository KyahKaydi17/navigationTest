// DatabaseHelper.kt
package com.example.navigationtest

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "LostDonkey.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "products"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_PRICE = "price"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_USERNAME = "Username"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_PRICE REAL,
                $COLUMN_CATEGORY TEXT,
                $COLUMN_USERNAME TEXT,
                 `index` INTEGER
            )
        """.trimIndent()
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertProduct(name: String, price: Double, category: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_PRICE, price)
            put(COLUMN_CATEGORY, category)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    @SuppressLint("Range")
    fun getAllProducts(): List<String> {
        val products = mutableListOf<String>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor: Cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                val price = cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE))
                val category = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY))
                products.add("$name / $price / $category")
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return products
    }


    @SuppressLint("Range")
    fun getProductsByCategory(category: String): List<String> {
        val products = mutableListOf<String>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_CATEGORY = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(category))
        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                val price = cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE))
                products.add("$name - $price")
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return products
    }

    fun updateProduct(oldName: String, newName: String, newPrice: Double) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, newName)
            put(COLUMN_PRICE, newPrice)
        }
        db.update(TABLE_NAME, values, "$COLUMN_NAME=?", arrayOf(oldName))
        db.close()
    }

    fun deleteProduct(name: String) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_NAME=?", arrayOf(name))
        db.close()
    }

    @SuppressLint("Range")
    fun getProductId(position: Int): Long {
        return readableDatabase.use {
            val query = "SELECT $COLUMN_ID FROM $TABLE_NAME LIMIT 1 OFFSET $position"
            it.rawQuery(query, null).use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
                } else {
                    -1
                }
            }
        }
    }
    fun clearAllProducts() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME")
        db.close()
    }
    @SuppressLint("Range")
    fun getProductDetails(productId: Long): Pair<String, Double> {
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_NAME, $COLUMN_PRICE FROM $TABLE_NAME WHERE $COLUMN_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(productId.toString()))
        var productName = ""
        var productPrice = 0.0
        if (cursor.moveToFirst()) {
            productName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
            productPrice = cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE))
        }
        cursor.close()
        db.close()
        return Pair(productName, productPrice)
    }


    @SuppressLint("Range")
    fun getAllCategories(): List<String> {
        val categories = mutableListOf<String>()
        val db = this.readableDatabase
        val selectQuery = "SELECT DISTINCT category FROM $TABLE_NAME"

        val cursor = db.rawQuery(selectQuery, null)
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val category = it.getString(it.getColumnIndex(COLUMN_CATEGORY))
                    categories.add(category)
                } while (it.moveToNext())
            }
        }
        cursor.close()
        return categories
    }

    fun insertCategory(category: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CATEGORY, category)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }


    @SuppressLint("Range")
    fun getUsername(): String {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor: Cursor = db.rawQuery(query, null)

        var username = ""
        if (cursor.moveToFirst()) {
            username = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME))
        }

        cursor.close()
        return username
    }

    fun insertUsername(username: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_USERNAME, username)
        db.insert(TABLE_NAME, null, contentValues)
    }

}
