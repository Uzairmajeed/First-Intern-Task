package com.facebook.firsttask.admin.class_management

data class WingResponse(
    val status: Int,
    val data: List<WingData>,
    val success: Boolean,
    val isValidationError: Boolean,
    val totalCount: Int
)

data class WingData(
    val wingId: Int,
    val wingName: String
)

data class ClassResponse(
    val status: Int,
    val data: List<ClassData>,
    val success: Boolean,
    val isValidationError: Boolean,
    val totalCount: Int
)

data class ClassData(
    val classId: Int,
    val className: String,
    val sectionName: String,
    val wingId: Int,
    val wing: String
)
