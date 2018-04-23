package wxm.KeepAccount.define

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

import java.math.BigDecimal
import java.sql.Timestamp

import wxm.androidutil.DBHelper.IDBRow

/**
 * remind
 * Created by WangXM on 2016/10/9.
 */
@DatabaseTable(tableName = "tbRemind")
class RemindItem : IDBRow<Int> {
    @DatabaseField(generatedId = true, columnName = "id", dataType = DataType.INTEGER)
    var id: Int = GlobalDef.INVALID_ID
        protected set

    @DatabaseField(columnName = "usr_id", foreign = true, foreignColumnName = UsrItem.FIELD_ID, canBeNull = false)
    var usr: UsrItem? = null

    @DatabaseField(columnName = "name", canBeNull = false, dataType = DataType.STRING)
    var name: String? = null

    @DatabaseField(columnName = "type", canBeNull = false, dataType = DataType.STRING)
    var type: String? = null

    @DatabaseField(columnName = "reason", canBeNull = false, dataType = DataType.STRING)
    var reason: String? = null

    @DatabaseField(columnName = "period", canBeNull = false, dataType = DataType.STRING)
    var period: String? = null

    @DatabaseField(columnName = "startDate", dataType = DataType.TIME_STAMP)
    var startDate: Timestamp? = null

    @DatabaseField(columnName = "endDate", dataType = DataType.TIME_STAMP)
    var endDate: Timestamp? = null

    @DatabaseField(columnName = "amount", dataType = DataType.BIG_DECIMAL)
    var amount: BigDecimal? = null

    override fun getID(): Int {
        return id
    }

    override fun setID(integer: Int) {
        id = integer
    }

    companion object {
        // for type
        const val REMIND_BUDGET = "预算预警"
        const val REMIND_PAY = "支出预警"
        const val REMIND_INCOME = "收入预警"

        // for reason
        const val RAT_AMOUNT_BELOW = "金额低于预设值预警"
        const val RAT_AMOUNT_EXCEED = "金额高于预设值预警"

        // for period
        const val PERIOD_WEEKLY = "每周"
        const val PERIOD_MONTHLY = "每月"
        const val PERIOD_YEARLY = "每年"

        const val FIELD_USR = "usr_id"
        const val FIELD_NAME = "name"
    }
}
