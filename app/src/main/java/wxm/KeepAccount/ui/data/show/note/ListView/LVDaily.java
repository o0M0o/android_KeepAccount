package wxm.KeepAccount.ui.data.show.note.ListView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import butterknife.BindView;
import butterknife.OnClick;
import wxm.KeepAccount.ui.base.Adapter.LVAdapter;
import wxm.androidutil.Dialog.DlgOKOrNOBase;
import wxm.androidutil.util.FastViewHolder;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.define.INote;
import wxm.KeepAccount.ui.base.Helper.ResourceHelper;
import wxm.KeepAccount.ui.data.edit.Note.ACNoteAdd;
import wxm.KeepAccount.ui.data.report.ACReport;
import wxm.KeepAccount.ui.data.show.note.ACDailyDetail;
import wxm.KeepAccount.ui.data.show.note.ACNoteShow;
import wxm.KeepAccount.ui.data.show.note.ShowData.FilterShowEvent;
import wxm.KeepAccount.ui.dialog.DlgSelectReportDays;
import wxm.KeepAccount.ui.data.show.note.base.ValueShow;
import wxm.KeepAccount.ui.utility.NoteDataHelper;
import wxm.KeepAccount.ui.utility.NoteShowInfo;
import wxm.KeepAccount.utility.ContextUtil;
import wxm.KeepAccount.utility.ToolUtil;
import wxm.uilib.IconButton.IconButton;

/**
 * listview for daily data
 * Created by WangXM on 2016/9/10.
 */
public class LVDaily extends LVBase {
    // for action
    enum EAction    {
        DELETE(1),
        EDIT(2);

        int mID;
        EAction(int v)  {
            mID = v;
        }

        public boolean isEdit() {
            return mID == EDIT.mID;
        }

        public boolean isDelete() {
            return mID == DELETE.mID;
        }
    }
    private EAction mAction = EAction.EDIT;

    // 若为true则数据以时间降序排列
    private boolean mBTimeDownOrder = true;

    class MainAdapterItem   {
        public String  tag;
        public String  show;

        public String  month;
        public String  dayNumber;
        public String  dayInWeek;
        public recordDetail day;
        public String  amount;

        MainAdapterItem()    {
            show = EShowFold.FOLD.getName();
            day = new recordDetail();
        }
    }
    protected final LinkedList<MainAdapterItem> mMainPara;


    class DailyActionHelper extends ActionHelper    {
        @BindView(R.id.ib_sort)
        IconButton mIBSort;

        DailyActionHelper() {
            super();
        }

        @Override
        protected void initActs() {
            mIBSort.setActIcon(mBTimeDownOrder ?
                    R.drawable.ic_sort_up_1 : R.drawable.ic_sort_down_1);
            mIBSort.setActName(mBTimeDownOrder ?
                    R.string.cn_sort_up_by_time : R.string.cn_sort_down_by_time);
        }

        /**
         * actions
         * @param v     clicked view
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
                    mAction = EAction.EDIT;
                    reloadView(getContext(), false);
                }
                break;

                case R.id.ib_delete: {
                    if (mAction.isEdit()) {
                        mAction = EAction.DELETE;
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
    }


    public LVDaily() {
        super();
        mBActionExpand = false;

        mMainPara = new LinkedList<>();
        mAHActs = new DailyActionHelper();
    }

    /**
     * filter event
     * @param event     param
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFilterShowEvent(FilterShowEvent event) {
        List<String> e_p = event.getFilterTag();
        if ((NoteDataHelper.TAB_TITLE_MONTHLY.equals(event.getSender()))
                && (null != e_p)) {
            mBFilter = true;
            mAction = EAction.EDIT;

            mFilterPara.clear();
            mFilterPara.addAll(e_p);
            loadUIUtility(true);
        }
    }

    /**
     * handler for 'accept' or 'cancel'
     * @param v     action view
     */
    @OnClick({R.id.bt_accpet, R.id.bt_giveup, R.id.bt_giveup_filter})
    public void onAccpetOrGiveupClick(View v) {
        int vid = v.getId();
        switch (vid) {
            case R.id.bt_accpet:
                if (mAction.isDelete()) {
                    ArrayList<Integer> al_i = new ArrayList<>();
                    ArrayList<Integer> al_p = new ArrayList<>();

                    MainAdapter cur_ap = (MainAdapter) mLVShow.getAdapter();
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

                    mAction = EAction.EDIT;
                    if (bf)
                        reloadView(v.getContext(), false);
                }
                break;

            case R.id.bt_giveup:
                mAction = EAction.EDIT;
                redrawUI();
                break;

            case R.id.bt_giveup_filter:
                mBFilter = false;
                loadUIUtility(true);
                break;
        }
    }

