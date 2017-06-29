package wxm.KeepAccount.ui.data.show.note.ListView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.OnClick;
import cn.wxm.andriodutillib.Dialog.DlgOKOrNOBase;
import cn.wxm.andriodutillib.util.FastViewHolder;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.define.INote;
import wxm.KeepAccount.ui.base.ResourceHelper;
import wxm.KeepAccount.ui.data.edit.Note.ACNoteAdd;
import wxm.KeepAccount.ui.data.report.ACReport;
import wxm.KeepAccount.ui.data.show.note.ACDailyDetail;
import wxm.KeepAccount.ui.data.show.note.ACNoteShow;
import wxm.KeepAccount.ui.data.show.note.ShowData.FilterShowEvent;
import wxm.KeepAccount.ui.dialog.DlgSelectReportDays;
import wxm.KeepAccount.ui.extend.ValueShow.ValueShow;
import wxm.KeepAccount.ui.utility.NoteDataHelper;
import wxm.KeepAccount.ui.utility.NoteShowInfo;
import wxm.KeepAccount.utility.ContextUtil;
import wxm.KeepAccount.utility.ToolUtil;

/**
 * 日数据视图辅助类
 * Created by 123 on 2016/9/10.
 */
public class LVDaily extends LVBase {
    // for action
    //private final static int ACTION_NONE    = 0;
    private final static int ACTION_DELETE = 1;
    private final static int ACTION_EDIT = 2;

    // 若为true则数据以时间降序排列
    private boolean mBTimeDownOrder = true;
    private int mActionType = ACTION_EDIT;

    public LVDaily() {
        super();

        LOG_TAG = "LVDaily";
        mBActionExpand = false;
    }

    /**
     * 过滤视图事件
     *
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFilterShowEvent(FilterShowEvent event) {
        List<String> e_p = event.getFilterTag();
        if ((NoteDataHelper.TAB_TITLE_MONTHLY.equals(event.getSender()))
                && (null != e_p)) {
            mBFilter = true;
            mActionType = ACTION_EDIT;

            mFilterPara.clear();
            mFilterPara.addAll(e_p);
            loadUIUtility(true);
        }
    }

    /**
     * 初始化可隐藏动作条
     */
    @Override
    protected void initActs() {
        mIBSort.setActIcon(mBTimeDownOrder ? R.drawable.ic_sort_up_1 : R.drawable.ic_sort_down_1);
        mIBSort.setActName(mBTimeDownOrder ? R.string.cn_sort_up_by_time : R.string.cn_sort_down_by_time);
    }

    /**
     * 附加动作
     *
     * @param v 动作view
     */
    @OnClick({R.id.ib_sort, R.id.ib_refresh, R.id.ib_delete,
            R.id.ib_add, R.id.ib_report})
    public void onActionClick(View v) {
        switch (v.getId()) {
            case R.id.ib_sort: {
                mBTimeDownOrder = !mBTimeDownOrder;

                mIBSort.setActIcon(mBTimeDownOrder ? R.drawable.ic_sort_up_1 : R.drawable.ic_sort_down_1);
                mIBSort.setActName(mBTimeDownOrder ? R.string.cn_sort_up_by_time : R.string.cn_sort_down_by_time);

                reorderData();
                loadUIUtility(true);
            }
            break;

            case R.id.ib_refresh: {
                mActionType = ACTION_EDIT;
                reloadView(getContext(), false);
            }
            break;

            case R.id.ib_delete: {
                if (ACTION_DELETE != mActionType) {
                    mActionType = ACTION_DELETE;
                    redrawUI();
                }
            }
            break;

            case R.id.ib_add: {
                ACNoteShow ac = getRootActivity();
                Intent intent = new Intent(ac, ACNoteAdd.class);
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
            }
            break;

            case R.id.ib_report: {
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
            }
            break;
        }
    }


