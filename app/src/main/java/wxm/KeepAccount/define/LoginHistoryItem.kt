package wxm.KeepAccount.define

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import wxm.androidutil.db.IDBRow
import java.sql.Timestamp


/**
 * usr class
 * Created by WangXM on 2016/8/5.
 */
@DatabaseTable(tableName = "tbLoginHistory")
class LoginHistoryItem : IDBRow<Int>, Cloneable, IPublicClone {
    @DatabaseField(generatedId = true, columnName = FIELD_ID, dataType = DataType.INTEGER)
    var id: Int = GlobalDef.INVALID_ID

    @DatabaseField(columnName = FIELD_USR_ID, dataType = DataType.INTEGER)
    var usrId: Int = GlobalDef.INVALID_ID

    @DatabaseField(columnName = FIELD_LOGIN_TIME, dataType = DataType.TIME_STAMP)
    var loginTime: Timestamp = Timestamp(0)

    override fun setID(mk: Int?) {
        id = mk!!
    }

    override fun getID(): Int {
        return id
    }

    override fun clone(): Any {
        return LoginHistoryItem().let{
            it.id = this.id
            it.usrId = this.usrId
            it.loginTime = this.loginTime

            it
        }
    }

    override fun publicClone(): Any {
        return clone()
    }

    companion object {
        const val FIELD_ID = "_id"
        const val FIELD_USR_ID = "usr_id"
        const val FIELD_LOGIN_TIME = "login_time"
    }
}
