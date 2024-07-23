package com.facebook.firsttask.admin.ptm_management

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

class NetworkForPtmManagement(private val authToken: String,private val context: Context) {

    private val client = HttpClient(Android) {
        followRedirects = false
        expectSuccess = false
    }

    suspend fun getAllPtmDatesForLocation(): PtmResponse? {
        val response: HttpResponse =
            client.get("http://68.178.165.107:91/api/PTM/GetAllPtmsforlocation") {
                header("Authorization", "Bearer $authToken")
            }
        val responseBody = response.receive<String>()
        Log.d("ResponseForPTMCreation", responseBody)

        return try {
            val gson = Gson()
            gson.fromJson(responseBody, PtmResponse::class.java)
        } catch (e: Exception) {
            Log.e("NetworkForPtmManagement", "Error parsing response", e)
            null
        }
    }

    suspend fun updateLocationForTeacher(teacherId: Int, locationId: Int, ptmId: Int) {
        val updateRequest = mapOf(
            "ptmId" to ptmId,
            "teacherAId" to teacherId,
            "locationId" to locationId
        )

        try {
            val gson = Gson()
            val jsonBody = gson.toJson(updateRequest)

            val response: HttpResponse = withContext(Dispatchers.IO) {
                client.post("http://68.178.165.107:91/api/Teacher/UpdateLocationForTeacher") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $authToken")
                    body = jsonBody
                }
            }

            if (response.status == HttpStatusCode.OK) {
                val responseBody = response.receive<String>()
                Log.d("UpdateLocationResponse", responseBody)
                showToast("Successfully Updated Location")
            } else {
                val responseBody = response.receive<String>()
                Log.e("UpdateLocationError", "Failed to update location: ${response.status}")
                Log.e("UpdateLocationError", responseBody)
                showToast("Failed to update location: ${response.status.value}")
            }
        } catch (e: Exception) {
            Log.e("UpdateLocationException", "Exception while updating location", e)
            showToast("Error updating location: ${e.message}")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}