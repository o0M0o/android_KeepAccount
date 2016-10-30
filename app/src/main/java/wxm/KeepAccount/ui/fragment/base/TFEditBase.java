package wxm.KeepAccount.ui.fragment.base;

import android.support.v4.app.Fragment;

/**
 * 数据编辑fragment基类
 * Created by wxm on 2016/9/27.
 */
public abstract class TFEditBase extends Fragment {
    //private final static String TAG = "TFEditBase";
    protected String mAction;

    /**
     * 设置运行数据
     *
     * @param action 可以为以下参数 --
     *               * “更新”(AppGobalDef.STR_MODIFY)
     *               * “新建”(AppGobalDef.STR_CREATE)
     * @param obj     若是“更新”，则此参数为待更新数据
     */
    public abstract void setCurPara(String action, Object obj);


    /**
     * 接受数据时调用
     *
     * @return   若成功返回true
     */
    public abstract boolean onAccept();

    /**
     * 获得当前数据
     *
     * @return  当前数据，可以为null
     */
    public abstract Object getCurData();
}
