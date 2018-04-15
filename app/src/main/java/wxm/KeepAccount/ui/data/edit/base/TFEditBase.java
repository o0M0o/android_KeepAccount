package wxm.KeepAccount.ui.data.edit.base;


import wxm.androidutil.FrgUtility.FrgSupportBaseAdv;

/**
 * base for data edit
 * Created by WangXM on2016/9/27.
 */
public abstract class TFEditBase extends FrgSupportBaseAdv {
    protected String mAction;

    /**
     * set current record
     * @param action can be --
     *               * “modify”(GlobalDef.STR_MODIFY)
     *               * “create”(GlobalDef.STR_CREATE)
     * @param obj    if action is modify, it record
     */
    public abstract void setCurData(String action, Object obj);


    /**
     * invoke when accept record
     * @return      true if success
     */
    public abstract boolean onAccept();

    /**
     * get current record
     * @return      current data or null
     */
    public abstract Object getCurData();
}
