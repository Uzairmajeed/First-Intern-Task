package com.facebook.firsttask.admin.PTMCreation


import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse

class GetAllTeacherNames(private val authToken: String) {
    private val client = HttpClient(Android) {
        followRedirects = false
        expectSuccess = false
    }

    suspend fun getFromServer(wings: List<String>): String? {
        val wingsParam = wings.joinToString("&wings=") { it }
        val url = "http://68.178.165.107:91/api/Teacher/GetAllTeachersForPtm?wings=$wingsParam"

        val response: HttpResponse = client.get(url) {
            header("Authorization", "Bearer $authToken") // Add the auth token to the request
        }
        val responseBody = response.receive<String>()
        Log.d("ResponseTeachers", responseBody)
        return responseBody
    }
}
