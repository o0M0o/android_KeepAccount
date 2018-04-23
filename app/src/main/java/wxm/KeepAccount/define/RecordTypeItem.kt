package wxm.KeepAccount.define

import android.os.Parcel
import android.os.Parcelable

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

import wxm.androidutil.DBHelper.IDBRow

/**
 * record type
 * Created by WangXM on 2016/8/8.
 */
@DatabaseTable(tableName = "tbRecordType")
class RecordTypeItem :  IDBRow<Int> {
    @DatabaseField(generatedId = true, columnName = "id", dataType = DataType.INTEGER)
    var _id: Int = 0
    /**
     * 记录类型大类([.DEF_INCOME] or [.DEF_PAY])
     */
    @DatabaseField(columnName = "itemType", canBeNull = false, dataType = DataType.STRING)
    var itemType: String? = null
    /**
     * 记录分类
     */
    @DatabaseField(columnName = "type", canBeNull = false, dataType = DataType.STRING)
    var type: String? = null
    /**
     * 辅助注释信息
     */
    @DatabaseField(columnName = "note", dataType = DataType.STRING)
    var note: String? = null

    companion object {
        const val FIELD_ITEMTYPE = "itemType"

        const val DEF_INCOME = "income"
        const val DEF_PAY = "pay"
    }

    init {
        _id = -1
        itemType = ""
        type = ""
        note = ""
    }

    override fun getID(): Int {
        return _id
    }

    override fun setID(integer: Int) {
        _id = integer
    }
}
