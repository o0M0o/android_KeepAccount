package wxm.KeepAccount.ui.sync

import android.os.Parcel
import android.os.Parcelable
import java.sql.Timestamp

/**
 * @author      WangXM
 * @version     create：2018/6/22
 */
data class SmsItem(val id:Long, val date: Timestamp,
                   val address:String, val body:String, val type:String)
    : Parcelable
{
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            Timestamp(parcel.readLong()),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeLong(date.time)
        parcel.writeString(address)
        parcel.writeString(body)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SmsItem> {
        override fun createFromParcel(parcel: Parcel): SmsItem {
            return SmsItem(parcel)
        }

        override fun newArray(size: Int): Array<SmsItem?> {
            return arrayOfNulls(size)
        }
    }
}
