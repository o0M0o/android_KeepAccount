package wxm.KeepAccount.ui.data.edit.RecordInfo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import wxm.androidutil.Dialog.DlgOKOrNOBase;
import wxm.androidutil.util.UiUtil;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.define.RecordTypeItem;
import wxm.KeepAccount.ui.data.edit.base.TFEditBase;
import wxm.KeepAccount.ui.dialog.DlgRecordInfo;
import wxm.KeepAccount.utility.ContextUtil;
import wxm.KeepAccount.utility.ToolUtil;

/**
 * UI for record info
 * Created by WangXM on2016/9/28.
 */
public class TFEditRecordInfo extends TFEditBase implements View.OnClickListener {
    private final static String TAG = "TFEditRecordInfo";

    // 没有选中action
    private final static int SELECTED_NONE = 1;
    // 选中"minus"
    private final static int SELECTED_MINUS = 2;
    // 选中"accpet"
    private final static int SELECTED_ACCPET = 3;
    // 选中"reject"
    private final static int SELECTED_REJECT = 4;
    private final static String KEY_NAME = "key_name";
    private final static String KEY_NOTE = "key_note";
    private final static String KEY_SELECTED = "key_selected";
    private final static String KEY_ID = "key_id";
    private final static String VAL_SELECTED = "val_selected";
    private final static String VAL_NOT_SELECTED = "val_not_selected";
    private String mEditType;
    // data for view
    private String mCurType;
    private ArrayList<HashMap<String, String>> mLHMData;
    private GVTypeAdapter mGVAdapter;

    // ui component for view
    @BindView(R.id.tv_note)
    TextView mTVNote;

    @BindView(R.id.rl_add)
    RelativeLayout mRLActAdd;

    @BindView(R.id.rl_minus)
    RelativeLayout mRLActMinus;

    @BindView(R.id.rl_pencil)
    RelativeLayout mRLActPencil;

    @BindView(R.id.rl_accept)
    RelativeLayout mRLActAccept;

    @BindView(R.id.rl_reject)
    RelativeLayout mRLActReject;

    @BindView(R.id.gv_record_info)
    GridView mGVHolder;

    private static int mCLSelected;
    private static int mCLNotSelected;
    static {
        mCLSelected = UiUtil.getColor(ContextUtil.getInstance(), R.color.peachpuff);
        mCLNotSelected = UiUtil.getColor(ContextUtil.getInstance(), R.color.white);
    }

    @Override
    protected void initUI(Bundle bundle)    {
        // init action
        mRLActAdd.setOnClickListener(this);
        mRLActMinus.setOnClickListener(this);
        mRLActAccept.setOnClickListener(this);
        mRLActReject.setOnClickListener(this);
        mRLActPencil.setOnClickListener(this);
        update_acts(SELECTED_NONE);
    }

