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

import wxm.KeepAccount.define.EAction;
import wxm.androidutil.Dialog.DlgOKOrNOBase;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.dialog.base.DlgResource;
import wxm.KeepAccount.utility.DGVButtonAdapter;

/**
 * select follow channel
 * Created by WangXM on 2016/9/20.
 */
public class DlgSelectChannel extends DlgOKOrNOBase {
    // for hot channel
    private ArrayList<String> mLSHotChannel = new ArrayList<>();

    /**
     * get followed channel(channel name)
     * after finish dlg, use this get new followed list
     * @return      new followed channel
     */
    public List<String> getHotChannel() {
        return mLSHotChannel;
    }

    /**
     * set followed channel(channel name)
     * before use dlg, use this set init val
     * @param org_hot   followed list
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
        for(EAction ea : EAction.values())  {
            HashMap<String, Object> hm = new HashMap<>();
            hm.put(DGVButtonAdapter.HKEY_ACT_NAME, ea.getName());

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
     * adapter for gridview
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
                Bitmap bm = EAction.getIcon(hv);
                if (null != bm) {
                    ImageView iv = UtilFun.cast_t(v.findViewById(R.id.iv_act));
                    iv.setImageBitmap(bm);
                }
            }

            return v;
        }
    }

}
