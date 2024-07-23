package com.facebook.firsttask.admin.dashboard.PTMCreation.nextpage


import android.content.Context
import android.util.Log
import android.widget.Toast
import com.facebook.firsttask.admin.dashboard.PTMCreation.TimeSelection
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class NetworkOperations(private val authToken: String, private val context: Context) {
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

    suspend fun getAllLocations(): List<Location>? {
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

    private fun parseLocations(response: String): List<Location> {
        val locationList = mutableListOf<Location>()
        try {
            val jsonObject = JSONObject(response)
            val dataArray = jsonObject.getJSONArray("data")

            for (i in 0 until dataArray.length()) {
                val locationObject = dataArray.getJSONObject(i)
                val locationId = locationObject.getInt("location_Id")
                val locationName = locationObject.getString("locationName")
                locationList.add(Location(locationId, locationName))
            }
        } catch (e: JSONException) {
            Log.e("ParseLocationsError", "Error parsing locations", e)
        }
        return locationList
    }



    suspend fun createPTM(
        selectedItemsWithIds: List<SelectedItemWithIds>,
        selectedWingIds: List<String>?,
        selectedDuration: String?,
        selectedStartTime: String?,
        selectedEndTime: String?,
        ptmDate: String?,
        isOnlineChecked: Boolean?,
        isOfflineChecked: Boolean?,
        timeSelections: List<TimeSelection>?,
        onSuccess: () -> Unit
    ) {
        // Convert selectedWingNames to integers
        val wings = selectedWingIds

        // Determine the meeting type
        val meetingType = when {
            isOnlineChecked == true -> 1
            isOfflineChecked == true -> 2
            else -> 0
        }

        // Create lunch slots list
        val lunchSlots = timeSelections?.map {
            LunchSlot(it.startTime, it.endTime)
        } ?: emptyList()

        // Create teacher attributes list
        val teacherAttributes = selectedItemsWithIds.map { selectedItem ->
            val timeslotDtos = selectedItem.selectedTimes.map { timeRange ->
                val times = timeRange.split(" - ")
                val startTime = times.getOrNull(0) ?: ""
                val endTime = times.getOrNull(1) ?: ""

                TimeslotDto(formatTime(startTime), formatTime(endTime))
            }

            // Log timeslotDtos for debugging purposes
            Log.d("TimeslotDtos", timeslotDtos.toString())

            TeacherAttribute(
                teacher_Id = selectedItem.teacherId,
                location_Id = selectedItem.locationId,
                class_Id = selectedItem.classId,
                timeslotDtos = timeslotDtos
            )
        }

        // Format ptmDate to ISO8601 format if needed
        val formattedPtmDate = ptmDate?.let {
            try {
                // Determine the format of ptmDate and parse accordingly
                Log.d("FormattedPtmDate", "Formatted PTM Date before Change: $ptmDate")

                val parsedDate = when {
                    it.matches("\\d{4}-\\d{2}-\\d{2}".toRegex()) -> {
                        // If already in ISO8601 format, directly parse
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                            timeZone = TimeZone.getTimeZone("UTC")
                        }.parse(it)
                    }
                    it.matches("\\d{1,2}/\\d{1,2}/\\d{4}".toRegex()) -> {
                        // Attempt to parse assuming day/month/year format
                        SimpleDateFormat("d/M/yyyy", Locale.getDefault()).apply {
                            timeZone = TimeZone.getTimeZone("UTC")
                        }.parse(it)
                    }
                    else -> throw ParseException("Unknown date format", 0)
                }

                // Format parsedDate to ISO8601 format
                val isoFormattedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }.format(parsedDate)

                // Log the parsed and formatted date for verification
                Log.d("FormattedPtmDate", "Formatted PTM Date After Change: $isoFormattedDate")

                isoFormattedDate
            } catch (e: Exception) {
                Log.e("DateFormatError", "Error formatting ptmDate", e)
                null
            }
        }
        // Create PTM request object
        val ptmRequest = mapOf(
            "date" to formattedPtmDate,
            "timeslotDuration" to selectedDuration,
            "ptmStartTime" to selectedStartTime,
            "ptmEndTime" to selectedEndTime,
            "meetingType" to meetingType,
            "wings" to wings,
            "lunchSlots" to lunchSlots,
            "teacherAttributes" to teacherAttributes
        )

        try {
            val gson = Gson()
            val jsonBody = gson.toJson(ptmRequest)

            val response: HttpResponse = withContext(Dispatchers.IO) {
                client.post("http://68.178.165.107:91/api/PTM/BookPTM") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $authToken")
                    body = jsonBody
                }
            }

            if (response.status == HttpStatusCode.OK) {
                val responseBody = response.receive<String>()
                Log.d("CreatePTMResponse", responseBody)
                showToast("Successfully Created ")
                onSuccess() // Trigger the success callback


            } else {
                val responseBody = response.receive<String>()
                Log.e("CreatePTMError", "Failed to create PTM: ${response.status}")
                Log.e("CreatePTMError", responseBody)
                showToast("Failed to create PTM: ${response.status.value}")
            }
        } catch (e: Exception) {
            Log.e("CreatePTMException", "Exception while creating PTM", e)
            showToast("Error creating PTM: ${e.message}")
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    // Function to format time into "hh:mm a" format (e.g., 07:00 AM)
    private fun formatTime(time: String): String {
        // Split the time string by " - " to handle ranges
        val parts = time.split(" - ")
        val startTime = parts.firstOrNull() ?: time // Use the first part or fallback to original time

        // Format the startTime to "hh:mm a" format
        val sdfInput = SimpleDateFormat("HH:mm", Locale.getDefault())
        val sdfOutput = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedTime = try {
            val date = sdfInput.parse(startTime)
            sdfOutput.format(date)
        } catch (e: ParseException) {
            startTime // Return original time if parsing fails
        }

        // Ensure AM/PM format and return
        return formattedTime.replace("am", "AM").replace("pm", "PM")
    }


}


