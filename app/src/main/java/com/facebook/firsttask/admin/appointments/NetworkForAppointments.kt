package com.facebook.firsttask.admin.appointments

import android.content.Context
import android.net.Uri
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


    suspend fun getAllAppointmentsByParameters(status: String?, date: String?, childName: String?): List<AppointmentData> {
        // Build the URL with only non-null parameters
        val url = buildString {
            append("http://68.178.165.107:91/api/Appointment/GetAllAppointment")

            // Append query parameters if they are not null
            val params = mutableListOf<String>()
            status?.let { params.add("status=${Uri.encode(it)}") }
            date?.let { params.add("date=${Uri.encode(it)}") }
            childName?.let { params.add("childName=${Uri.encode(it.trim())}") }

            if (params.isNotEmpty()) {
                append("?")
                append(params.joinToString("&"))
            }
        }
        Log.d("RequestURL", "Request URL: $url")


        if (url.endsWith("?")) {
            // If URL ends with "?", it means no parameters were added
            Log.d("FilteredAppointmentsResponse", "No parameters provided")
            return emptyList() // Handle case where no parameters are provided
        }

        val response: HttpResponse = client.get(url) {
            header("Authorization", "Bearer $authToken")
        }
        val responseBody = response.receive<String>()

        Log.d("FilteredAppointmentsResponse", responseBody)

        return try {
            val appointmentResponse = gson.fromJson(responseBody, AppointmentsResponse::class.java)
            appointmentResponse.data
        } catch (e: Exception) {
            Log.e("NetworkForAppointments", "Error parsing response", e)
            emptyList()
        }
    }

    suspend fun getAllAppointmentsWithTeacherDetails(): List<TeacherAppointmentData> {
        val url = "http://68.178.165.107:91/api/Appointment/GetAppointmentsWithTeacherDetails"

        val response: HttpResponse = client.get(url) {
            header("Authorization", "Bearer $authToken")
        }
        val responseBody = response.receive<String>()

        Log.d("TeacherAppointmentsResponse", responseBody)

        return try {
            val teacherAppointmentsResponse = gson.fromJson(responseBody, TeacherAppointmentsResponse::class.java)
            teacherAppointmentsResponse.data
        } catch (e: Exception) {
            Log.e("NetworkForAppointments", "Error parsing response", e)
            emptyList()
        }
    }

    suspend fun getAllAppointmentsWithTeacherDetailsWithParameters( date: String?, teacherName: String?):List<TeacherAppointmentData>{

        // Build the URL with only non-null parameters
        val url = buildString {
            append("http://68.178.165.107:91/api/Appointment/GetAppointmentsWithTeacherDetails")

            // Append query parameters if they are not null
            val params = mutableListOf<String>()
            date?.let { params.add("date=${Uri.encode(it)}") }
            teacherName?.let { params.add("teacherName=${Uri.encode(it.trim())}") }

            if (params.isNotEmpty()) {
                append("?")
                append(params.joinToString("&"))
            }
        }

        if (url.endsWith("?")) {
            // If URL ends with "?", it means no parameters were added
            Log.d("AllTeacherDetails", "No parameters provided")
            return emptyList() // Handle case where no parameters are provided
        }

        val response: HttpResponse = client.get(url) {
            header("Authorization", "Bearer $authToken")
        }
        val responseBody = response.receive<String>()

        Log.d("AllTeacherDetails", responseBody)

        return try {
            val teacherAppointmentsResponse = gson.fromJson(responseBody, TeacherAppointmentsResponse::class.java)
            teacherAppointmentsResponse.data
        } catch (e: Exception) {
            Log.e("NetworkForAppointmentsOfTeachers", "Error parsing response", e)
            emptyList()
        }
    }


    suspend fun getAllTeachersForSwap(ptmid: Int, teacherId: Int): List<TeacherDataForSwap> {
        val url = "http://68.178.165.107:91/api/Teacher/GetAllTeachersForSwap?TeacherId=$teacherId&ptmId=$ptmid"

        val response: HttpResponse = client.get(url) {
            header("Authorization", "Bearer $authToken")
        }
        val responseBody = response.receive<String>()
        Log.d("TeachersForSwapResponse", responseBody)

        return try {
            val teachersSwapResponse = gson.fromJson(responseBody, TeachersSwapResponse::class.java)
            teachersSwapResponse.data // Return the list of TeacherDataForSwap
        } catch (e: Exception) {
            Log.e("NetworkForAppointments", "Error parsing response", e)
            emptyList() // Return an empty list if parsing fails
        }
    }


    suspend fun swapTeacher(oldTeacherId: Int, newTeacherId: Int, ptmId: Int) {
        val swapRequest = mapOf(
            "oldTeacherId" to oldTeacherId,
            "newTeacherId" to newTeacherId,
            "ptmId" to ptmId
        )

        try {
            val gson = Gson()
            val jsonBody = gson.toJson(swapRequest)

            val response: HttpResponse = withContext(Dispatchers.IO) {
                client.post("http://68.178.165.107:91/api/Appointment/SwapTeacher") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $authToken")
                    body = jsonBody
                }
            }

            if (response.status == HttpStatusCode.OK) {
                val responseBody = response.receive<String>()
                Log.d("SwapTeacherResponse", responseBody)
                showToast("Successfully swapped teacher")
            } else {
                val responseBody = response.receive<String>()
                Log.e("SwapTeacherError", "Failed to swap teacher: ${response.status}")
                Log.e("SwapTeacherError", responseBody)
                showToast("Failed to swap teacher: ${response.status.value}")
            }
        } catch (e: Exception) {
            Log.e("SwapTeacherException", "Exception while swapping teacher", e)
            showToast("Error swapping teacher: ${e.message}")
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}