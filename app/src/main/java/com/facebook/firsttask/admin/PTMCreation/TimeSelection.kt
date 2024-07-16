package com.facebook.firsttask.admin.PTMCreation

import android.os.Parcel
import android.os.Parcelable

data class TimeSelection(
    var startTime: String,
    var endTime: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(startTime)
        parcel.writeString(endTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TimeSelection> {
        override fun createFromParcel(parcel: Parcel): TimeSelection {
            return TimeSelection(parcel)
        }

        override fun newArray(size: Int): Array<TimeSelection?> {
            return arrayOfNulls(size)
        }
    }
}



