package com.facebook.firsttask

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.firsttask.admin.AdminPage
import com.facebook.firsttask.databinding.ActivityMainBinding
import com.facebook.firsttask.parent.ParentPage
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val loginRepository = LoginRepository()
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        preferencesManager = PreferencesManager(this)

        // Check if the user is already logged in as an admin or parent
        if (preferencesManager.isLoggedInAsParent()) {
            navigateToParentDashBoard()
            finish() // Finish MainActivity to prevent going back to it on back press
        }
        if (preferencesManager.isLoggedInAsAdmin()) {
            navigateToAdminDashboard()
            finish() // Finish MainActivity to prevent going back to it on back press
        }

        binding.signInButton.setOnClickListener {
            val username = binding.usernameView.text.toString()
            val password = binding.passwordView.text.toString()
            login(username, password)
        }
    }

    // Calling LoginRepo Method For Fetching Data..
    // Based on roleName, we are navigating..
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
                    val token = dataObject.get("token").asString // Extract the token

                    withContext(Dispatchers.Main) {
                        preferencesManager.saveAuthToken(token) // Save the token in PreferencesManager

                        when (roleName) {
                            "Admin" -> {
                                preferencesManager.setLoggedInAsAdmin(true)
                                navigateToAdminDashboard()
                            }
                            "Parent" -> {
                                preferencesManager.setLoggedInAsParent(true)
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
