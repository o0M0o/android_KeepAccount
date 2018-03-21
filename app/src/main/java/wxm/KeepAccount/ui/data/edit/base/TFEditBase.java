package wxm.KeepAccount.ui.data.edit.base;

import android.support.v4.app.Fragment;

/**
 * base for data edit
 * Created by wxm on 2016/9/27.
 */
public abstract class TFEditBase extends Fragment {
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

    /**
     * reload UI
     */
    public abstract void reLoadView();
}
