package com.facebook.firsttask.admin.ptm_management

import android.util.Log
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse

class NetworkForPtmManagement(private val authToken: String) {

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
}