    /**
     * "接受"或者"取消"后动作
     *
     * @param v 动作view
     */
    @OnClick({R.id.bt_accpet, R.id.bt_giveup, R.id.bt_giveup_filter})
    public void onAccpetOrGiveupClick(View v) {
        int vid = v.getId();
        switch (vid) {
            case R.id.bt_accpet:
                if (ACTION_DELETE == mActionType) {
                    ArrayList<Integer> al_i = new ArrayList<>();
                    ArrayList<Integer> al_p = new ArrayList<>();

                    SelfAdapter cur_ap = (SelfAdapter) mLVShow.getAdapter();
                    List<String> ls_days = cur_ap.getWaitDeleteDays();
                    HashMap<String, ArrayList<INote>> hm =
                            NoteDataHelper.getInstance().getNotesForDay();
                    for (String day : ls_days) {
                        List<INote> ls_n = hm.get(day);
                        for (INote n : ls_n) {
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
                redrawUI();
                break;

            case R.id.bt_giveup_filter:
                mBFilter = false;
                loadUIUtility(true);
                break;
        }
    }

    /**
     * 重新加载数据
     */
    @Override
    protected void refreshData() {
        super.refreshData();

        //showLoadingProgress(true);
        mMainPara.clear();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // for day
                List<String> set_k_d = NoteDataHelper.getNotesDays();
                Collections.sort(set_k_d, (o1, o2) -> !mBTimeDownOrder ? o1.compareTo(o2) : o2.compareTo(o1));

                Calendar cl_day = Calendar.getInstance();
                for (String k : set_k_d) {
                    NoteShowInfo ni = NoteDataHelper.getInfoByDay(k);
                    HashMap<String, String> map = new HashMap<>();
                    map.put(K_MONTH, k.substring(0, 7));

                    String km = k.substring(8, 10);
                    String km_show = km.startsWith("0") ? km.replaceFirst("0", " ") : km;
                    map.put(K_DAY_NUMEBER, km_show);

                    int year = Integer.valueOf(k.substring(0, 4));
                    int month = Integer.valueOf(k.substring(5, 7));
                    int day = Integer.valueOf(km);
                    cl_day.set(year, month, day);
                    map.put(K_DAY_IN_WEEK, ToolUtil.getDayInWeek(cl_day.get(Calendar.DAY_OF_WEEK)));

                    map.put(K_DAY_PAY_COUNT, String.valueOf(ni.getPayCount()));
                    map.put(K_DAY_INCOME_COUNT, String.valueOf(ni.getIncomeCount()));
                    map.put(K_DAY_PAY_AMOUNT, ni.getSZPayAmount());
                    map.put(K_DAY_INCOME_AMOUNT, ni.getSZIncomeAmount());

                    BigDecimal bd_l = ni.getBalance();
                    String v_l = String.format(Locale.CHINA,
                            0 < bd_l.floatValue() ? "+ %.02f" : "%.02f", bd_l);
                    map.put(K_AMOUNT, v_l);

                    map.put(K_TAG, k);
                    map.put(K_SHOW, checkUnfoldItem(k) ? V_SHOW_UNFOLD : V_SHOW_FOLD);
                    mMainPara.add(map);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                // After completing execution of given task, control will return here.
                // Hence if you want to populate UI elements with fetched data, do it here.
                loadUIUtility(true);
                //showLoadingProgress(false);
            }
        }.execute();
    }


    @Override
    protected void loadUI() {
        loadUIUtility(false);
    }


    /// BEGIN PRIVATE
    /**
     * 用现有数据重绘UI
     */
    private void redrawUI() {
        // adjust attach layout
        setAttachLayoutVisible(ACTION_EDIT != mActionType || mBFilter ?
                View.VISIBLE : View.GONE);
        setFilterLayoutVisible(mBFilter ? View.VISIBLE : View.GONE);
        setAccpetGiveupLayoutVisible(ACTION_EDIT != mActionType && !mBFilter ? View.VISIBLE : View.GONE);

        SelfAdapter cur_ap = (SelfAdapter) mLVShow.getAdapter();
        cur_ap.notifyDataSetChanged();
    }


    /**
     * 加载UI
     * @param b_fully 若为true则加载数据
     */
    private void loadUIUtility(boolean b_fully) {
        /*
        Log.d(LOG_TAG, "in loadUIUtility, b_fully = " + Boolean.toString(b_fully));
        String[] calls = ToolUtil.getCallStack(8);
        for (int i = 0; i < calls.length; ++i) {
            Log.v(LOG_TAG, "in loadUIUtility, [" + i + "] = " + calls[i]);
        }
        */

        // adjust attach layout
        setAttachLayoutVisible(ACTION_EDIT != mActionType || mBFilter ?
                View.VISIBLE : View.GONE);
        setFilterLayoutVisible(mBFilter ? View.VISIBLE : View.GONE);
        setAccpetGiveupLayoutVisible(ACTION_EDIT != mActionType && !mBFilter ? View.VISIBLE : View.GONE);

        if (b_fully) {
            // load show data
            LinkedList<HashMap<String, String>> n_mainpara;
            if (mBFilter) {
                n_mainpara = new LinkedList<>();
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
                n_mainpara = mMainPara;
            }

            // 设置listview adapter
            SelfAdapter mSNAdapter = new SelfAdapter(ContextUtil.getInstance(), n_mainpara,
                    new String[]{K_MONTH, K_DAY_NUMEBER, K_DAY_IN_WEEK},
                    new int[]{R.id.tv_month, R.id.tv_day_number, R.id.tv_day_in_week});

            mLVShow.setAdapter(mSNAdapter);
            mSNAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 调整数据排序
     */
    private void reorderData() {
        Collections.reverse(mMainPara);
    }
    /// END PRIVATE


    /**
     * 首级列表adapter
     */
    private class SelfAdapter extends SimpleAdapter {
        private final static String TAG = "SelfAdapter";

        private ArrayList<String> mALWaitDeleteDays = new ArrayList<>();

        private View.OnClickListener mCLAdapter = v -> {
            int vid = v.getId();
            int pos = mLVShow.getPositionForView(v);

            HashMap<String, String> hm = UtilFun.cast(getItem(pos));
            String k_tag = hm.get(K_TAG);

            switch (vid) {
                case R.id.rl_delete: {
                    if (mALWaitDeleteDays.contains(k_tag)) {
                        mALWaitDeleteDays.remove(k_tag);
                        v.setBackgroundColor(ResourceHelper.mCRLVItemNoSel);
                    } else {
                        mALWaitDeleteDays.add(k_tag);
                        v.setBackgroundColor(ResourceHelper.mCRLVItemSel);
                    }
                }
                break;

                default: {
                    ACNoteShow ac = getRootActivity();
                    Intent it = new Intent(ac, ACDailyDetail.class);
                    it.putExtra(ACDailyDetail.K_HOTDAY, k_tag);
                    ac.startActivity(it);
                }
                break;
            }
        };


        SelfAdapter(Context context, List<? extends Map<String, ?>> mdata,
                    String[] from, int[] to) {
            super(context, mdata, R.layout.li_daily_show, from, to);
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
            FastViewHolder viewHolder = FastViewHolder.get(getRootActivity(),
                    view, R.layout.li_daily_show);

            View root_view = viewHolder.getConvertView();
            root_view.setBackgroundColor(0 == position % 2 ?
                    ResourceHelper.mCRLVLineOne : ResourceHelper.mCRLVLineTwo);

            // for line data
            RelativeLayout rl_del = viewHolder.getView(R.id.rl_delete);
            rl_del.setVisibility(mActionType == ACTION_EDIT ? View.GONE : View.VISIBLE);
            rl_del.setOnClickListener(mCLAdapter);

            viewHolder.getView(R.id.rl_date).setOnClickListener(mCLAdapter);
            viewHolder.getView(R.id.vs_daily_info).setOnClickListener(mCLAdapter);

            // for show
            HashMap<String, String> hm = UtilFun.cast(getItem(position));
            viewHolder.setText(R.id.tv_month, hm.get(K_MONTH));
            viewHolder.setText(R.id.tv_day_number, hm.get(K_DAY_NUMEBER));
            viewHolder.setText(R.id.tv_day_in_week, hm.get(K_DAY_IN_WEEK));

            ValueShow vs = viewHolder.getView(R.id.vs_daily_info);
            HashMap<String, Object> hm_attr = new HashMap<>();
            hm_attr.put(ValueShow.ATTR_PAY_COUNT, hm.get(K_DAY_PAY_COUNT));
            hm_attr.put(ValueShow.ATTR_PAY_AMOUNT, hm.get(K_DAY_PAY_AMOUNT));
            hm_attr.put(ValueShow.ATTR_INCOME_COUNT, hm.get(K_DAY_INCOME_COUNT));
            hm_attr.put(ValueShow.ATTR_INCOME_AMOUNT, hm.get(K_DAY_INCOME_AMOUNT));
            vs.adjustAttribute(hm_attr);

            return root_view;
        }

        /**
         * 返回等待删除的日数据
         *
         * @return 待删除日数据
         */
        List<String> getWaitDeleteDays() {
            return mALWaitDeleteDays;
        }
    }
}
