package com.facebook.firsttask.admin.PTMCreation.nextpage


import android.content.Context
import android.util.Log
import android.widget.Toast
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import org.json.JSONException
import org.json.JSONObject

class GetAllTeacherNames(private val authToken: String,private val context: Context) {
    private val client = HttpClient(Android) {
        followRedirects = false
        expectSuccess = false
    }

    suspend fun getFromServer(wings: List<String>): String? {
        return try {
            val wingsParam = wings.joinToString("&wings=") { it }
            val url = "http://68.178.165.107:91/api/Teacher/GetAllTeachersForPtm?wings=$wingsParam"

            val response: HttpResponse = client.get(url) {
                header("Authorization", "Bearer $authToken")
            }

            if (response.status == HttpStatusCode.OK) {
                val responseBody = response.receive<String>()
                Log.d("ResponseTeachers", responseBody)
                responseBody
            } else {
                showToast("Failed to fetch data: ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e("GetFromServerError", "Error fetching data", e)
            showToast("Error fetching data: ${e.message}")
            null
        }
    }

    suspend fun getAllLocations(): List<String>? {
        return try {
            val url = "http://68.178.165.107:91/api/Teacher/GetAllLocations"

            val response: HttpResponse = client.get(url) {
                header("Authorization", "Bearer $authToken")
            }

            if (response.status == HttpStatusCode.OK) {
                val responseBody = response.receive<String>()
                Log.d("ResponseLocations", responseBody)
                parseLocations(responseBody)
            } else {
                showToast("Failed to fetch locations: ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e("GetLocationsError", "Error fetching locations", e)
            showToast("Error fetching locations: ${e.message}")
            null
        }
    }

    private fun parseLocations(response: String): List<String> {
        val locationList = mutableListOf<String>()
        try {
            val jsonObject = JSONObject(response)
            val dataArray = jsonObject.getJSONArray("data")

            for (i in 0 until dataArray.length()) {
                val locationObject = dataArray.getJSONObject(i)
                val locationName = locationObject.getString("locationName")
                locationList.add(locationName)
            }
        } catch (e: JSONException) {
            Log.e("ParseLocationsError", "Error parsing locations", e)
        }
        return locationList
    }



    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
