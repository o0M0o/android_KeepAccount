package wxm.KeepAccount.Base.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;

/**
 * drag grid view adapter for buttons in welcome activity
 * Created by 123 on 2016/9/19.
 */
public class DGVButtonAdapter extends SimpleAdapter {
    public final static String HKEY_ACT_NAME = "HKEY_ACT_NAME";

    private Context             mCTContext;

    public DGVButtonAdapter(Context context, List<? extends Map<String, ?>> data,
                            String[] from, int[] to) {
        super(context, data, R.layout.gi_button, from, to);
        mCTContext = context;
    }

    @Override
    public int getViewTypeCount() {
        int org_ct = getCount();
        return org_ct < 1 ? 1 : org_ct;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup arg2) {
        View v = super.getView(position, view, arg2);
        if(null != v)   {
            HashMap<String, Object> hmd = UtilFun.cast(getItem(position));
            String hv = UtilFun.cast(hmd.get(HKEY_ACT_NAME));

            /*
            // for button
            Button bt = UtilFun.cast(v.findViewById(R.id.bt_action));
            assert null != bt;
            bt.setText(hv);

            Rect rt = new Rect();
            bt.getPaint().getTextBounds(hv, 0, hv.length(), rt);
            bt.setWidth(rt.width()+ 32);
            //bt.setHeight(rt.height());
            */

            TextView tv = UtilFun.cast(v.findViewById(R.id.tv_name));
            ToolUtil.throwExIf(null == tv);
            tv.setText(hv);

            if(mCTContext instanceof View.OnClickListener)  {
                View.OnClickListener ac_cl = UtilFun.cast(mCTContext);
                //bt.setOnClickListener(ac_cl);
                v.setOnClickListener(ac_cl);
            }

            // for image
            ImageView iv = UtilFun.cast(v.findViewById(R.id.iv_image));
            assert null != iv;
            Bitmap bm = ActionHelper.getBitMapFromName(hv);
            if(null != bm) {
                iv.setImageBitmap(bm);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        }

        return v;
    }

    /**
     * 获取当前动作
     * @return  当前动作
     */
    public List<String> getCurAction()  {
        ArrayList<String> ret_ls = new ArrayList<>();
        int ic = getCount();
        for(int i = 0; i < ic; ++i) {
            HashMap<String, Object> hmd = UtilFun.cast(getItem(i));
            String hv = UtilFun.cast(hmd.get(HKEY_ACT_NAME));
            ret_ls.add(hv);
        }

        return ret_ls;
    }
}
