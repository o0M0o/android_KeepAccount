package wxm.KeepAccount.ui.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.wxm.andriodutillib.Dialog.DlgOKOrNOBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.dialog.utility.DlgResource;
import wxm.KeepAccount.utility.ActionHelper;
import wxm.KeepAccount.utility.DGVButtonAdapter;

/**
 * 选择关注channel的对话框
 * Created by 123 on 2016/9/20.
 */
public class DlgSelectChannel extends DlgOKOrNOBase {
    // for hot channel
    private ArrayList<String> mLSHotChannel = new ArrayList<>();

    /*
     * 根据当前hot channel来初始化
     * @param org_hot   当前hot channel
    public DlgSelectChannel(ArrayList<String> org_hot)  {
        super();
        mLSHotChannel.addAll(org_hot);
    }
    */

    /**
     * 获取放置在首页上的项（项名）
     * 结束对话框后使用此方法得到更新的值
     *
     * @return 用户在对话框中的选择
     */
    public List<String> getHotChannel() {
        return mLSHotChannel;
    }

    /**
     * 设置放置在首页上的项（项名）
     * 显示对话框前使用此方法设置初始值
     *
     * @param org_hot 放置在首页上的项名
     */
    public void setHotChannel(List<String> org_hot) {
        mLSHotChannel.addAll(org_hot);
    }

    @Override
    protected View InitDlgView() {
        InitDlgTitle("选择首页项", "接受", "放弃");

        View vw = View.inflate(getActivity(), R.layout.dlg_select_channel, null);
        GridView gv = UtilFun.cast_t(vw.findViewById(R.id.gv_channels));
        gv.setOnItemClickListener((parent, view, position, id) -> {
            HashMap<String, Object> hmd = UtilFun.cast(parent.getAdapter().getItem(position));
            String act = UtilFun.cast(hmd.get(DGVButtonAdapter.HKEY_ACT_NAME));

            boolean hot = mLSHotChannel.contains(act);
            if (hot) {
                mLSHotChannel.remove(act);
                view.setBackground(DlgResource.mDAChannelNoSel);
            } else {
                mLSHotChannel.add(act);
                view.setBackground(DlgResource.mDAChannelSel);
            }
        });

        ArrayList<HashMap<String, Object>> ls_data = new ArrayList<>();
        for (String i : ActionHelper.ACTION_NAMES) {
            HashMap<String, Object> hm = new HashMap<>();
            hm.put(DGVButtonAdapter.HKEY_ACT_NAME, i);

            ls_data.add(hm);
        }

        GVChannelAdapter ga = new GVChannelAdapter(getActivity(), ls_data,
                new String[]{DGVButtonAdapter.HKEY_ACT_NAME},
                new int[]{R.id.tv_name});
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
            super(context, data, R.layout.gi_channel, from, to);
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
                String hv = UtilFun.cast(hmd.get(DGVButtonAdapter.HKEY_ACT_NAME));

                v.setBackground(mLSHotChannel.contains(hv) ?
                        DlgResource.mDAChannelSel : DlgResource.mDAChannelNoSel);

                // for image
                ImageView iv = UtilFun.cast_t(v.findViewById(R.id.iv_act));
                Bitmap bm = ActionHelper.getBitMapFromName(hv);
                if (null != bm) {
                    iv.setImageBitmap(bm);
                }
            }

            return v;
        }
    }

}
