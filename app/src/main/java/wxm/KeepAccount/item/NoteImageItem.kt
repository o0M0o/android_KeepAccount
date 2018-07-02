package wxm.KeepAccount.item

import android.os.Parcel
import android.os.Parcelable
import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.define.IPublicClone
import wxm.KeepAccount.utility.defaultUsrIcon
import wxm.androidutil.db.IDBRow


/**
 * usr class
 * Created by WangXM on 2016/8/5.
 */
@DatabaseTable(tableName = "tbNoteImage")
class NoteImageItem()
    : IDBRow<Int>, Cloneable, IPublicClone, Parcelable {
    @DatabaseField(generatedId = true, columnName = FIELD_ID, dataType = DataType.INTEGER)
    var id: Int = GlobalDef.INVALID_ID

    @DatabaseField(columnName = FIELD_IMAGE_TYPE, dataType = DataType.STRING)
    var imageType: String = ""

    @DatabaseField(columnName = FIELD_FOREIGN_ID, dataType = DataType.INTEGER)
    var foreignID: Int = GlobalDef.INVALID_ID

    @DatabaseField(columnName = FIELD_IMAGE_PATH, dataType = DataType.STRING)
    var imagePath: String = ""

    @DatabaseField(columnName = FIELD_STATUS, dataType = DataType.INTEGER)
    var status: Int = STATUS_USE

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        imageType = parcel.readString()
        foreignID = parcel.readInt()
        imagePath = parcel.readString()
        status = parcel.readInt()
    }

    override fun getID(): Int {
        return id
    }

    override fun setID(integer: Int) {
        id = integer
    }

    override fun clone(): Any {
        return NoteImageItem().let{
            it.id = this.id
            it.foreignID = this.foreignID
            it.imageType = this.imageType
            it.imagePath = this.imagePath
            it.status = this.status

            it
        }
    }

    override fun publicClone(): Any {
        return clone()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(imageType)
        parcel.writeInt(foreignID)
        parcel.writeString(imagePath)
        parcel.writeInt(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        const val FIELD_ID = "_id"
        const val FIELD_FOREIGN_ID = "foreignID"
        const val FIELD_IMAGE_TYPE = "imageType"
        const val FIELD_IMAGE_PATH = "imagePath"
        const val FIELD_STATUS = "status"

        const val STATUS_USE = 1
        const val STATUS_NOT_USE = 0

        @JvmField val CREATOR = object : Parcelable.Creator<NoteImageItem> {
            override fun createFromParcel(parcel: Parcel): NoteImageItem {
                return NoteImageItem(parcel)
            }

            override fun newArray(size: Int): Array<NoteImageItem?> {
                return arrayOfNulls(size)
            }
        }
    }
}
