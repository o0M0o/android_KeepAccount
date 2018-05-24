package wxm.KeepAccount.define


import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import wxm.androidutil.db.IDBRow

/**
 * record type
 * Created by WangXM on 2016/8/8.
 */
@DatabaseTable(tableName = "tbRecordType")
class RecordTypeItem : IDBRow<Int> {
    @DatabaseField(generatedId = true, columnName = "_id", dataType = DataType.INTEGER)
    var _id: Int = -1

    /**
     * 记录类型大类([.DEF_INCOME] or [.DEF_PAY])
     */
    @DatabaseField(columnName = FIELD_ITEM_TYPE, canBeNull = false, dataType = DataType.STRING)
    var itemType: String = DEF_INCOME

    /**
     * 记录分类
     */
    @DatabaseField(columnName = "type", canBeNull = false, dataType = DataType.STRING)
    var type: String = ""

    /**
     * 辅助注释信息
     */
    @DatabaseField(columnName = "note", dataType = DataType.STRING)
    var note: String? = null

    companion object {
        const val FIELD_ITEM_TYPE = "itemType"

        const val DEF_INCOME = "income"
        const val DEF_PAY = "pay"
    }

    init {
        note = ""
    }

    override fun getID(): Int {
        return _id
    }

    override fun setID(integer: Int) {
        _id = integer
    }
}
