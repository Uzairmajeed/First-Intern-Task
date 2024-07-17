package com.facebook.firsttask.admin.PTMCreation.nextpage

data class ClassData(val classId: Int, val className: String)
data class TeacherData(val teacherId: Int, val teacherName: String, val locationId: Int, val teacherLocation: String)

data class SelectedItemWithIds(
    val classId: Int,
    val teacherId: Int,
    val locationId: Int,
    val selectedTimes: List<String>
)


