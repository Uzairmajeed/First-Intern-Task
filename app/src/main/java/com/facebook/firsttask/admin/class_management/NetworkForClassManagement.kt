// NetworkForClassManagement.kt
package com.facebook.firsttask.admin.class_management

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NetworkForClassManagement(private val authToken: String, private val context: Context) {
    private val client = HttpClient(Android) {
        followRedirects = false
        expectSuccess = false
    }
    private val gson = Gson()  // Create a Gson instance

    suspend fun getAllWingNames(): List<WingData> {
        val response: HttpResponse = client.get("http://68.178.165.107:91/api/Course/GetSchoolWings") {
            header("Authorization", "Bearer $authToken") // Add the auth token to the request
        }
        val responseBody = response.receive<String>()

        Log.d("ResponseWingsNames", responseBody)

        return try {
            val wingResponse = gson.fromJson(responseBody, WingResponse::class.java)
            wingResponse.data // Assuming 'data' contains the list of wings
        } catch (e: Exception) {
            Log.e("NetworkForClassManagement", "Error parsing response", e)
            emptyList() // Return an empty list if parsing fails
        }
    }

    suspend fun getAllClassesForManagement(wingName: String): List<ClassData> {
        return try {
            // Construct the URL with the wingName parameter
            val url = "http://68.178.165.107:91/api/Location/GetClassesForManagement?wing=${wingName}"

            // Make the GET request
            val response: HttpResponse = client.get(url) {
                header("Authorization", "Bearer $authToken")
            }
            val responseBody = response.receive<String>()

            // Log the response body
            Log.d("ResponseClassesForManagement", responseBody)

            // Parse the response body
            return try {
                val classResponse = gson.fromJson(responseBody, ClassResponse::class.java)
                classResponse.data // Return the list of classes
            } catch (e: Exception) {
                Log.e("NetworkForClassManagement", "Error parsing response", e)
                emptyList() // Return an empty list if parsing fails
            }
        } catch (e: Exception) {
            Log.e("NetworkForClassManagement", "Error fetching classes for wing", e)
            emptyList() // Return an empty list if an error occurs
        }
    }

    suspend fun updateClassName(Id: Int, className: String, sectionName: String) {
        val updateRequest = mapOf(
            "id" to Id,
            "className" to className,
            "sectionName" to sectionName
        )

        try {
            val gson = Gson()
            val jsonBody = gson.toJson(updateRequest)

            val response: HttpResponse = withContext(Dispatchers.IO) {
                client.post("http://68.178.165.107:91/api/Location/UpdateClass") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $authToken")
                    body = jsonBody
                }
            }

            if (response.status == HttpStatusCode.OK) {
                val responseBody = response.receive<String>()
                Log.d("UpdatedResponse", responseBody)
                showToast("Successfully Updated ")
            } else {
                val responseBody = response.receive<String>()
                Log.e("UpdatedResponse", "Failed to Update : ${response.status}")
                Log.e("UpdatedResponse", responseBody)
                showToast("Failed to Update : ${response.status.value}")
            }
        } catch (e: Exception) {
            Log.e("UpdateException", "Exception while Updating ", e)
            showToast("Error updating teacher: ${e.message}")
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
