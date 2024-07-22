package com.facebook.firsttask.admin.ptm_management

data class PtmResponse(
    val status: Int,
    val data: List<PtmData>
)

data class PtmData(
    val ptmId: Int,
    val date: String,
    val duration: String,
    val startTime: String,
    val endTime: String,
    val wings: List<Wing>,
    val teacherAttributes: List<TeacherAttribute>
)

data class Wing(
    val wingId: Int,
    val wingName: String
)

data class TeacherAttribute(
    val teacherAttId: Int,
    val teacherName: String,
    val teacherEmail: String,
    val locationId: Int?,
    val locationName: String?,
    val className: String
)
