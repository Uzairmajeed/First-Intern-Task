package com.facebook.firsttask.admin.user_management

import android.content.Context
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse

class NetworkForUserManagement (private val authToken: String,private val context: Context) {

    private val client = HttpClient(Android) {
        followRedirects = false
        expectSuccess = false
    }

    private val gson = Gson()

    suspend fun getAllParents(): List<ParentData> {
        val response: HttpResponse = client.get("http://68.178.165.107:91/api/Parent/GetAllParents") {
            header("Authorization", "Bearer $authToken")
        }
        val responseBody = response.receive<String>()

        return try {
            val parentResponse = gson.fromJson(responseBody, ParentResponse::class.java)
            parentResponse.data
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // Return an empty list if parsing fails
        }
    }
}