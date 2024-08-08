package com.facebook.firsttask.admin.user_management

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

    // Function to change the status of a child
    suspend fun changeStatus(childId: Int, newState: Boolean): Boolean {
        val response: HttpResponse = client.post("http://68.178.165.107:91/api/Parent/ActivateInActivateChild?childId=$childId") {
            header("Authorization", "Bearer $authToken")
            // Assuming the request body might not be needed for this endpoint
        }

        return if (response.status.value == 200) {
            Log.d("StatusResponse", response.toString())
            true
        } else {
            Log.d("StatusResponse", response.toString())
            false
        }
    }

    suspend fun updateChild(id: Int?, updatedFirstName: String?, updatedLastName: String?): Boolean {
        val editRequest = mapOf(
            "id" to id,
            "firstName" to updatedFirstName,
            "lastName" to updatedLastName
        )

        return try {
            val jsonBody = gson.toJson(editRequest)

            val response: HttpResponse = withContext(Dispatchers.IO) {
                client.post("http://68.178.165.107:91/api/Parent/UpdateChild") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $authToken")
                    body = jsonBody
                }
            }

            withContext(Dispatchers.Main) {
                if (response.status == HttpStatusCode.OK) {
                    val responseBody = response.receive<String>()
                    Log.d("EditChildResponse", responseBody)
                    showToast("Successfully Edited Child")
                    true
                } else {
                    val responseBody = response.receive<String>()
                    Log.e("EditChildResponse", "Failed to edit child: ${response.status}")
                    Log.e("EditChildResponse", responseBody)
                    showToast("Failed to Edit Child: ${response.status.value}")
                    false
                }
            }
        } catch (e: Exception) {
            Log.e("EditChildException", "Exception while editing child", e)
            withContext(Dispatchers.Main) {
                showToast("Error Editing Child: ${e.message}")
            }
            false
        }
    }


    suspend fun updateParent(
        parentId: Int?,
        fatherFirstName: String,
        fatherLastName: String,
        motherFirstName: String,
        motherLastName: String
    ):Boolean{
        val editParent = mapOf(
            "id" to parentId,
            "firstName1" to fatherFirstName,
            "lastName1" to fatherLastName,
            "firstName2" to motherFirstName,
            "lastName2" to motherLastName
        )

        return try {
            val jsonBody = gson.toJson(editParent)

            val response: HttpResponse = withContext(Dispatchers.IO) {
                client.post("http://68.178.165.107:91/api/Parent/UpdateParent") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $authToken")
                    body = jsonBody
                }
            }

            withContext(Dispatchers.Main) {
                if (response.status == HttpStatusCode.OK) {
                    val responseBody = response.receive<String>()
                    Log.d("EditParentResponse", responseBody)
                    showToast("Successfully Edited Parent")
                    true
                } else {
                    val responseBody = response.receive<String>()
                    Log.e("EditParentResponse", "Failed to edit parent: ${response.status}")
                    Log.e("EditParentResponse", responseBody)
                    showToast("Failed to Edit Parent: ${response.status.value}")
                    false
                }
            }
        } catch (e: Exception) {
            Log.e("EditParentException", "Exception while editing parent", e)
            withContext(Dispatchers.Main) {
                showToast("Error Editing parent: ${e.message}")
            }
            false
        }

    }

    suspend fun getAllWingNames(): List<String> {
        val response: HttpResponse = client.get("http://68.178.165.107:91/api/Course/GetSchoolWings") {
            header("Authorization", "Bearer $authToken")
        }

        val responseBody = response.receive<String>()

        return try {
            val wingResponse = gson.fromJson(responseBody, WingResponse::class.java)
            wingResponse.data.map { it.wingName } // Return list of wing names
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // Return an empty list if parsing fails
        }
    }

    suspend fun getAllGroups(): List<GroupDataAll> {
        val response: HttpResponse = client.get("http://68.178.165.107:91/api/Course/GetAllGroups") {
            header("Authorization", "Bearer $authToken")
        }
        val responseBody = response.receive<String>()

        return try {
            val groupResponse = gson.fromJson(responseBody, GroupResponse::class.java)
            groupResponse.data
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // Return an empty list if parsing fails
        }
    }


    suspend fun getAllClasses(wingName: String): List<ClassData> {
        val response: HttpResponse = client.get("http://68.178.165.107:91/api/Location/GetAllClasses?wings=$wingName") {
            header("Authorization", "Bearer $authToken")
        }
        val responseBody = response.receive<String>()

        return try {
            val classResponse = gson.fromJson(responseBody, ClassResponse::class.java)
            classResponse.data
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // Return an empty list if parsing fails
        }
    }

    suspend fun addChildrenToParent(
        parentId: Int?,
        firstName: String,
        lastName: String,
        email: String,
        wing: String,
        className: Int?,
        groups: List<Int>?
    ): Boolean {

        // Convert groups to the correct format
        val groupIds = groups ?: emptyList()

        // Create the request body as a list of ChildRequest
        val requestBody = listOf(
            ChildRequest(
                firstName = firstName,
                lastName = lastName,
                email = email,
                wing = wing,
                parentId = parentId ?: 0,
                classId = className ?: 0,
                groups = groupIds
            )
        )

        return try {
            // Serialize the request body to JSON
            val jsonBody = gson.toJson(requestBody)

            // Make the POST request
            val response: HttpResponse = withContext(Dispatchers.IO) {
                client.post("http://68.178.165.107:91/api/Parent/AddChildToParent?parentId=${parentId ?: 0}") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $authToken")
                    body = jsonBody
                }
            }

            // Handle the response
            if (response.status == HttpStatusCode.OK) {
                Log.d("AddChildResponse", response.receive<String>())
                showToast("Child added successfully")
                true
            } else {
                Log.e("AddChildResponse", "Failed to add child: ${response.status}")
                showToast("Failed to add child: ${response.status.value}")
                false
            }
        } catch (e: Exception) {
            Log.e("AddChildException", "Exception while adding child", e)
            showToast("Error adding child: ${e.message}")
            false
        }
    }







    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}