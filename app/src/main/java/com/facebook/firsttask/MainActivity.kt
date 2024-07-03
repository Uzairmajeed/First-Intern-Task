package com.facebook.firsttask

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.firsttask.admin.AdminPage
import com.facebook.firsttask.databinding.ActivityMainBinding
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val loginRepository = LoginRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Check if the user is already logged in as an admin
        val sharedPreferences = getSharedPreferences("login_pref", Context.MODE_PRIVATE)
        val isLoggedInAsAdmin = sharedPreferences.getBoolean("isLoggedInAsAdmin", false)
        val isLoggedInAsParent = sharedPreferences.getBoolean("isLoggedInAsParent", false)

        if (isLoggedInAsParent) {
            navigateToParentDashBoard()
            finish() // Finish MainActivity to prevent going back to it on back press
        }
        if (isLoggedInAsAdmin) {
            navigateToAdminDashboard()
            finish() // Finish MainActivity to prevent going back to it on back press
        }


        binding.signInButton.setOnClickListener {
            val username = binding.usernameView.text.toString()
            val password = binding.passwordView.text.toString()
            login(username, password)
        }
    }

    //Calling LoginRepo Method For Fetching Data..
    //Based On rollName We are Navigating..
    private fun login(username: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = loginRepository.login(username, password)
            if (response == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            } else {
                try {
                    val jsonObject = JsonParser.parseString(response).asJsonObject
                    val dataObject = jsonObject.getAsJsonObject("data")
                    val roleName = dataObject.get("roleName").asString
                    withContext(Dispatchers.Main) {
                        when (roleName) {
                            "Admin" -> {
                                // Save login state
                                val sharedPreferences = getSharedPreferences("login_pref", Context.MODE_PRIVATE)
                                with(sharedPreferences.edit()) {
                                    putBoolean("isLoggedInAsAdmin", true)
                                    apply()
                                }
                                navigateToAdminDashboard()
                            }
                            "Parent" -> {
                                // Save login state
                                val sharedPreferences = getSharedPreferences("login_pref", Context.MODE_PRIVATE)
                                with(sharedPreferences.edit()) {
                                    putBoolean("isLoggedInAsParent", true)
                                    apply()
                                }
                                navigateToParentDashBoard()
                            }
                            else -> {
                                Log.d("MainActivity", "Unknown role: $roleName")
                                // Show error or handle unknown roles
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("MainActivity", "Error parsing response: ${e.message}")
                }
            }
        }
    }


    private fun navigateToAdminDashboard() {
        val intent = Intent(this, AdminPage::class.java)
        startActivity(intent)
    }

    private fun navigateToParentDashBoard() {
        val intent = Intent(this, ParentPage::class.java)
        startActivity(intent)
    }
}
