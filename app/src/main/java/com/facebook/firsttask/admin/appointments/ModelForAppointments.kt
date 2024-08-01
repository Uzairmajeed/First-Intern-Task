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

data class TeacherAppointmentsResponse(
    val status: Int,
    val data: List<TeacherAppointmentData>,
    val success: Boolean,
    val isValidationError: Boolean,
    val totalCount: Int
)

data class TeacherAppointmentData(
    val ptmDate: String,
    val teacherName: String,
    val totalAppts: Int,
    val ptmId: Int,
    val teacherId: Int,
    val timeslots: List<TimeslotData>
)

data class TimeslotData(
    val timeslotId: Int,
    val startTime: String,
    val endTime: String,
    val ptmDate: String,
    val childName: String,
    val className: String,
    val location: String? // Location is optional
)

data class TeachersSwapResponse(
    val status: Int,
    val data: List<TeacherDataForSwap>,
    val success: Boolean,
    val isValidationError: Boolean,
    val messages: List<String>,
    val totalCount: Int
)

data class TeacherDataForSwap(
    val teacherId: Int,
    val teacherName: String
)

