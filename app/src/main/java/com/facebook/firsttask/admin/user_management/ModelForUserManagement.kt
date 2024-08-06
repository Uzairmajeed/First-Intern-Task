package com.facebook.firsttask.admin.user_management

import android.os.Parcel
import android.os.Parcelable

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
    val firstName: String?,
    val lastName: String?,
    val childId: Int,
    val isActive: Boolean,
    val className: String?,
    val childCount: Int,
    val groups: List<GroupData>?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readInt(),
        TODO("groups")
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeInt(childId)
        parcel.writeByte(if (isActive) 1 else 0)
        parcel.writeString(className)
        parcel.writeInt(childCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChildData> {
        override fun createFromParcel(parcel: Parcel): ChildData {
            return ChildData(parcel)
        }

        override fun newArray(size: Int): Array<ChildData?> {
            return arrayOfNulls(size)
        }
    }
}


data class GroupData(
    val groupId: Int,
    val groupName: String
)
