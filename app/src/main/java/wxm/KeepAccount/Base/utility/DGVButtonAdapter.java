package wxm.KeepAccount.Base.utility;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

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


    public final static String ACT_LOOK_DATA    = "查看数据";
    public final static String ACT_LOOK_BUDGET  = "查看预算";
    public final static String ACT_ADD_BUDGET   = "添加预算";
    public final static String ACT_ADD_DATA     = "添加记录";
    public final static String ACT_LOGOUT       = "退出登录";

    public final static String[] ACTION_NAMES   =   {
            ACT_LOOK_DATA
            ,ACT_LOOK_BUDGET
            ,ACT_ADD_BUDGET
            ,ACT_ADD_DATA
            ,ACT_LOGOUT
    };

    private Context  mCTContext;


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

            // for button
            Button bt = UtilFun.cast(v.findViewById(R.id.bt_action));
            assert null != bt;
            bt.setText(hv);

            Rect rt = new Rect();
            bt.getPaint().getTextBounds(hv, 0, hv.length(), rt);
            bt.setWidth(rt.width()+ 32);
            //bt.setHeight(rt.height());
            if(mCTContext instanceof View.OnClickListener)  {
                View.OnClickListener ac_cl = UtilFun.cast(mCTContext);
                bt.setOnClickListener(ac_cl);
            }

            // for image
            Resources res = v.getResources();
            ImageView iv = UtilFun.cast(v.findViewById(R.id.iv_image));
            assert null != iv;
            Bitmap bm = null;
            switch (hv)     {
                case ACT_LOOK_DATA :
                    bm = BitmapFactory.decodeResource(res, R.drawable.ic_act_look_data);
                    break;

                case ACT_LOOK_BUDGET :
                    bm = BitmapFactory.decodeResource(res, R.drawable.ic_act_look_budget);
                    break;

                case ACT_ADD_BUDGET :
                    bm = BitmapFactory.decodeResource(res, R.drawable.ic_act_add_budget);
                    break;

                case ACT_ADD_DATA:
                    bm = BitmapFactory.decodeResource(res, R.drawable.ic_bt_add);
                    break;

                case ACT_LOGOUT :
                    bm = BitmapFactory.decodeResource(res, R.drawable.ic_bt_logout);
                    break;
            }

            if(null != bm) {
                iv.setImageBitmap(bm);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        }

        return v;
    }
}
