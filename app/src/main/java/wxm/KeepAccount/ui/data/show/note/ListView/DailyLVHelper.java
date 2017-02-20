package wxm.KeepAccount.ui.data.show.note.ListView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.Dialog.DlgOKOrNOBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.INote;
import wxm.KeepAccount.Base.define.GlobalDef;
import wxm.KeepAccount.Base.utility.ContextUtil;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.utility.NoteShowDataHelper;
import wxm.KeepAccount.ui.utility.NoteShowInfo;
import wxm.KeepAccount.ui.data.show.note.ACNoteShow;
import wxm.KeepAccount.ui.data.show.note.ACDailyDetail;
import wxm.KeepAccount.ui.data.edit.ACNoteEdit;
import wxm.KeepAccount.ui.data.report.ACReport;
import wxm.KeepAccount.ui.dialog.DlgSelectReportDays;
import wxm.KeepAccount.ui.utility.HelperDayNotesInfo;

/**
 * 日数据视图辅助类
 * Created by 123 on 2016/9/10.
 */
public class DailyLVHelper extends LVShowDataBase
        implements OnClickListener {
    // 若为true则数据以时间降序排列
    private boolean mBTimeDownOrder = true;

    // for action
    //private final static int ACTION_NONE    = 0;
    private final static int ACTION_DELETE = 1;
    private final static int ACTION_EDIT = 2;
    private int mActionType = ACTION_EDIT;

    @BindView(R.id.lv_show)
    ListView mLVData;

    /**
     * 如果设置为true则数据可以删除
     */
    private boolean mBLCanDelete = false;

    public DailyLVHelper() {
        super();

        LOG_TAG         = "DailyLVHelper";
        mBLCanDelete    = false;

        mBActionExpand  = false;
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        mSelfView = inflater.inflate(R.layout.lv_newpager, container, false);
        mBFilter = false;
        ButterKnife.bind(this, mSelfView);

        refreshAttachLayout();
        initActs(mSelfView);
        return mSelfView;
    }

    /**
     * 初始化可隐藏动作条
     *
     * @param pv 视图
     */
    private void initActs(View pv) {
        mRLActReport.setOnClickListener(v -> {
            final String[] d_s = {"", ""};

            DlgSelectReportDays dlg_days = new DlgSelectReportDays();
            dlg_days.addDialogListener(new DlgOKOrNOBase.DialogResultListener() {
                @Override
                public void onDialogPositiveResult(DialogFragment dialogFragment) {
                    d_s[0] = dlg_days.getStartDay();
                    d_s[1] = dlg_days.getEndDay();
                    Log.d(LOG_TAG, "start : " + d_s[0] + ", end : " + d_s[1]);

                    ArrayList<String> ls_sz = new ArrayList<>();
                    ls_sz.add(d_s[0]);
                    ls_sz.add(d_s[1]);

                    Intent it = new Intent(getRootActivity(), ACReport.class);
                    it.putExtra(ACReport.PARA_TYPE, ACReport.PT_DAY);
                    it.putStringArrayListExtra(ACReport.PARA_LOAD, ls_sz);
                    getRootActivity().startActivity(it);
                }

                @Override
                public void onDialogNegativeResult(DialogFragment dialogFragment) {
                    Log.d(LOG_TAG, "start : " + d_s[0] + ", end : " + d_s[1]);
                }
            });

            dlg_days.show(getRootActivity().getSupportFragmentManager(), "select days");
        });

        mRLActAdd.setOnClickListener(v -> {
            ACNoteShow ac = getRootActivity();
            Intent intent = new Intent(ac, ACNoteEdit.class);
            intent.putExtra(ACNoteEdit.PARA_ACTION, GlobalDef.STR_CREATE);

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            intent.putExtra(GlobalDef.STR_RECORD_DATE,
                    String.format(Locale.CHINA, "%d-%02d-%02d %02d:%02d"
                            , cal.get(Calendar.YEAR)
                            , cal.get(Calendar.MONTH) + 1
                            , cal.get(Calendar.DAY_OF_MONTH)
                            , cal.get(Calendar.HOUR_OF_DAY)
                            , cal.get(Calendar.MINUTE)));

            ac.startActivityForResult(intent, 1);
        });

        mRLActDelete.setOnClickListener(v -> {
            mActionType = ACTION_DELETE;
            refreshView();
        });

        mRLActRefresh.setOnClickListener(v -> {
            mActionType = ACTION_EDIT;
            reloadView(v.getContext(), false);
        });

        final ImageView iv_sort = UtilFun.cast_t(mRLActSort.findViewById(R.id.iv_sort));
        final TextView tv_sort = UtilFun.cast_t(mRLActSort.findViewById(R.id.tv_sort));
        iv_sort.setImageDrawable(pv.getContext().getResources()
                .getDrawable(mBTimeDownOrder ? R.drawable.ic_sort_up_1 : R.drawable.ic_sort_down_1));
        tv_sort.setText(mBTimeDownOrder ? R.string.cn_sort_up_by_time : R.string.cn_sort_down_by_time);
        mRLActSort.setOnClickListener(v -> {
            mBTimeDownOrder = !mBTimeDownOrder;

            iv_sort.setImageDrawable(pv.getContext().getResources()
                    .getDrawable(mBTimeDownOrder ? R.drawable.ic_sort_up_1 : R.drawable.ic_sort_down_1));
            tv_sort.setText(mBTimeDownOrder ? R.string.cn_sort_up_by_time : R.string.cn_sort_down_by_time);

            refreshData();
            refreshView();
        });
    }


    @Override
    public void filterView(List<String> ls_tag) {
        if (null != ls_tag) {
            mBFilter = true;
            mActionType = ACTION_EDIT;

            mFilterPara.clear();
            mFilterPara.addAll(ls_tag);
            refreshView();
        } else {
            mBFilter = false;
            mActionType = ACTION_EDIT;

            refreshView();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int vid = v.getId();
        switch (vid) {
            case R.id.bt_accpet:
                if (ACTION_DELETE == mActionType) {
                    ArrayList<Integer> al_i = new ArrayList<>();
                    ArrayList<Integer> al_p = new ArrayList<>();

                    SelfAdapter cur_ap = (SelfAdapter)mLVData.getAdapter();
                    List<String> ls_days = cur_ap.getWaitDeleteDays();
                    HashMap<String, ArrayList<INote>> hm =
                            NoteShowDataHelper.getInstance().getNotesForDay();
                    for(String day : ls_days)   {
                        List<INote> ls_n = hm.get(day);
                        for(INote n : ls_n) {
                            if (n.isPayNote())
                                al_p.add(n.getId());
                            else
                                al_i.add(n.getId());
                        }
                    }

                    boolean bf = false;
                    if (!al_i.isEmpty()) {
                        ContextUtil.getPayIncomeUtility().deleteIncomeNotes(al_i);
                        bf = true;
                    }

                    if (!al_p.isEmpty()) {
                        ContextUtil.getPayIncomeUtility().deletePayNotes(al_p);
                        bf = true;
                    }

                    mActionType = ACTION_EDIT;
                    if (bf)
                        reloadView(v.getContext(), false);
                }
                break;

            case R.id.bt_giveup:
                mActionType = ACTION_EDIT;
                refreshView();
                break;
        }
    }

    /**
     * 重新加载数据
     */
    protected void refreshData() {
        super.refreshData();

        mTSLastLoadViewTime.setTime(Calendar.getInstance().getTimeInMillis());

        mMainPara.clear();

        // for day
        HashMap<String, NoteShowInfo> hm_d = NoteShowDataHelper.getInstance().getDayInfo();
        ArrayList<String> set_k_d = new ArrayList<>(hm_d.keySet());
        Collections.sort(set_k_d, (o1, o2) -> !mBTimeDownOrder ? o1.compareTo(o2) : o2.compareTo(o1));

        for (String k : set_k_d) {
            NoteShowInfo ni = hm_d.get(k);
            HashMap<String, String> map = new HashMap<>();
            map.put(K_MONTH, k.substring(0, 7));

            String km = k.substring(8, 10);
            km = km.startsWith("0") ? km.replaceFirst("0", " ") : km;
            map.put(K_DAY_NUMEBER, km);
            try {
                Timestamp ts = ToolUtil.StringToTimestamp(k);
                Calendar day = Calendar.getInstance();
                day.setTimeInMillis(ts.getTime());
                map.put(K_DAY_IN_WEEK, getDayInWeek(day.get(Calendar.DAY_OF_WEEK)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            map.put(K_DAY_PAY_COUNT, String.valueOf(ni.getPayCount()));
            map.put(K_DAY_INCOME_COUNT, String.valueOf(ni.getIncomeCount()));
            map.put(K_DAY_PAY_AMOUNT, String.format(Locale.CHINA,
                    "%.02f", ni.getPayAmount()));
            map.put(K_DAY_INCOME_AMOUNT, String.format(Locale.CHINA,
                    "%.02f", ni.getIncomeAmount()));

            BigDecimal bd_l = ni.getBalance();
            String v_l = String.format(Locale.CHINA,
                    0 < bd_l.floatValue() ? "+ %.02f" : "%.02f", bd_l);
            map.put(K_AMOUNT, v_l);

            map.put(K_TAG, k);
            map.put(K_SHOW, checkUnfoldItem(k) ? V_SHOW_UNFOLD : V_SHOW_FOLD);
            mMainPara.add(map);
        }
    }

    /**
     * 仅更新视图
     */
    @Override
    protected void refreshView() {
        refreshAttachLayout();

        // load show data
        LinkedList<HashMap<String, String>> n_mainpara = new LinkedList<>();
        if (mBFilter) {
            for (HashMap<String, String> i : mMainPara) {
                String cur_tag = i.get(K_TAG);
                for (String ii : mFilterPara) {
                    if (cur_tag.equals(ii)) {
                        n_mainpara.add(i);
                        break;
                    }
                }
            }
        } else {
            n_mainpara.addAll(mMainPara);
        }

        // 设置listview adapter
        SelfAdapter mSNAdapter = new SelfAdapter(mSelfView.getContext(), n_mainpara,
                new String[]{K_MONTH, K_DAY_NUMEBER, K_DAY_IN_WEEK},
                new int[]{R.id.tv_month, R.id.tv_day_number, R.id.tv_day_in_week});
        mLVData.setAdapter(mSNAdapter);
        mSNAdapter.notifyDataSetChanged();
    }

    private void refreshAttachLayout() {
        setAttachLayoutVisible(ACTION_EDIT != mActionType || mBFilter ?
                View.VISIBLE : View.GONE);
        setFilterLayoutVisible(mBFilter ? View.VISIBLE : View.GONE);
        setAccpetGiveupLayoutVisible(ACTION_EDIT != mActionType && !mBFilter ? View.VISIBLE : View.GONE);
    }


    /**
     * 首级列表adapter
     */
    private class SelfAdapter extends SimpleAdapter implements OnClickListener {
        private final static String TAG = "SelfAdapter";
        private int mClOne;
        private int mClTwo;

        private int mCLNoSelected;
        private int mCLSelected;

        private ArrayList<String>   mALWaitDeleteDays = new ArrayList<>();

        SelfAdapter(Context context, List<? extends Map<String, ?>> mdata,
                    String[] from, int[] to) {
            super(context, mdata, R.layout.li_daily_show, from, to);

            Resources res = context.getResources();
            mClOne = res.getColor(R.color.color_1);
            mClTwo = res.getColor(R.color.color_2);

            mCLNoSelected = res.getColor(R.color.red_ff725f_half);
            mCLSelected   = res.getColor(R.color.red_ff725f);
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
                v.setBackgroundColor(0 == position % 2 ? mClOne : mClTwo);

                HashMap<String, String> hm = UtilFun.cast(getItem(position));
                v.setOnClickListener(this);

                // for line data
                RelativeLayout rl_del = UtilFun.cast_t(v.findViewById(R.id.rl_delete));
                rl_del.setVisibility(mActionType == ACTION_EDIT ? View.GONE : View.VISIBLE);
                rl_del.setOnClickListener(view1 -> {
                    String k_tag = hm.get(K_TAG);
                    if(mALWaitDeleteDays.contains(k_tag))  {
                        mALWaitDeleteDays.remove(k_tag);
                        rl_del.setBackgroundColor(mCLNoSelected);
                    } else  {
                        mALWaitDeleteDays.add(k_tag);
                        rl_del.setBackgroundColor(mCLSelected);
                    }
                });

                // for show
                RelativeLayout rl_info = UtilFun.cast_t(v.findViewById(R.id.rl_info));
                HelperDayNotesInfo.fillNoteInfo(rl_info,
                        hm.get(K_DAY_PAY_COUNT), hm.get(K_DAY_PAY_AMOUNT),
                        hm.get(K_DAY_INCOME_COUNT), hm.get(K_DAY_INCOME_AMOUNT),
                        hm.get(K_AMOUNT));
            }

            return v;
        }

        /**
         * 返回等待删除的日数据
         * @return  待删除日数据
         */
        public List<String> getWaitDeleteDays() {
            return mALWaitDeleteDays;
        }

        @Override
        public void onClick(View view) {
            int pos = mLVData.getPositionForView(view);

            HashMap<String, String> hm = UtilFun.cast(getItem(pos));
            String k_tag = hm.get(K_TAG);
            ACNoteShow ac = getRootActivity();
            Intent it = new Intent(ac, ACDailyDetail.class);
            it.putExtra(ACDailyDetail.K_HOTDAY, k_tag);
            ac.startActivity(it);
        }
    }
}
