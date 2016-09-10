package wxm.KeepAccount.ui.helper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acinterface.ACBudgetShow;


/**
 * activity adapter
 * Created by wxm on 2016/8/13.
 */
public class ACSBAdapter extends SimpleAdapter {
    private ACBudgetShow mHome;
    private ArrayList<HashMap<String, String>> mLVList;

    private static final int    BTDRAW_WIDTH    = 48;
    private static final int    BTDRAW_HEIGHT   = 48;

    public ACSBAdapter(ACBudgetShow home,
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
            ImageButton ibedit = UtilFun.cast(v.findViewById(R.id.ib_budget_edit));
            ImageButton ibdelete = UtilFun.cast(v.findViewById(R.id.ib_budget_delete));
            assert null != ibedit && null != ibdelete;

            HashMap<String, String> map = mLVList.get(position);
            String editst = map.get(ACBudgetShow.FIELD_EDIT_STATUS);
            String delst = map.get(ACBudgetShow.FIELD_DELETE_STATUS);
            assert null != editst && null != delst;

            if(editst.equals(ACBudgetShow.FIELD_VAL_ENABLE))    {
                ibedit.setVisibility(View.VISIBLE);
                ibedit.setOnClickListener(mHome);

                Drawable dr = ibedit.getDrawable();
                dr.setBounds(0, 0, BTDRAW_WIDTH, BTDRAW_HEIGHT);
                ibedit.setImageDrawable(dr);

                ibedit.setMaxWidth(BTDRAW_WIDTH);
                ibedit.setMinimumWidth(BTDRAW_WIDTH);
                ibedit.setMaxHeight(BTDRAW_HEIGHT);
                ibedit.setMinimumHeight(BTDRAW_HEIGHT);
            }   else    {
                ibedit.setVisibility(View.INVISIBLE);
            }

            if(delst.equals(ACBudgetShow.FIELD_VAL_ENABLE))    {
                ibdelete.setVisibility(View.VISIBLE);
                ibdelete.setOnClickListener(mHome);

                Drawable dr = ibdelete.getDrawable();
                dr.setBounds(0, 0, BTDRAW_WIDTH, BTDRAW_HEIGHT);
                ibdelete.setImageDrawable(dr);
            }   else    {
                ibdelete.setVisibility(View.INVISIBLE);
            }
        }

        return v;
    }
}

