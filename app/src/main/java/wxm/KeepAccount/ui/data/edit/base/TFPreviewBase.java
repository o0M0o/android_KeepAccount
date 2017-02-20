package wxm.KeepAccount.ui.data.edit.base;

import android.support.v4.app.Fragment;

/**
 * 数据预览fragment基类
 * Created by wxm on 2016/9/27.
 */
public abstract class TFPreviewBase extends Fragment {
    //private final static String LOG_TAG = "TFPreviewBase";

    /**
     * 设置预览参数
     *
     * @param obj     预览参数
     */
    public abstract void setPreviewPara(Object obj);


    /**
     * 获得当前数据
     *
     * @return  当前数据，可以为null
     */
    public abstract Object getCurData();

    /**
     * 重新加载视图
     */
    public abstract void reLoadView();
}
