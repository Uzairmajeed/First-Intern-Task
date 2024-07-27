package com.facebook.firsttask.admin.appointments

data class AppointmentsResponse(
    val status: Int,
    val data: List<AppointmentData>,
    val success: Boolean,
    val isValidationError: Boolean,
    val totalCount: Int
)

data class AppointmentData(
    val id: Int,
    val meetingType: String,
    val ptmId: Int,
    val ptmDate: String,
    val startTime: String,
    val endTime: String,
    val duration: String,
    val status: String,
    val childName: String,
    val teacherName: String,
    val isActve: Boolean,
    val className: String,
    val wingName: String,
    val childId: Int
)