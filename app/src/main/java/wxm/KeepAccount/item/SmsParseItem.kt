package wxm.KeepAccount.item

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.define.IPublicClone
import wxm.androidutil.db.IDBRow
import java.sql.Timestamp


/**
 * usr class
 * Created by WangXM on 2016/8/5.
 */
@DatabaseTable(tableName = "tbSmsParse")
class SmsParseItem : IDBRow<Int>, Cloneable, IPublicClone {
    @DatabaseField(generatedId = true, columnName = FIELD_ID, dataType = DataType.INTEGER)
    var id: Int = GlobalDef.INVALID_ID

    @DatabaseField(columnName = FIELD_SMS_ID, dataType = DataType.LONG)
    var smsId: Long = GlobalDef.INVALID_ID.toLong()

    @DatabaseField(columnName = FIELD_PARSE_STATUS, dataType = DataType.STRING)
    var parseStatus: String = FIELD_VAL_NONE


    override fun setID(mk: Int?) {
        id = mk!!
    }

    override fun getID(): Int {
        return id
    }

    override fun clone(): Any {
        return SmsParseItem().let{
            it.id = this.id
            it.smsId = this.smsId
            it.parseStatus = this.parseStatus

            it
        }
    }

    override fun publicClone(): Any {
        return clone()
    }

    companion object {
        const val FIELD_ID = "_id"
        const val FIELD_SMS_ID = "sms_id"
        const val FIELD_PARSE_STATUS = "parse_status"

        const val FIELD_VAL_NONE = "none"
        const val FIELD_VAL_REMOVE = "remove"
        const val FIELD_VAL_TO_PAY = "to pay"
        const val FIELD_VAL_TO_INCOME = "to income"
    }
}
