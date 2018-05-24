package wxm.KeepAccount.define

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import wxm.KeepAccount.utility.toShowString

import java.math.BigDecimal
import java.sql.Timestamp
import java.util.Locale

import wxm.androidutil.db.IDBRow

/**
 * pay record
 * Created by WangXM on 2016/5/3.
 */
@DatabaseTable(tableName = "tbPayNote")
class PayNoteItem : INote, IDBRow<Int>, Cloneable, IPublicClone {
    @DatabaseField(generatedId = true, columnName = "_id", dataType = DataType.INTEGER)
    override var id: Int = GlobalDef.INVALID_ID

    @DatabaseField(columnName = FIELD_USR, foreign = true, foreignColumnName = UsrItem.FIELD_ID, canBeNull = false)
    override var usr: UsrItem? = null

    @DatabaseField(columnName = FIELD_BUDGET, foreign = true, foreignColumnName = BudgetItem.FIELD_ID)
    override var budget: BudgetItem? = null

    @DatabaseField(columnName = "info", canBeNull = false, dataType = DataType.STRING)
    override var info: String? = null

    @DatabaseField(columnName = "note", dataType = DataType.STRING)
    override var note: String? = null

    @DatabaseField(columnName = "val", dataType = DataType.BIG_DECIMAL)
    override var amount: BigDecimal = BigDecimal.ZERO
        set(newAmount) {
            field = newAmount
            valToStr = field.toShowString()
        }

    @DatabaseField(columnName = "ts", dataType = DataType.TIME_STAMP)
    override var ts: Timestamp = Timestamp(0)
        set(tsVal) {
            field = tsVal
            tsToStr = field.toString()
        }

    override var valToStr: String = BigDecimal.ZERO.toShowString()
        private set(value) {
            field = value
        }

    override var tsToStr: String = Timestamp(0).toString()
        private set(value) {
            field = value
        }

    override val isPayNote: Boolean
        get() = true

    override val isIncomeNote: Boolean
        get() = false

    init {
        info = ""
    }

    override fun toPayNote(): PayNoteItem {
        return this
    }

    override fun toIncomeNote(): IncomeNoteItem? {
        return null
    }

    override fun toString(): String {
        return String.format(Locale.CHINA, "info : %s, amount : %f, timestamp : %s\nnote : %s", info, amount, ts.toString(), note)
    }

    override fun getID(): Int {
        return id
    }

    override fun setID(integer: Int) {
        id = integer
    }

    override fun clone(): Any {
        val obj = PayNoteItem()

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
        return obj
    }

    override fun publicClone(): Any {
        return clone()
    }

    companion object {
        const val FIELD_USR = "usr_id"
        const val FIELD_BUDGET = "budget_id"
    }
}

