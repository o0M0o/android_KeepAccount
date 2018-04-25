package wxm.KeepAccount.ui.data.edit.base


import wxm.androidutil.FrgUtility.FrgSupportBaseAdv

/**
 * base for data edit
 * Created by WangXM on2016/9/27.
 */
abstract class TFEditBase : FrgSupportBaseAdv() {
    protected var mAction: String? = null

    /**
     * get current record
     * @return      current data or null
     */
    abstract val curData: Any?

    /**
     * set current record
     * @param action can be --
     * * “modify”(GlobalDef.STR_MODIFY)
     * * “create”(GlobalDef.STR_CREATE)
     * @param obj    if action is modify, it record
     */
    abstract fun setCurData(action: String, obj: Any?)


    /**
     * invoke when accept record
     * @return      true if success
     */
    abstract fun onAccept(): Boolean
}
