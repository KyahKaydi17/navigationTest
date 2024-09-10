package com.example.navigationtest

import android.app.Dialog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class MainActivity : AppCompatActivity() {

    private var username: String? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navView: NavigationView
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val currentUser = FirebaseAuth.getInstance().currentUser

        //Nav_Header_Main design
       username = intent.getStringExtra("USERNAME")

        val headerView = navView.getHeaderView(0)
        val welcomeTextView: TextView = headerView.findViewById(R.id.textViewWelcome)
        welcomeTextView.text = "Welcome $username!!"

        if (currentUser != null) {
            // Assuming you have stored the username in the user's profile or database
            val username = currentUser.displayName
            if (username.isNullOrEmpty()) {
                // Prompt the user to set their username

            }
        }
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.sales-> replaceFragment(HomeFragment(), username)
                R.id.transactions -> replaceFragment(TransactionsFragment(),username)
                R.id.Inventory -> replaceFragment(InventoryFragment(), username)
                R.id.menu_logout -> {
                    // Perform logout action here
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, LogInActivity::class.java))
                    finish() // Close MainActivity after logout
                    true
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        // Load the default fragment
        replaceFragment(HomeFragment(),username)
    }

    private fun replaceFragment(fragment: Fragment, username: String?) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val args = Bundle()
        args.putString("USERNAME", username)
        fragment.arguments = args
        fragmentTransaction.replace(R.id.content_frame, fragment)
        fragmentTransaction.commit()

    }







}
