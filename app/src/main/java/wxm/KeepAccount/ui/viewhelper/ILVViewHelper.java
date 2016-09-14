package wxm.KeepAccount.ui.viewhelper;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 列表view辅助类
 * Created by 123 on 2016/9/10.
 */
public interface ILVViewHelper {
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
     * 过滤视图
     * @param ls_tag   过滤参数 :
     *                 1. 如果为null则不过滤
     *                 2. 如果不为null, 但为空则过滤（不显示任何数据)
     */
    void filterView(List<String> ls_tag);

    /**
     * 处理activity返回结果
     * @param requestCode   返回参数
     * @param resultCode    返回参数
     * @param data          返回参数
     */
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
