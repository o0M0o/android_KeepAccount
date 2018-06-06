package wxm.KeepAccount.item

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.define.IPublicClone
import wxm.KeepAccount.utility.toMoneyString
import wxm.androidutil.db.IDBRow

import java.math.BigDecimal
import java.sql.Timestamp
import java.util.*


/**
 * income record
 * Created by WangXM on 2016/5/3.
 */
@DatabaseTable(tableName = "tbIncomeNote")
class IncomeNoteItem : INote, IDBRow<Int>, Cloneable, IPublicClone {
    @DatabaseField(generatedId = true, columnName = "_id", dataType = DataType.INTEGER)
    override var id: Int = GlobalDef.INVALID_ID

    @DatabaseField(columnName = FIELD_USR, foreign = true, foreignColumnName = UsrItem.FIELD_ID, canBeNull = false)
    override var usr: UsrItem? = null

    //@DatabaseField(columnName = "budget_id", foreign = true, foreignColumnName = BudgetItem.FIELD_ID)
    override var budget: BudgetItem?
        set(value)  {
            throw AssertionError("NOT SUPPORT")
        }
        get() = null

    @DatabaseField(columnName = "info", canBeNull = false, dataType = DataType.STRING)
    override var info: String = ""

    @DatabaseField(columnName = "note", dataType = DataType.STRING)
    override var note: String? = null

    @DatabaseField(columnName = "val", dataType = DataType.BIG_DECIMAL)
    override var amount: BigDecimal = BigDecimal.ZERO
        set(newAmount) {
            field = newAmount
            valToStr = field.toMoneyString()
        }

    @DatabaseField(columnName = "ts", dataType = DataType.TIME_STAMP)
    override var ts: Timestamp = Timestamp(0)
        set(tsVal) {
            field = tsVal
            tsToStr = field.toString()
        }

    override var images: LinkedList<String> = LinkedList()
        set(value) {
            field.clear()
            field.addAll(value)
        }

    override var valToStr: String = ""
        get() {
            if(field.isEmpty())  field = amount.toMoneyString()
            return field
        }
        private set(value) {
            field = value
        }

    override var tsToStr: String = ""
        get() {
            if(field.isEmpty())  field = ts.toString()
            return field
        }
        private set(value) {
            field = value
        }

    override val isPayNote: Boolean
        get() = false

    override val isIncomeNote: Boolean
        get() = true

    override fun toPayNote(): PayNoteItem? {
        return null
    }

    override fun toIncomeNote(): IncomeNoteItem? {
        return this
    }


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
        val obj = IncomeNoteItem()

        obj.id = this.id
        this.usr?.let {
            obj.usr = it.publicClone() as UsrItem
        }

        this.budget?.let {
            obj.budget = it.publicClone() as BudgetItem
        }

        obj.amount = this.amount
        obj.info = this.info
        obj.note = this.note
        obj.ts = this.ts

        obj.valToStr = this.valToStr
        obj.tsToStr = this.tsToStr
        obj.images = this.images
        return obj
    }

    override fun publicClone(): Any {
        return clone()
    }

    companion object {
        const val FIELD_USR = "usr_id"
    }
}

