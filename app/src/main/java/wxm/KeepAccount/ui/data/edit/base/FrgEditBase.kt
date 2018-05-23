package wxm.KeepAccount.ui.data.edit.base

import wxm.androidutil.ui.frg.FrgSupportBaseAdv

/**
 * @author      WangXM
 * @version     create：2018/4/25
 */
abstract class FrgEditBase : FrgSupportBaseAdv() {
    /**
     * check whether in 'edit' status
     * @return  true if in 'edit' status
     */
    abstract fun isEditStatus(): Boolean

    /**
     * check whether in 'preview' status
     * @return  true if in 'preview' status
     */
    abstract fun isPreviewStatus(): Boolean

    /**
     * set current record
     * @param type   can be --
     * * “pay”(GlobalDef.STR_RECORD_PAY)
     * * “income”(GlobalDef.STR_RECORD_INCOME)
     * * “budget”(GlobalDef.STR_RECORD_BUDGET)
     * @param obj       if action is 'modify' then is record
     */
    abstract fun setCurData(type: String, obj: Any?)

    abstract fun getCurData(): Any

    /**
     * invoke when accept record
     * @return  true if success
     */
    abstract fun onAccept(): Boolean


    /**
     * switch to preview status
     */
    abstract fun toPreviewStatus()


    /**
     * switch to edit status
     */
    abstract fun toEditStatus()
}