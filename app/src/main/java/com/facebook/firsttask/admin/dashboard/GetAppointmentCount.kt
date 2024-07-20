package com.facebook.firsttask.admin.dashboard

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import org.json.JSONObject

class GetAppointmentCount(private val authToken: String) {

    private val client = HttpClient(Android) {
        followRedirects = false
        expectSuccess = false
    }

    suspend fun getCountAndStatusForDates(dates: List<String>): List<Pair<Int, String>> {
        val resultList = mutableListOf<Pair<Int, String>>()

        for (date in dates) {
            var totalCount = 0
            var unmarkedStatus = ""

            try {
                val response: HttpResponse = client.get("http://68.178.165.107:91/api/Timeslot/GetAllByPtmDate?date=$date") {
                    header("Authorization", "Bearer $authToken") // Add the auth token to the request
                }
                val responseBody = response.receive<String>()
                Log.d("ResponseForCount", responseBody)

                // Parse JSON response
                val jsonObject = JSONObject(responseBody)
                val dataArray = jsonObject.getJSONArray("data")

                // Count the number of objects in the data array
                totalCount = dataArray.length()

                // Get the "status" value from the first object if available
                if (dataArray.length() > 0) {
                    val firstObject = dataArray.getJSONObject(0)
                    unmarkedStatus = firstObject.optString("status", "")
                }
            } catch (e: Exception) {
                Log.e("GetAppointmentCount", "Error fetching data for date: $date", e)
            }

            resultList.add(Pair(totalCount, unmarkedStatus))
        }

        return resultList
    }
}
