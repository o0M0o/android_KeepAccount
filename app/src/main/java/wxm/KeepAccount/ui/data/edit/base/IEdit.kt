package wxm.KeepAccount.ui.data.edit.base

/**
 * @author      WangXM
 * @version     create：2018/4/25
 */
interface IEdit {
    /**
     * set preview data
     * @param data      for preview
     */
    fun setEditData(data: Any)

    /**
     * invoke when accept record
     * @return  true if success
     */
    fun onAccept(): Boolean

    /**
     * update swap data
     */
    fun refillData()

    /**
     * get edit data if need
     */
    fun getEditData(): Any? {
        return null
    }
}