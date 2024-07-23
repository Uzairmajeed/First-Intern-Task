package com.facebook.firsttask.admin.dashboard.PTMCreation.nextpage

data class ClassData(val classId: Int, val className: String)
data class TeacherData(val teacherId: Int, val teacherName: String, val locationId: Int, val teacherLocation: String)


data class SelectedClassItem(
    val className: String,
    val teacherName: String,
    val location: String,
    val selectedTimes: List<String>
)

data class SelectedItemWithIds(
    val classId: Int,
    val teacherId: Int,
    val locationId: Int,
    val startTime: String,
    val endTime: String,
    val selectedTimes: List<String>
)

data class LunchSlot(
    val lunchStartTime: String?,
    val lunchEndTime: String?
)

data class TeacherAttribute(
    val teacher_Id: Int,
    val location_Id: Int,
    val class_Id: Int,
    val timeslotDtos: List<TimeslotDto>
)

data class TimeslotDto(
    val startTime: String,
    val endTime: String
)

data class Location(val locationId: Int, val locationName: String)






