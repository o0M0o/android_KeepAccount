package wxm.KeepAccount.ui.fragment.ShowData;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wxm.KeepAccount.R;

/**
 * show fragment for yearly
 * Created by wxm on 2016/9/25.
 */
public class TFShowYearly extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tf_show_daily, container, false);
    }
}
