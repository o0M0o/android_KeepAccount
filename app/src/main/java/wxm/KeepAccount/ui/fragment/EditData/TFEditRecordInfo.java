package wxm.KeepAccount.ui.fragment.EditData;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.db.RecordTypeItem;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.dialog.DlgOKAndNOBase;
import wxm.KeepAccount.ui.dialog.DlgRecordInfo;
import wxm.KeepAccount.ui.fragment.base.TFEditBase;

/**
 * 编辑记录类型
 * Created by wxm on 2016/9/28.
 */
public class TFEditRecordInfo extends TFEditBase implements View.OnClickListener {
    private final static String TAG = "TFEditRecordInfo";

    private View        mSelfView;
    private String      mEditType;
    private String      mAction;

    private final static String KEY_NAME = "key_name";
    private final static String KEY_NOTE = "key_note";
    private final static String KEY_SELECTED = "key_selected";
    private final static String KEY_ID = "key_id";

    private final static String VAL_SELECTED     = "val_selected";
    private final static String VAL_NOT_SELECTED = "val_not_selected";

    // data for view
    private String  mCurType;
    private ArrayList<HashMap<String, String>>  mLHMData;
    private GVTypeAdapter                       mGVAdapter;

    // ui component for view
    private TextView        mTVNote;
    private GridView        mGVHolder;
    private RelativeLayout  mRLActAdd;
    private RelativeLayout  mRLActMinus;
    private RelativeLayout  mRLActAccept;
    private RelativeLayout  mRLActReject;

