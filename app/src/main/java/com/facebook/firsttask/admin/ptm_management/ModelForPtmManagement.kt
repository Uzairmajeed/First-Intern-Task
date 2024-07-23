package com.facebook.firsttask.admin.ptm_management

import android.os.Parcel
import android.os.Parcelable

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
    val teacherName: String?,
    val teacherEmail: String?,
    val locationId: Int?,
    val locationName: String?,
    val className: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(teacherAttId)
        parcel.writeString(teacherName)
        parcel.writeString(teacherEmail)
        parcel.writeValue(locationId)
        parcel.writeString(locationName)
        parcel.writeString(className)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TeacherAttribute> {
        override fun createFromParcel(parcel: Parcel): TeacherAttribute {
            return TeacherAttribute(parcel)
        }

        override fun newArray(size: Int): Array<TeacherAttribute?> {
            return arrayOfNulls(size)
        }
    }
}
