package com.facebook.firsttask

import android.util.Log
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginRepository {
    private val client = HttpClient()

    //Making Post Request For Login ..
    suspend fun login(userName: String, password: String): String? {
        return try {
            val gson = Gson()
            val loginData = LoginData(userName, password)
            val json = gson.toJson(loginData)

            // Log the JSON data that will be sent to the server
            Log.d("JSON_DATA", json)

            val response: HttpResponse = withContext(Dispatchers.IO) {
                client.post("http://68.178.165.107:91/api/Account/Login") {
                    contentType(ContentType.Application.Json) // Set content type as JSON
                    body = json // Set the JSON data as the request body directly
                }
            }

            val responseBody = response.readText()
            Log.d("RESPONSE_BODY", responseBody) // Log the response body from the server
            Log.d("HTTP_STATUS_CODE", "${response.status.value}")
            responseBody // Return the response body
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("LOGIN_ERROR", "Exception: ${e.message}")
            null
        }
    }

    data class LoginData(
        val userName: String,
        val password: String
    )
}