    private int mCLSelected;
    private int mCLNotSelected;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vw_edit_record_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != view) {
            mCLSelected = getResources().getColor(R.color.peachpuff);
            mCLNotSelected = getResources().getColor(R.color.white);

            mSelfView = view;
            if(!mEditType.equals(AppGobalDef.STR_RECORD_PAY) &&
                    !mEditType.equals(AppGobalDef.STR_RECORD_INCOME))
                return;

            // init view
            mGVHolder = UtilFun.cast_t(mSelfView.findViewById(R.id.gv_record_info));
            mTVNote = UtilFun.cast_t(mSelfView.findViewById(R.id.tv_note));
            mTVNote.setText("");
            mRLActAdd = UtilFun.cast_t(mSelfView.findViewById(R.id.rl_add));
            mRLActMinus = UtilFun.cast_t(mSelfView.findViewById(R.id.rl_minus));
            mRLActAccept = UtilFun.cast_t(mSelfView.findViewById(R.id.rl_accept));
            mRLActReject = UtilFun.cast_t(mSelfView.findViewById(R.id.rl_reject));

            // fill data
            if(mEditType.equals(AppGobalDef.STR_RECORD_PAY))    {
                init_pay_info();
            } else  {
                init_income_info();
            }

            // init action
            mRLActAdd.setOnClickListener(this);
            mRLActMinus.setOnClickListener(this);
            mRLActAccept.setOnClickListener(this);
            mRLActReject.setOnClickListener(this);

            mRLActAccept.setVisibility(View.INVISIBLE);
            mRLActReject.setVisibility(View.INVISIBLE);
        }
    }

    private void init_income_info() {
        List<RecordTypeItem> al_type = AppModel.getRecordTypeUtility().getAllIncomeItem();
        mLHMData = new ArrayList<>();
        for(RecordTypeItem ri : al_type)    {
            HashMap<String, String> hmd = new HashMap<>();
            hmd.put(KEY_NAME, ri.getType());
            hmd.put(KEY_NOTE, ri.getNote());
            hmd.put(KEY_SELECTED, VAL_NOT_SELECTED);
            hmd.put(KEY_ID, String.valueOf(ri.get_id()));

            mLHMData.add(hmd);
        }

        mGVAdapter = new GVTypeAdapter(getActivity(), mLHMData,
                        new String[] { KEY_NAME }, new int[]{ R.id.tv_type_name });
        mGVHolder.setAdapter(mGVAdapter);
        mGVHolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onGVItemClick(position);
            }
        });
        mGVAdapter.notifyDataSetChanged();
    }

    private void init_pay_info() {
        List<RecordTypeItem> al_type = AppModel.getRecordTypeUtility().getAllPayItem();
        mLHMData = new ArrayList<>();
        for(RecordTypeItem ri : al_type)    {
            HashMap<String, String> hmd = new HashMap<>();
            hmd.put(KEY_NAME, ri.getType());
            hmd.put(KEY_NOTE, ri.getNote());
            hmd.put(KEY_SELECTED, VAL_NOT_SELECTED);
            hmd.put(KEY_ID, String.valueOf(ri.get_id()));

            mLHMData.add(hmd);
        }

        mGVAdapter = new GVTypeAdapter(getActivity(), mLHMData,
                        new String[] { KEY_NAME }, new int[]{ R.id.tv_type_name });
        mGVHolder.setAdapter(mGVAdapter);
        mGVHolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onGVItemClick(position);
            }
        });
        mGVAdapter.notifyDataSetChanged();
    }


    private void onGVItemClick(int position) {
        if(mRLActMinus.isSelected())    {
            HashMap<String, String> hm = mLHMData.get(position);
            String old_sel = hm.get(KEY_SELECTED);
            hm.put(KEY_SELECTED, old_sel.equals(VAL_SELECTED) ?
                                    VAL_NOT_SELECTED : VAL_SELECTED);

            int i_sel = 0;
            for(HashMap<String, String> hml : mLHMData )   {
                if(hml.get(KEY_SELECTED).equals(VAL_SELECTED))
                    i_sel++;
            }

            mRLActAccept.setVisibility(0 == i_sel ? View.INVISIBLE : View.VISIBLE);
            mTVNote.setText(String.format(Locale.CHINA, "已选择%d项待删除", i_sel));
            mGVAdapter.notifyDataSetChanged();
        } else {
            String tv_str = mLHMData.get(position).get(KEY_NAME);
            if (!tv_str.equals(mCurType)) {
                for (HashMap<String, String> hm : mLHMData) {
                    if (hm.get(KEY_NAME).equals(tv_str)) {
                        hm.put(KEY_SELECTED, VAL_SELECTED);
                        mTVNote.setText(UtilFun.StringIsNullOrEmpty(hm.get(KEY_NOTE)) ?
                                "" : hm.get(KEY_NOTE));
                    } else {
                        hm.put(KEY_SELECTED, VAL_NOT_SELECTED);
                    }
                }

                mCurType = tv_str;
                mGVAdapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    public void setCurData(String action, Object obj) {
        mAction = action;
        mEditType = UtilFun.cast_t(obj);
    }

    @Override
    public boolean onAccept() {
        return true;
    }

    @Override
    public Object getCurData() {
        return null;
    }

    @Override
    public void reLoadView() {
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        switch (vid)    {
            case R.id.rl_add :  {
                DlgRecordInfo dp = new DlgRecordInfo();
                dp.setInitDate(null);
                dp.setRecordType(mEditType);
                dp.setDialogListener(new DlgOKAndNOBase.NoticeDialogListener() {
                    @Override
                    public void onDialogPositiveClick(DialogFragment dialog) {
                        DlgRecordInfo cur_dp = UtilFun.cast_t(dialog);
                        RecordTypeItem ri = cur_dp.getCurDate();
                        if(null != ri) {
                            AppModel.getRecordTypeUtility().addItem(ri);
                            if(mEditType.equals(AppGobalDef.STR_RECORD_PAY))    {
                                init_pay_info();
                            } else  {
                                init_income_info();
                            }
                        }
                    }

                    @Override
                    public void onDialogNegativeClick(DialogFragment dialog) {
                    }
                });

                dp.show(getFragmentManager(), "选择日期");
            }
            break;

            case R.id.rl_minus :    {
                if(mRLActMinus.isSelected())    {
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

                    mCurType = "";
                    for (HashMap<String, String> hm : mLHMData) {
                        hm.put(KEY_SELECTED, VAL_NOT_SELECTED);
                    }

                    mRLActAccept.setVisibility(View.INVISIBLE);
                    mTVNote.setText("请选择待删除项");
                    mGVAdapter.notifyDataSetChanged();
                }
            }
            break;

            case R.id.rl_accept :   {
                //for gv
                final LinkedList<Integer> ll_i = new LinkedList<>();
                for (HashMap<String, String> hm : mLHMData) {
                    if(hm.get(KEY_SELECTED).equals(VAL_SELECTED))   {
                        ll_i.add(Integer.valueOf(hm.get(KEY_ID)));
                    }
                }

                if(0 < ll_i.size()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("请确认是否删除数据!").setTitle("警告");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for(int id : ll_i) {
                                AppModel.getRecordTypeUtility().removeItemById(id);
                            }

                            mTVNote.setText("");
                            if(mEditType.equals(AppGobalDef.STR_RECORD_PAY))    {
                                init_pay_info();
                            } else  {
                                init_income_info();
                            }

                            // for ui
                            mRLActAdd.setVisibility(View.VISIBLE);
                            mRLActAccept.setVisibility(View.INVISIBLE);
                            mRLActReject.setVisibility(View.INVISIBLE);
                            mRLActMinus.setBackgroundColor(mCLNotSelected);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    AlertDialog dlg = builder.create();
                    dlg.show();
                } else {
                    mTVNote.setText("");
                    if(mEditType.equals(AppGobalDef.STR_RECORD_PAY))    {
                        init_pay_info();
                    } else  {
                        init_income_info();
                    }

                    // for ui
                    mRLActAdd.setVisibility(View.VISIBLE);
                    mRLActAccept.setVisibility(View.INVISIBLE);
                    mRLActReject.setVisibility(View.INVISIBLE);
                    mRLActMinus.setBackgroundColor(mCLNotSelected);
                }
            }
            break;

            case R.id.rl_reject :   {
                // for ui
                mRLActAdd.setVisibility(View.VISIBLE);
                mRLActAccept.setVisibility(View.INVISIBLE);
                mRLActReject.setVisibility(View.INVISIBLE);
                mRLActMinus.setBackgroundColor(mCLNotSelected);

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
     * 加载gridview的适配器类
     */
    public class GVTypeAdapter extends SimpleAdapter {
        private int mCLSelected;
        private int mCLNotSelected;

        GVTypeAdapter(Context context, List<? extends Map<String, ?>> data,
                      String[] from, int[] to) {
            super(context, data, R.layout.gi_record_type, from, to);

            mCLSelected = context.getResources().getColor(R.color.peachpuff);
            mCLNotSelected = context.getResources().getColor(R.color.white);
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
