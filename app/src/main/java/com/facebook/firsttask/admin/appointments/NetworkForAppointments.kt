package com.facebook.firsttask.admin.appointments

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse

class NetworkForAppointments (private val authToken: String,private val context: Context) {

    private val client = HttpClient(Android) {
        followRedirects = false
        expectSuccess = false
    }

    private val gson = Gson()


    suspend fun getAllAppointments(): List<AppointmentData> {
        val response: HttpResponse =
            client.get("http://68.178.165.107:91/api/Appointment/GetAllAppointment") {
                header("Authorization", "Bearer $authToken")
            }
        val responseBody = response.receive<String>()

        Log.d("AllAppointmentsResponse", responseBody)

        return try {
            val gson = Gson()
            val appointmentResponse = gson.fromJson(responseBody, AppointmentsResponse::class.java)
            appointmentResponse.data // Assuming 'data' contains the list of appointments
        } catch (e: Exception) {
            Log.e("NetworkForAppointments", "Error parsing response", e)
            emptyList() // Return an empty list if parsing fails
        }
    }

}