    @Override
    protected void asyncInitUI(Bundle bundle) {
        mMainPara.clear();
        // for day
        List<String> set_k_d = NoteDataHelper.getNotesDays();
        Collections.sort(set_k_d, (o1, o2) -> !mBTimeDownOrder ? o1.compareTo(o2) : o2.compareTo(o1));

        Calendar cl_day = Calendar.getInstance();
        ExecutorService fixedThreadPool = Executors.newCachedThreadPool();
        LinkedList<Future<LinkedList<MainAdapterItem>>> ll_rets = new LinkedList<>();

        int once_process = 10;
        int idx = 0;
        int len = set_k_d.size();
        while (idx < len)   {
            List<String> jobs = set_k_d.subList(idx, idx + once_process < len ? idx + once_process : len - 1);
            idx += once_process;

            Future<LinkedList<MainAdapterItem>> ret = fixedThreadPool
                    .submit(() -> {
                        LinkedList<MainAdapterItem> maps = new LinkedList<>();
                        for(String k : jobs) {
                            NoteShowInfo ni = NoteDataHelper.getInfoByDay(k);
                            MainAdapterItem map = new MainAdapterItem();
                            map.month = k.substring(0, 7);

                            String km = k.substring(8, 10);
                            km = km.startsWith("0") ? km.replaceFirst("0", "") : km;
                            map.dayNumber = km;

                            int year = Integer.valueOf(k.substring(0, 4));
                            int month = Integer.valueOf(k.substring(5, 7));
                            int day = Integer.valueOf(km);
                            cl_day.set(year, month, day);
                            map.dayInWeek = ToolUtil.getDayInWeek(cl_day.get(Calendar.DAY_OF_WEEK));

                            map.day.mPayCount = String.valueOf(ni.getPayCount());
                            map.day.mIncomeCount = String.valueOf(ni.getIncomeCount());
                            map.day.mPayAmount = ni.getSZPayAmount();
                            map.day.mIncomeAmount = ni.getSZIncomeAmount();

                            BigDecimal bd_l = ni.getBalance();
                            map.amount = String.format(Locale.CHINA,
                                    (0 < bd_l.floatValue()) ? "+ %.02f" : "%.02f", bd_l);

                            map.tag = k;
                            map.show = EShowFold.getByFold(!checkUnfoldItem(k)).getName();
                            maps.add(map);
                        }
                        return maps;
                    });

            ll_rets.add(ret);
        }

        try {
            for(Future<LinkedList<MainAdapterItem>> ret : ll_rets) {
                mMainPara.addAll(ret.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void loadUI(Bundle bundle) {
        // adjust attach layout
        setAttachLayoutVisible(mAction.isDelete() || mBFilter ?
                View.VISIBLE : View.GONE);
        setFilterLayoutVisible(mBFilter ? View.VISIBLE : View.GONE);
        setAccpetGiveupLayoutVisible(mAction.isDelete() && !mBFilter ? View.VISIBLE : View.GONE);

        // load show data
        LinkedList<MainAdapterItem> n_mainpara;
        if (mBFilter) {
            n_mainpara = new LinkedList<>();
            for (MainAdapterItem i : mMainPara) {
                for (String ii : mFilterPara) {
                    if (i.tag.equals(ii)) {
                        n_mainpara.add(i);
                        break;
                    }
                }
            }
        } else {
            n_mainpara = mMainPara;
        }

        MainAdapter mSNAdapter = new MainAdapter(ContextUtil.getInstance(), n_mainpara);
        mLVShow.setAdapter(mSNAdapter);
        mSNAdapter.notifyDataSetChanged();
    }


    /// BEGIN PRIVATE
    /**
     * redraw UI
     */
    private void redrawUI() {
        // adjust attach layout
        setAttachLayoutVisible(mAction.isDelete() || mBFilter ?
                View.VISIBLE : View.GONE);
        setFilterLayoutVisible(mBFilter ? View.VISIBLE : View.GONE);
        setAccpetGiveupLayoutVisible(mAction.isDelete() && !mBFilter ? View.VISIBLE : View.GONE);

        MainAdapter cur_ap = (MainAdapter) mLVShow.getAdapter();
        cur_ap.notifyDataSetChanged();
    }


    /**
     * load UI
     * @param b_fully   load data if true
     */
    private void loadUIUtility(boolean b_fully) {

    }

    /**
     * reorder data
     */
    private void reorderData() {
        Collections.reverse(mMainPara);
    }
    /// END PRIVATE


    /**
     * main adapter
     */
    private class MainAdapter extends LVAdapter {
        private ArrayList<String> mALWaitDeleteDays = new ArrayList<>();

        private View.OnClickListener mCLAdapter = v -> {
            int vid = v.getId();
            int pos = mLVShow.getPositionForView(v);

            MainAdapterItem hm = UtilFun.cast(getItem(pos));
            String k_tag = hm.tag;
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

        MainAdapter(Context context, List<?> mdata)  {
            super(context, mdata, R.layout.li_daily_show);
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
            rl_del.setVisibility(mAction.isEdit() ? View.GONE : View.VISIBLE);
            rl_del.setOnClickListener(mCLAdapter);

            viewHolder.getView(R.id.rl_date).setOnClickListener(mCLAdapter);
            viewHolder.getView(R.id.vs_daily_info).setOnClickListener(mCLAdapter);

            // for show
            MainAdapterItem item = UtilFun.cast(getItem(position));
            viewHolder.setText(R.id.tv_month, item.month);
            viewHolder.setText(R.id.tv_day_number, item.dayNumber);
            viewHolder.setText(R.id.tv_day_in_week, item.dayInWeek);

            ValueShow vs = viewHolder.getView(R.id.vs_daily_info);
            HashMap<String, Object> hm_attr = new HashMap<>();
            hm_attr.put(ValueShow.ATTR_PAY_COUNT, item.day.mPayCount);
            hm_attr.put(ValueShow.ATTR_PAY_AMOUNT, item.day.mPayAmount);
            hm_attr.put(ValueShow.ATTR_INCOME_COUNT, item.day.mIncomeCount);
            hm_attr.put(ValueShow.ATTR_INCOME_AMOUNT, item.day.mIncomeAmount);
            vs.adjustAttribute(hm_attr);

            return root_view;
        }

        /**
         * get daily data need delete
         * @return      daily data
         */
        List<String> getWaitDeleteDays() {
            return mALWaitDeleteDays;
        }
    }
}


