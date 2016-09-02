package wxm.KeepAccount.Base.utility;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acinterface.ACShowBudget;


/**
 * activity adapter
 * Created by wxm on 2016/8/13.
 */
public class ACSBAdapter extends SimpleAdapter {
    private ACShowBudget mHome;
    private ArrayList<HashMap<String, String>> mLVList;

    public ACSBAdapter(ACShowBudget home,
                             Context context, List<? extends Map<String, ?>> data,
                             String[] from,
                             int[] to) {
        super(context, data, R.layout.li_budget, from, to);

        mHome = home;
        mLVList = UtilFun.cast(data);
    }

    @Override
    public View getView(final int position, View view, ViewGroup arg2) {
        View v = super.getView(position, view, arg2);
        if(null != v)   {
        }

        return v;
    }
}