    @Override
    protected void loadUI(Bundle bundle)    {
        if (!mEditType.equals(GlobalDef.STR_RECORD_PAY) &&
                !mEditType.equals(GlobalDef.STR_RECORD_INCOME))
            return;


        // init gv
        mLHMData = new ArrayList<>();
        mGVAdapter = new GVTypeAdapter(getActivity(), mLHMData,
                new String[]{KEY_NAME}, new int[]{R.id.tv_type_name});
        mGVHolder.setAdapter(mGVAdapter);
        mGVHolder.setOnItemClickListener((parent, view1, position, id) -> onGVItemClick(position));
        load_info();
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.vw_edit_record_info, container, false);
    }

    /**
     * update actions
     * @param type  for type
     */
    private void update_acts(int type) {
        switch (type) {
            case SELECTED_NONE:
                mRLActPencil.setVisibility(View.INVISIBLE);
                mRLActAdd.setVisibility(View.VISIBLE);
                mRLActMinus.setVisibility(View.VISIBLE);
                mRLActAccept.setVisibility(View.INVISIBLE);
                mRLActReject.setVisibility(View.INVISIBLE);
                break;

            case SELECTED_MINUS:
                mRLActPencil.setVisibility(View.INVISIBLE);
                if (mRLActMinus.isSelected()) {
                    mRLActAdd.setVisibility(View.VISIBLE);
                    mRLActAccept.setVisibility(View.INVISIBLE);
                    mRLActReject.setVisibility(View.INVISIBLE);

                    mRLActMinus.setBackgroundColor(mCLNotSelected);
                    mRLActMinus.setSelected(false);
                } else {
                    mRLActAdd.setVisibility(View.INVISIBLE);
                    mRLActAccept.setVisibility(View.VISIBLE);
                    mRLActReject.setVisibility(View.VISIBLE);

                    mRLActMinus.setBackgroundColor(mCLSelected);
                    mRLActMinus.setSelected(true);
                }
                break;

            case SELECTED_ACCPET:
                mRLActPencil.setVisibility(View.INVISIBLE);
                mRLActAdd.setVisibility(View.VISIBLE);
                mRLActAccept.setVisibility(View.INVISIBLE);
                mRLActReject.setVisibility(View.INVISIBLE);
                mRLActMinus.setBackgroundColor(mCLNotSelected);
                break;

            case SELECTED_REJECT:
                mRLActPencil.setVisibility(View.INVISIBLE);
                mRLActAdd.setVisibility(View.VISIBLE);
                mRLActAccept.setVisibility(View.INVISIBLE);
                mRLActReject.setVisibility(View.INVISIBLE);
                mRLActMinus.setBackgroundColor(mCLNotSelected);
                break;
        }
    }

    /**
     * load data to gird view
     */
    private void load_info() {
        mLHMData.clear();
        List<RecordTypeItem> al_type;
        if (mEditType.equals(GlobalDef.STR_RECORD_PAY)) {
            al_type = ContextUtil.getRecordTypeUtility().getAllPayItem();
        } else {
            al_type = ContextUtil.getRecordTypeUtility().getAllIncomeItem();
        }

        if (null != al_type) {
            Collections.sort(al_type, (o1, o2) -> o1.getType().compareTo(o2.getType()));

            for (RecordTypeItem ri : al_type) {
                HashMap<String, String> hmd = new HashMap<>();
                hmd.put(KEY_NAME, ri.getType());
                hmd.put(KEY_NOTE, ri.getNote());
                hmd.put(KEY_SELECTED, VAL_NOT_SELECTED);
                hmd.put(KEY_ID, String.valueOf(ri.get_id()));

                mLHMData.add(hmd);
            }

            mGVAdapter.notifyDataSetChanged();
        }
    }

    /**
     * after gridview item selected
     * @param position      current click position
     */
    private void onGVItemClick(int position) {
        if (mRLActMinus.isSelected()) {
            HashMap<String, String> hm = mLHMData.get(position);
            String old_sel = hm.get(KEY_SELECTED);
            hm.put(KEY_SELECTED, old_sel.equals(VAL_SELECTED) ?
                    VAL_NOT_SELECTED : VAL_SELECTED);

            int i_sel = 0;
            for (HashMap<String, String> hml : mLHMData) {
                if (hml.get(KEY_SELECTED).equals(VAL_SELECTED))
                    i_sel++;
            }

            mRLActAccept.setVisibility(0 == i_sel ? View.INVISIBLE : View.VISIBLE);
            mTVNote.setText(String.format(Locale.CHINA, "已选择%d项待删除", i_sel));
            mGVAdapter.notifyDataSetChanged();
        } else {
            String tv_str = mLHMData.get(position).get(KEY_NAME);
            for (HashMap<String, String> hm : mLHMData) {
                if (hm.get(KEY_NAME).equals(tv_str)) {
                    if (hm.get(KEY_SELECTED).equals(VAL_NOT_SELECTED)) {
                        hm.put(KEY_SELECTED, VAL_SELECTED);
                        mTVNote.setText(UtilFun.StringIsNullOrEmpty(hm.get(KEY_NOTE)) ?
                                "" : hm.get(KEY_NOTE));

                        mRLActPencil.setVisibility(View.VISIBLE);
                        mCurType = tv_str;
                    } else {
                        hm.put(KEY_SELECTED, VAL_NOT_SELECTED);
                        mTVNote.setText("");

                        mRLActPencil.setVisibility(View.INVISIBLE);
                        mCurType = "";
                    }
                } else {
                    hm.put(KEY_SELECTED, VAL_NOT_SELECTED);
                }
            }

            mGVAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void setCurData(String action, Object obj) {
        mEditType = UtilFun.cast_t(obj);
    }

    @Override
    public boolean onAccept() {
        return true;
    }

    @Override
    public Object getCurData() {
        RecordTypeItem ri = null;
        if (!UtilFun.StringIsNullOrEmpty(mCurType)) {
            for (HashMap<String, String> hm : mLHMData) {
                if (hm.get(KEY_NAME).equals(mCurType)) {
                    ri = ContextUtil.getRecordTypeUtility().getData(Integer.valueOf(hm.get(KEY_ID)));
                    break;
                }
            }
        }

        return ri;
    }


    @Override
    public void onClick(View v) {
        final int vid = v.getId();
        switch (vid) {
            case R.id.rl_pencil:
            case R.id.rl_add: {
                DlgRecordInfo dp = new DlgRecordInfo();
                dp.setInitDate(R.id.rl_pencil == vid ? (RecordTypeItem) getCurData() : null);
                dp.setRecordType(mEditType);
                dp.addDialogListener(new DlgOKOrNOBase.DialogResultListener() {
                    @Override
                    public void onDialogPositiveResult(DialogFragment dialog) {
                        DlgRecordInfo cur_dp = UtilFun.cast_t(dialog);
                        RecordTypeItem ri = cur_dp.getCurDate();
                        if (null != ri) {
                            if (R.id.rl_add == vid)
                                ContextUtil.getRecordTypeUtility().createData(ri);
                            else
                                ContextUtil.getRecordTypeUtility().modifyData(ri);
                            load_info();
                        }
                    }

                    @Override
                    public void onDialogNegativeResult(DialogFragment dialog) {
                    }
                });

                dp.show(getFragmentManager(), "添加记录信息");
            }
            break;

            case R.id.rl_minus: {
                update_acts(SELECTED_MINUS);
                mCurType = "";
                for (HashMap<String, String> hm : mLHMData) {
                    hm.put(KEY_SELECTED, VAL_NOT_SELECTED);
                }

                mRLActAccept.setVisibility(View.INVISIBLE);
                mTVNote.setText(mRLActMinus.isSelected() ? "请选择待删除项" : "");
                mGVAdapter.notifyDataSetChanged();
            }
            break;

            case R.id.rl_accept: {
                //for gv
                final LinkedList<Integer> ll_i = new LinkedList<>();
                for (HashMap<String, String> hm : mLHMData) {
                    if (hm.get(KEY_SELECTED).equals(VAL_SELECTED)) {
                        ll_i.add(Integer.valueOf(hm.get(KEY_ID)));
                    }
                }

                if (0 < ll_i.size()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("请确认是否删除数据!").setTitle("警告");
                    builder.setPositiveButton("确认", (dialog, which) -> {
                        for (int id : ll_i) {
                            ContextUtil.getRecordTypeUtility().removeData(id);
                        }

                        mTVNote.setText("");
                        load_info();
                        update_acts(SELECTED_ACCPET);
                    });
                    builder.setNegativeButton("取消", (dialog, which) -> {
                    });

                    AlertDialog dlg = builder.create();
                    dlg.show();
                } else {
                    mTVNote.setText("");
                    load_info();
                    update_acts(SELECTED_ACCPET);
                }
            }
            break;

            case R.id.rl_reject: {
                // for ui
                update_acts(SELECTED_REJECT);

                // for gv
                for (HashMap<String, String> hm : mLHMData) {
                    hm.put(KEY_SELECTED, VAL_NOT_SELECTED);
                }
                mTVNote.setText("");
                mGVAdapter.notifyDataSetChanged();
            }
            break;
        }

    }


    /**
     * adapter for gridview
     */
    public class GVTypeAdapter extends SimpleAdapter {
        private int mCLSelected;
        private int mCLNotSelected;

        GVTypeAdapter(Context context, List<? extends Map<String, ?>> data,
                      String[] from, int[] to) {
            super(context, data, R.layout.gi_record_type, from, to);

            mCLSelected = UiUtil.getColor(context, R.color.peachpuff);
            mCLNotSelected = UiUtil.getColor(context, R.color.white);
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
                        mCLSelected : mCLNotSelected;
                v.setBackgroundColor(curCL);
            }

            return v;
        }
    }
}
