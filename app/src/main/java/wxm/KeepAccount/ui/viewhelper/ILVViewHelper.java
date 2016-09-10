package wxm.KeepAccount.ui.viewhelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 列表view辅助类
 * Created by 123 on 2016/9/10.
 */
public interface ILVViewHelper {
    View createView(LayoutInflater inflater, ViewGroup container);

    View getView();

    void loadView();
}
