package wxm.KeepAccount.ui.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.wxm.andriodutillib.Dialog.DlgOKOrNOBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.R;

/**
 * 颜色选择对话框
 * Created by 123 on 2016/10/10.
 */
public class DlgSelectColor extends DlgOKOrNOBase
        implements AdapterView.OnItemClickListener  {
    private int     mHotPos = GlobalDef.INVALID_ID;

    private final static String  PARA_COLOR = "color";
    private final static int[] ARR_COLOR = {
            R.color.aliceblue
            ,R.color.antiquewhite
            ,R.color.lightyellow
            ,R.color.snow
            ,R.color.lavenderblush
            ,R.color.bisque
            ,R.color.pink
            ,R.color.orange
            ,R.color.coral
            ,R.color.hotpink
            ,R.color.orangered
            ,R.color.magenta
            ,R.color.violet
            ,R.color.darksalmon
            ,R.color.lavender
            ,R.color.burlywood
            ,R.color.crimson
            ,R.color.firebrick
            ,R.color.powderblue
            ,R.color.lightsteelblue
            ,R.color.paleturquoise
            ,R.color.greenyellow
            ,R.color.darkgrey
            ,R.color.lawngreen
            ,R.color.lightslategrey
            ,R.color.cadetblue
            ,R.color.indigo
            ,R.color.lightseagreen
            ,R.color.cyan
    };


    public int getSelectedColor()   {
        if(GlobalDef.INVALID_ID != mHotPos)
            return getResources().getColor(ARR_COLOR[mHotPos]);
        else
            return GlobalDef.INVALID_ID;
    }

    /**
     * Gridview的itemclick处理方法
     * @param parent        param
     * @param view          param
     * @param position      param
     * @param id            param
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Resources res = getResources();

        view.setBackgroundColor(res.getColor(R.color.red));
        if(GlobalDef.INVALID_ID == mHotPos)   {
            mHotPos = position;
        }   else    {
            parent.getChildAt(mHotPos).setBackgroundColor(res.getColor(R.color.white));
            mHotPos = position;
        }
    }

    @Override
    protected View InitDlgView() {
        InitDlgTitle("选择颜色", "接受", "放弃");

        View vw = View.inflate(getActivity(), R.layout.dlg_select_color, null);
        GridView gv = UtilFun.cast(vw.findViewById(R.id.gv_colors));
        assert null != gv;
        gv.setOnItemClickListener(this);

        Resources res = getResources();
        ArrayList<HashMap<String, Object>> ls_data = new ArrayList<>();
        for(int i : ARR_COLOR)  {
            HashMap<String, Object> hm = new HashMap<>();
            hm.put(PARA_COLOR, res.getColor(i));
            ls_data.add(hm);
        }

        GVChannelAdapter ga = new GVChannelAdapter(getActivity(), ls_data,
                new String[] { }, new int[]{ });
        gv.setAdapter(ga);
        ga.notifyDataSetChanged();
        return vw;
    }


    /**
     * 加载gridview的适配器类
     */
    public class GVChannelAdapter extends SimpleAdapter {
        GVChannelAdapter(Context context, List<? extends Map<String, ?>> data,
                         String[] from, int[] to) {
            super(context, data, R.layout.gi_color, from, to);
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
            if (null != v) {
                HashMap<String, Object> hmd = UtilFun.cast(getItem(position));
                int c = UtilFun.cast(hmd.get(PARA_COLOR));

                // for image
                ImageView iv = UtilFun.cast(v.findViewById(R.id.iv_color));
                assert null != iv;
                iv.setBackgroundColor(c);
            }

            return v;
        }
    }

}
