package wxm.KeepAccount.item

import android.os.Parcel
import android.os.Parcelable
import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.define.IPublicClone
import wxm.androidutil.db.IDBRow
import java.math.BigDecimal
import java.sql.Timestamp
import java.util.*

/**
 * @author      WangXM
 * @version     create：2018/7/2
 */
@DatabaseTable(tableName = "tbDebtAction")
class DebtActionItem() :
        IImage, IDBRow<Int>, Cloneable, IPublicClone, Parcelable {
    @DatabaseField(generatedId = true, columnName = FIELD_ID, dataType = DataType.INTEGER)
    var id = GlobalDef.INVALID_ID

    @DatabaseField(columnName = FIELD_NOTE, dataType = DataType.STRING)
    var note = ""

    @DatabaseField(columnName = FIELD_AMOUNT, dataType = DataType.BIG_DECIMAL)
    var amount : BigDecimal = BigDecimal.ZERO

    @DatabaseField(columnName = FIELD_DEBT_ID, dataType = DataType.INTEGER)
    var debtId  = GlobalDef.INVALID_ID

    @DatabaseField(columnName = FIELD_TS, dataType = DataType.TIME_STAMP)
    var ts = Timestamp(0)

    /// IImage START
    override val holderId
        get() = id

    override val holderType
        get() = GlobalDef.STR_RECORD_DEBT_ACTION

    override var images: LinkedList<NoteImageItem> = LinkedList()
        set(value) {
            field.clear()
            field.addAll(value)
        }
    /// IImage END

    var tag: Any? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        note = parcel.readString()
        debtId = parcel.readInt()

        ts = Timestamp(parcel.readLong())
        parcel.readTypedList(images, NoteImageItem.CREATOR)
    }

    override fun setID(mk: Int) {
        id = mk
    }

    override fun getID(): Int = id

    override fun clone(): Any {
        val obj = DebtActionItem()
        obj.id = this.id
        obj.debtId = this.debtId

        obj.amount = this.amount
        obj.note = this.note
        obj.ts = this.ts
        obj.images = this.images
        obj.tag = this.tag
        return obj
    }

    override fun publicClone(): Any = clone()

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(note)
        parcel.writeInt(debtId)

        parcel.writeLong(ts.time)
        parcel.writeTypedList(images)
    }

    override fun describeContents(): Int {
        return 0
    }


    companion object {
        const val FIELD_ID = "_id"
        const val FIELD_DEBT_ID = "debt_id"
        const val FIELD_NOTE = "info"
        const val FIELD_AMOUNT = "amount"
        const val FIELD_TS = "ts"

        @JvmField val CREATOR = object : Parcelable.Creator<DebtActionItem> {
            override fun createFromParcel(parcel: Parcel): DebtActionItem {
                return DebtActionItem(parcel)
            }

            override fun newArray(size: Int): Array<DebtActionItem?> {
                return arrayOfNulls(size)
            }
        }
    }
}