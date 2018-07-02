package wxm.KeepAccount.item

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
@DatabaseTable(tableName = "tbDebtNote")
class DebtNoteItem(override var tag: Any? = null) :
        INote, IImage, IDBRow<Int>, Cloneable, IPublicClone {
    @DatabaseField(generatedId = true, columnName = FIELD_ID, dataType = DataType.INTEGER)
    override var id = GlobalDef.INVALID_ID

    @DatabaseField(columnName = "info", canBeNull = false, dataType = DataType.STRING)
    override var info: String = FIELD_VAL_LEND

    @DatabaseField(columnName = FIELD_NOTE, dataType = DataType.STRING)
    override var note: String? = null

    @DatabaseField(columnName = FIELD_AMOUNT, dataType = DataType.BIG_DECIMAL)
    override var amount : BigDecimal = BigDecimal.ZERO

    @DatabaseField(columnName = FIELD_USR, foreign = true, foreignColumnName = UsrItem.FIELD_ID, canBeNull = false)
    override var usr: UsrItem? = null

    @DatabaseField(columnName = FIELD_TS, dataType = DataType.TIME_STAMP)
    override var ts = Timestamp(0)

    val remainAmount: BigDecimal
        get() {
            var ret = amount
            actions.forEach {
                ret = ret.subtract(it.amount)
            }

            return ret
        }

    var actions: LinkedList<DebtActionItem> = LinkedList()
        set(value) {
            field.clear()
            field.addAll(value)
        }

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

    override fun noteType(): String = GlobalDef.STR_RECORD_DEBT

    override fun setID(mk: Int) {
        id = mk
    }

    override fun getID(): Int = id

    override fun clone(): Any {
        val obj = DebtNoteItem()
        obj.id = this.id
        this.usr?.let {
            obj.usr = it.publicClone() as UsrItem
        }

        obj.amount = this.amount
        obj.info = this.info
        obj.note = this.note
        obj.ts = this.ts
        obj.images = this.images
        obj.tag = this.tag
        return obj
    }

    override fun publicClone(): Any = clone()

    companion object {
        const val FIELD_ID = "_id"
        const val FIELD_USR = "usr_id"
        const val FIELD_NOTE = "note"
        const val FIELD_AMOUNT = "amount"
        const val FIELD_TS = "ts"

        const val FIELD_VAL_BORROW = "borrow"
        const val FIELD_VAL_LEND = "lend"
    }
}