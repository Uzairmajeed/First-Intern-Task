package com.facebook.firsttask.admin.user_management

data class ParentResponse(
    val status: Int,
    val data: List<ParentData>,
    val success: Boolean,
    val isValidationError: Boolean,
    val totalCount: Int
)

data class ParentData(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val firstName2: String?,
    val lastName2: String?,
    val email2: String?,
    val childrens: List<ChildData>
)

data class ChildData(
    val firstName: String,
    val lastName: String,
    val childId: Int,
    val isActive: Boolean,
    val className: String,
    val childCount: Int,
    val groups: List<GroupData>?
)

data class GroupData(
    val groupId: Int,
    val groupName: String
)
