package wxm.KeepAccount.item

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.define.IPublicClone
import wxm.KeepAccount.improve.toMoneyStr
import wxm.androidutil.db.IDBRow

import java.math.BigDecimal
import java.sql.Timestamp
import java.util.*


/**
 * income record
 * Created by WangXM on 2016/5/3.
 */
@DatabaseTable(tableName = "tbIncomeNote")
class IncomeNoteItem(override var tag: Any? = null) :
        INote, IImage, IDBRow<Int>, Cloneable, IPublicClone {
    @DatabaseField(generatedId = true, columnName = INote.FILELD_ID, dataType = DataType.INTEGER)
    override var id: Int = GlobalDef.INVALID_ID

    @DatabaseField(columnName = INote.FIELD_USR, foreign = true, foreignColumnName = UsrItem.FIELD_ID, canBeNull = false)
    override var usr: UsrItem? = null

    @DatabaseField(columnName = INote.FIELD_INFO, canBeNull = false, dataType = DataType.STRING)
    override var info: String = ""

    @DatabaseField(columnName = INote.FIELD_NOTE, dataType = DataType.STRING)
    override var note: String? = null

    @DatabaseField(columnName = INote.FIELD_AMOUNT, dataType = DataType.BIG_DECIMAL)
    override var amount: BigDecimal = BigDecimal.ZERO

    @DatabaseField(columnName = INote.FIELD_TS, dataType = DataType.TIME_STAMP)
    override var ts: Timestamp = Timestamp(0)

    /// IImage START
    override val holderId
        get() = id

    override val holderType
        get() = noteType()

    override var images: LinkedList<NoteImageItem> = LinkedList()
        set(value) {
            field.clear()
            field.addAll(value)
        }
    /// IImage END

    override fun noteType(): String = GlobalDef.STR_RECORD_INCOME

    override fun toString(): String {
        return String.format(Locale.CHINA,
                "info : %s, amount : %f, timestamp : %s\nnote : %s",
                info, amount, ts.toString(), note)
    }

    override fun getID(): Int? {
        return id
    }

    override fun setID(integer: Int) {
        id = integer
    }

    override fun clone(): Any {
        val obj = IncomeNoteItem(tag)

        obj.id = this.id
        this.usr?.let {
            obj.usr = it.publicClone() as UsrItem
        }

        obj.amount = this.amount
        obj.info = this.info
        obj.note = this.note
        obj.ts = this.ts

        obj.images = this.images
        return obj
    }

    override fun publicClone(): Any {
        return clone()
    }
}

