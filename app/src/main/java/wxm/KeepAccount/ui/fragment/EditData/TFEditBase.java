package wxm.KeepAccount.ui.fragment.EditData;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * 数据编辑fragment基类
 * Created by wxm on 2016/9/27.
 */
public abstract class TFEditBase extends Fragment {
    private final static String TAG = "TFEditBase";
    protected String mAction;

    /**
     * 设置运行参数
     *
     * @param action “更新”或“新建”
     * @param obj     若是“更新”，则此参数为待更新数据
     */
    public abstract void setPara(String action, Object obj);


    /**
     * activity上选择“确认”后调用
     * @return   若一切成功，返回true
     */
    public abstract boolean onAccept();
}
