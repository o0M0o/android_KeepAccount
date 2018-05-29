package wxm.KeepAccount.item

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
@DatabaseTable(tableName = "tbUsr")
class UsrItem : IDBRow<Int>, Cloneable, IPublicClone {
    @DatabaseField(generatedId = true, columnName = FIELD_ID, dataType = DataType.INTEGER)
    var id: Int = GlobalDef.INVALID_ID

    @DatabaseField(columnName = FIELD_NAME, unique = true, dataType = DataType.STRING)
    var name: String = ""

    @DatabaseField(columnName = FIELD_PWD, dataType = DataType.STRING)
    var pwd: String = ""

    @DatabaseField(columnName = FIELD_ICON_PATH, dataType = DataType.STRING)
    var iconPath: String = defaultUsrIcon()

    override fun getID(): Int {
        return id
    }

    override fun setID(integer: Int) {
        id = integer
    }

    override fun clone(): Any {
        return UsrItem().let{
            it.id = this.id
            it.name = this.name
            it.pwd = this.pwd
            it.iconPath = this.iconPath

            it
        }
    }

    override fun publicClone(): Any {
        return clone()
    }

    companion object {
        const val FIELD_ID = "_id"
        const val FIELD_NAME = "name"
        const val FIELD_PWD = "pwd"
        const val FIELD_ICON_PATH = "icon_path"
    }
}
