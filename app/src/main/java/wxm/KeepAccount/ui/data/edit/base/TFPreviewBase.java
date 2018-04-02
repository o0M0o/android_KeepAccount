package wxm.KeepAccount.ui.data.edit.base;

import android.support.v4.app.Fragment;

/**
 * base for data preview
 * Created by WangXM on2016/9/27.
 */
public abstract class TFPreviewBase extends Fragment {
    /**
     * set preview record
     * @param obj   record for preview
     */
    public abstract void setPreviewPara(Object obj);


    /**
     * get preview record
     * @return      current data or null
     */
    public abstract Object getCurData();

    /**
     * reload UI
     */
    public abstract void reLoadView();
}
