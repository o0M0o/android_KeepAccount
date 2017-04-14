package wxm.KeepAccount.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import cn.wxm.andriodutillib.Dialog.DlgOKOrNOBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.define.RecordTypeItem;
import wxm.KeepAccount.db.RecordTypeDBUtility;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.ui.dialog.utility.DlgResource;
import wxm.KeepAccount.ui.extend.IconButton.IconButton;
import wxm.KeepAccount.utility.ContextUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.data.edit.RecordInfo.ACRecordInfoEdit;

/**
 * 选择“记录类型”对话框
 * Created by 123 on 2016/11/1.
 */
public class DlgSelectRecordType extends DlgOKOrNOBase {
    private final static String KEY_NAME = "key_name";
    private final static String KEY_NOTE = "key_note";
    private final static String KEY_SELECTED = "key_selected";

    private final static String VAL_SELECTED     = "val_selected";
    private final static String VAL_NOT_SELECTED = "val_not_selected";

    private ArrayList<HashMap<String, String>>      mLHMData;
    private GVTypeAdapter                           mGAAdapter;

    private String  mRootType;
    private String  mCurType;

    @BindView(R.id.tv_hint)
    TextView        mTVNote;

    @BindView(R.id.gv_record_info)
    GridView        mGVMain;

    @BindView(R.id.ib_sort)
    IconButton      mIBSort;

    /**
     * 设置以前的“记录类型”
     * @param rt  记录类型归属的大类，可以为 :
     *             -- GlobalDef.STR_RECORD_PAY
     *             -- GlobalDef.STR_RECORD_INCOME
     * @param ot  以前的记录类型
     */
    public void setOldType(String rt, String ot)   {
        mRootType = rt;
        mCurType = ot;
    }

    /**
     * 得到当前“记录类型"
     * @return  当前“记录类型"
     */
    public String getCurType()  {
        return mCurType;
    }

    @Override
    protected View InitDlgView() {
        if(UtilFun.StringIsNullOrEmpty(mRootType)
                || (!GlobalDef.STR_RECORD_PAY.equals(mRootType)
                        && !GlobalDef.STR_RECORD_INCOME.equals(mRootType)))
            return null;

        // init data
        mLHMData = new ArrayList<>();
        mGAAdapter= new GVTypeAdapter(getActivity(), mLHMData,
                new String[] { KEY_NAME }, new int[]{ R.id.tv_type_name });
        InitDlgTitle(GlobalDef.STR_RECORD_PAY.equals(mRootType) ? "选择支出类型" : "选择收入类型",
                "接受", "放弃");

        // for UI component
        View vw = View.inflate(getActivity(), R.layout.dlg_select_record_info, null);
        ButterKnife.bind(this, vw);

        mGVMain.setAdapter(mGAAdapter);

        // for gridview show
        loadData();
        return vw;
    }

    @OnItemClick({R.id.gv_record_info})
    public void onGVItemClick(AdapterView<?> parent, View view, int position, long id)  {
        String tv_str = mLHMData.get(position).get(KEY_NAME);
        if(!tv_str.equals(mCurType))    {
            for(HashMap<String, String> hm : mLHMData)  {
                if(hm.get(KEY_NAME).equals(tv_str)) {
                    hm.put(KEY_SELECTED, VAL_SELECTED);

                    mTVNote.setText(UtilFun.StringIsNullOrEmpty(hm.get(KEY_NOTE)) ?
                            "" : hm.get(KEY_NOTE));
                    /*
                    String sz = hm.get(KEY_NOTE);
                    if(!UtilFun.StringIsNullOrEmpty(sz)) {
                        Toast ts = Toast.makeText(getContext(), sz, Toast.LENGTH_SHORT);
                        ts.setGravity(Gravity.CENTER, 0, 200);
                        ts.show();
                    }
                    */
                } else  {
                    hm.put(KEY_SELECTED, VAL_NOT_SELECTED);
                }
            }

            mCurType = tv_str;
            mGAAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 附加动作
     * @param v   动作view
     */
    @OnClick({R.id.ib_sort, R.id.ib_manage})
    public void onActionClick(View v) {
        int vid = v.getId();
        switch (vid)    {
            case R.id.ib_manage :   {
                Intent it = new Intent(getContext(), ACRecordInfoEdit.class);
                it.putExtra(ACRecordInfoEdit.IT_PARA_RECORDTYPE, mRootType);

                startActivityForResult(it, 1);
            }
            break;

            case R.id.ib_sort :     {
                String cur_name = mIBSort.getActName();
                boolean is_up = DlgResource.mSZSortByNameUp.equals(cur_name);

                mIBSort.setActName(is_up ? DlgResource.mSZSortByNameDown : DlgResource.mSZSortByNameUp);
                mIBSort.setActIcon(is_up ? R.drawable.ic_sort_down_1 : R.drawable.ic_sort_up_1);

                loadData();
            }
            break;
        }
    }


    private void loadData() {
        RecordTypeDBUtility rd = ContextUtil.getRecordTypeUtility();
        List<RecordTypeItem> al_type = GlobalDef.STR_RECORD_PAY.equals(mRootType) ?
                                                rd.getAllPayItem() : rd.getAllIncomeItem();

        boolean is_up = DlgResource.mSZSortByNameUp.equals(mIBSort.getActName());
        Collections.sort(al_type, (o1, o2) ->
                             is_up ?
                                o1.getType().compareTo(o2.getType())
                                : o2.getType().compareTo(o1.getType()));

        String old_note = null;
        mLHMData.clear();
        for(RecordTypeItem ri : al_type)    {
            HashMap<String, String> hmd = new HashMap<>();
            hmd.put(KEY_NAME, ri.getType());
            hmd.put(KEY_NOTE, ri.getNote());

            if(ri.getType().equals(mCurType)) {
                old_note = ri.getNote();
                hmd.put(KEY_SELECTED, VAL_SELECTED);
            } else  {
                hmd.put(KEY_SELECTED, VAL_NOT_SELECTED);
            }

            mLHMData.add(hmd);
        }

        //if(!UtilFun.StringIsNullOrEmpty(old_note))
        //    Toast.makeText(getContext(), old_note, Toast.LENGTH_SHORT).show();

        mTVNote.setText(UtilFun.StringIsNullOrEmpty(old_note) ? "" : old_note);
        mGAAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)   {
        loadData();
    }


    /**
     * 加载gridview的适配器类
     */
    public class GVTypeAdapter extends SimpleAdapter {
        GVTypeAdapter(Context context, List<? extends Map<String, ?>> data,
                         String[] from, int[] to) {
            super(context, data, R.layout.gi_record_type, from, to);
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
                HashMap<String, String> hm = mLHMData.get(position);
                int curCL = hm.get(KEY_SELECTED).equals(VAL_SELECTED) ?
                                DlgResource.mCLSelected : DlgResource.mCLNotSelected;
                v.setBackgroundColor(curCL);
            }

            return v;
        }
    }
}
