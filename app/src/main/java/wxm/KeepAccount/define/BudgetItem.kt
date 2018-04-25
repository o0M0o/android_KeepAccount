package wxm.KeepAccount.define

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import wxm.androidutil.DBHelper.IDBRow
import java.math.BigDecimal
import java.sql.Timestamp

/**
 * budget
 * Created by WangXM on 2016/9/1.
 */
@DatabaseTable(tableName = "tbBudget")
class BudgetItem : IDBRow<Int> {
    @DatabaseField(generatedId = true, columnName = FIELD_ID, dataType = DataType.INTEGER)
    var _id: Int = GlobalDef.INVALID_ID
    @DatabaseField(columnName = FIELD_NAME, canBeNull = false, dataType = DataType.STRING)
    var name: String = ""
    @DatabaseField(columnName = FIELD_USR, foreign = true, foreignColumnName = UsrItem.FIELD_ID, canBeNull = false)
    var usr: UsrItem? = null
    @DatabaseField(columnName = "amount", dataType = DataType.BIG_DECIMAL, canBeNull = false)
    var amount: BigDecimal = BigDecimal.ZERO
        set(value) {
            remainderAmount = value
            field = value
        }
    @DatabaseField(columnName = "remainder_amount", dataType = DataType.BIG_DECIMAL, canBeNull = false)
    var remainderAmount: BigDecimal = BigDecimal.ZERO
        private set
    @DatabaseField(columnName = "note", dataType = DataType.STRING)
    var note: String? = null
    @DatabaseField(columnName = "ts", dataType = DataType.TIME_STAMP)
    var ts: Timestamp = Timestamp(System.currentTimeMillis())

    /**
     * use budget
     * @param use_val   total used budget amount
     */
    fun useBudget(use_val: BigDecimal) {
        remainderAmount = this.amount.subtract(use_val)
    }

    override fun hashCode(): Int {
        return name.hashCode() + amount.hashCode() + _id
    }

    override fun getID(): Int? {
        return _id
    }

    override fun setID(integer: Int) {
        _id = integer
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BudgetItem

        if (_id != other._id) return false
        if (name != other.name) return false
        if (usr != other.usr) return false
        if (amount != other.amount) return false
        if (remainderAmount != other.remainderAmount) return false
        if (note != other.note) return false
        if (ts != other.ts) return false

        return true
    }

    companion object {
        const val FIELD_USR = "usr_id"
        const val FIELD_NAME = "name"
        const val FIELD_ID = "id"
    }
}
