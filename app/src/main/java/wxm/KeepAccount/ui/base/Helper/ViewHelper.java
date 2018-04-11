package wxm.KeepAccount.ui.base.Helper;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;


/**
 * @author WangXM
 * @version create：2018/4/11
 */
public class ViewHelper {
    private View    mVWHolder;

    public ViewHelper(View v)   {
        mVWHolder = v;
    }

    @NonNull
    public <T extends View> T getChildView(@IdRes int viewId) {
        return (T)mVWHolder.findViewById(viewId);
    }

    /**
     * set visibility for child view
     * @param vId           id for child view
     * @param visibility    visibility for child view
     */
    public void setVisibility(@IdRes int vId, int visibility) {
        mVWHolder.findViewById(vId).setVisibility(visibility);
    }

    public ViewHelper setText(@IdRes int vId, String txt) {
        TextView v = mVWHolder.findViewById(vId);
        v.setText(txt);
        return this;
    }

    /**
     * setup textView text color
     * @param viewId    id for view
     * @param color     color for show
     * @return          ViewHolder后续操作可多次调用该方法（连续点）
     */
    @TargetApi(Build.VERSION_CODES.M)
    public ViewHelper setTextColor(@IdRes int viewId, @ColorRes int color) {
        Resources res = mVWHolder.getResources();
        TextView textView = mVWHolder.findViewById(viewId);
        textView.setTextColor(res.getColor(color, mVWHolder.getContext().getTheme()));

        return this;
    }
}
