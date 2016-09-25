package wxm.KeepAccount.ui.fragment.ShowData;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;

/**
 * fragment for daily data show
 * Created by wxm on 2016/9/25.
 */

public class TFShowDaily extends Fragment {
    private final static String TAG = "TFShowDaily";

    private ViewSwitcher    mVSSwitcher;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tf_show_daily, container, false);
        if(null != v)   {
            mVSSwitcher = UtilFun.cast(v.findViewById(R.id.vs_page));
            mVSSwitcher.setDisplayedChild(0);
        }
        return v;
    }
}
