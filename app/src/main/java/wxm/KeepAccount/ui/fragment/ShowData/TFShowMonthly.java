package wxm.KeepAccount.ui.fragment.ShowData;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acinterface.ACNoteShowNew;

/**
 * fragment for monthly show
 * Created by wxm on 2016/9/25.
 */
public class TFShowMonthly extends Fragment  implements ACNoteShowNew.IFShowUtil  {
    private final static String TAG = "TFShowMonthly";

    private ViewSwitcher mVSSwitcher;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tf_show_daily, container, false);
        if(null != v)   {
            mVSSwitcher = UtilFun.cast(v.findViewById(R.id.vs_page));
            mVSSwitcher.setDisplayedChild(1);
        }
        return v;
    }

    @Override
    public void switchPage() {
        mVSSwitcher.setDisplayedChild(mVSSwitcher.getDisplayedChild() == 0 ? 1 : 0);
    }
}
