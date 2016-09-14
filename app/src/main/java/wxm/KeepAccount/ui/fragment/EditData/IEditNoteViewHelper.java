package wxm.KeepAccount.ui.fragment.EditData;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 编辑数据的视图辅助类
 * Created by 123 on 2016/9/13.
 */
public interface IEditNoteViewHelper {
    /**
     *  创建视图
     * @param inflater      视图加载参数
     * @param container     视图所在group参数
     * @return              若成功，返回所创建视图
     */
    View createView(LayoutInflater inflater, ViewGroup container);

    /**
     * 获得视图
     * @return   返回已经创建的视图
     */
    View getView();

    /**
     * 加载视图
     * 在这里进行视图初始化
     */
    void loadView();

    /**
     * 设置运行参数
     *
     * @param action “更新”或“新建”
     * @param obj     若是“更新”，则此参数为待更新数据
     */
    void setPara(String action, Object obj);

    /**
     * 处理activity返回结果
     * @param requestCode   返回参数
     * @param resultCode    返回参数
     * @param data          返回参数
     */
    void onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * activity上选择“确认”后调用
     * @return   若一切成功，返回true
     */
    boolean onAccept();
}
