package wxm.KeepAccount.ui.data.show.note.ListView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

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

import butterknife.BindView;
import butterknife.OnClick;
import wxm.KeepAccount.ui.base.Adapter.LVAdapter;
import wxm.androidutil.Dialog.DlgOKOrNOBase;
import wxm.androidutil.ViewHolder.ViewDataHolder;
import wxm.androidutil.ViewHolder.ViewHolder;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.define.INote;
import wxm.KeepAccount.ui.base.Helper.ResourceHelper;
import wxm.KeepAccount.ui.data.edit.NoteCreate.ACNoteCreate;
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
        public String  show;

        public String  year;
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

    class ItemHolder  extends ViewDataHolder<String, MainAdapterItem> {
        public ItemHolder(String tag)   {
            super(tag);
        }

        @Override
        protected MainAdapterItem getDataByTag(String tag) {
            Calendar cl_day = Calendar.getInstance();
            NoteShowInfo ni = NoteDataHelper.Companion.getInfoByDay(tag);

            MainAdapterItem item = new MainAdapterItem();
            item.year = tag.substring(0, 4);
            item.month = tag.substring(5, 7);
            item.month = item.month.startsWith("0") ?
                    item.month.replaceFirst("0", " ") : item.month;

            String km = tag.substring(8, 10);
            km = km.startsWith("0") ? km.replaceFirst("0", "") : km;
            item.dayNumber = km;

            int year = Integer.valueOf(tag.substring(0, 4));
            int month = Integer.valueOf(tag.substring(5, 7));
            int day = Integer.valueOf(km);
            cl_day.set(year, month - 1, day);
            item.dayInWeek = ToolUtil.INSTANCE.getDayInWeek(cl_day.get(Calendar.DAY_OF_WEEK));

            item.day.mPayCount = String.valueOf(ni.getPayCount());
            item.day.mIncomeCount = String.valueOf(ni.getIncomeCount());
            item.day.mPayAmount = ni.getSzPayAmount();
            item.day.mIncomeAmount = ni.getSzIncomeAmount();

            BigDecimal bd_l = ni.getBalance();
            item.amount = String.format(Locale.CHINA,
                    (0 < bd_l.floatValue()) ? "+ %.02f" : "%.02f", bd_l);

            item.show = EShowFold.getByFold(!checkUnfoldItem(tag)).getName();

            return item;
        }
    }
    protected final LinkedList<ItemHolder> mMainPara;

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
        @OnClick({R.id.ib_sort, R.id.ib_refresh, R.id.ib_delete, R.id.ib_add, R.id.ib_report})
        public void onActionClick(View v) {
            switch (v.getId()) {
                case R.id.ib_sort: {
                    mBTimeDownOrder = !mBTimeDownOrder;

                    mIBSort.setActIcon(mBTimeDownOrder ? R.drawable.ic_sort_up_1 : R.drawable.ic_sort_down_1);
                    mIBSort.setActName(mBTimeDownOrder ? R.string.cn_sort_up_by_time : R.string.cn_sort_down_by_time);

                    reorderData();
                    reloadUI();
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
                    } else  {
                        doDelete();
                        mAction = EAction.EDIT;
                    }

                    redrawUI();
                }
                break;

                case R.id.ib_add: {
                    ACNoteShow ac = getRootActivity();
                    Intent intent = new Intent(ac, ACNoteCreate.class);
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

    @Override
    protected boolean isUseEventBus() {
        return true;
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
            reloadUI();
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
                    doDelete();

                    mAction = EAction.EDIT;
                    redrawUI();
                }
                break;

            case R.id.bt_giveup:
                mAction = EAction.EDIT;
                redrawUI();
                break;

            case R.id.bt_giveup_filter:
                mBFilter = false;
                reloadUI();
                break;
        }
    }

    @Override
    protected void initUI(Bundle bundle) {
        super.initUI(bundle);

        showLoadingProgress(true);
        ToolUtil.INSTANCE.runInBackground(getActivity(),
                () -> {
                    mMainPara.clear();
                    // for day
                    List<String> set_k_d = NoteDataHelper.Companion.getNotesDays();
                    Collections.sort(set_k_d, (o1, o2) -> !mBTimeDownOrder ? o1.compareTo(o2) : o2.compareTo(o1));

                    for(String k : set_k_d) {
                        mMainPara.add(new ItemHolder(k));
                    }
                },
                () -> {
                    showLoadingProgress(false);
                    loadUI(bundle);
                });
    }


    @Override
    protected void loadUI(Bundle bundle) {
        // adjust attach layout
        setAttachLayoutVisible(mAction.isDelete() || mBFilter ?
                View.VISIBLE : View.GONE);
        setFilterLayoutVisible(mBFilter ? View.VISIBLE : View.GONE);
        setAccpetGiveupLayoutVisible(mAction.isDelete() && !mBFilter ? View.VISIBLE : View.GONE);

        // load show data
        LinkedList<ItemHolder> n_mainpara;
        if (mBFilter) {
            n_mainpara = new LinkedList<>();
            for (ItemHolder i : mMainPara) {
                for (String ii : mFilterPara) {
                    if (i.getTag().equals(ii)) {
                        n_mainpara.add(i);
                        break;
                    }
                }
            }
        } else {
            n_mainpara = mMainPara;
        }

        MainAdapter mSNAdapter = new MainAdapter(ContextUtil.Companion.getInstance(), n_mainpara);
        mLVShow.setAdapter(mSNAdapter);
        mSNAdapter.notifyDataSetChanged();
    }


    /// BEGIN PRIVATE
    private void doDelete() {
        ArrayList<Integer> al_i = new ArrayList<>();
        ArrayList<Integer> al_p = new ArrayList<>();

        MainAdapter cur_ap = (MainAdapter) mLVShow.getAdapter();
        List<String> ls_days = cur_ap.getWaitDeleteDays();
        HashMap<String, ArrayList<INote>> hm =
                NoteDataHelper.Companion.getInstance().getNotesForDay();
        if(null == hm)
            return;

        for (String day : ls_days) {
            List<INote> ls_n = hm.get(day);
            for (INote n : ls_n) {
                if (n.isPayNote())
                    al_p.add(n.getId());
                else
                    al_i.add(n.getId());
            }
        }

        if (!al_i.isEmpty()) {
            ContextUtil.Companion.getPayIncomeUtility().deleteIncomeNotes(al_i);
        }

        if (!al_p.isEmpty()) {
            ContextUtil.Companion.getPayIncomeUtility().deletePayNotes(al_p);
        }
    }

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
        private static final int SELF_TAG_ID = 0;

        private View.OnClickListener mCLAdapter = v -> {
            int pos = mLVShow.getPositionForView(v);

            ItemHolder hm = UtilFun.cast(getItem(pos));
            ACNoteShow ac = getRootActivity();
            Intent it = new Intent(ac, ACDailyDetail.class);
            it.putExtra(ACDailyDetail.K_HOTDAY, hm.getTag());
            ac.startActivity(it);
        };

        MainAdapter(Context context, List<?> mdata)  {
            super(context, mdata, R.layout.li_daily_show);
        }

        @Override
        public View getView(final int position, View view, ViewGroup arg2) {
            ViewHolder viewHolder = ViewHolder.get(getContext(), view, R.layout.li_daily_show);
            CheckBox cb = viewHolder.getView(R.id.cb_del);
            if(!mAction.isDelete()) {
                cb.setChecked(false);
                cb.setVisibility(View.GONE);
            } else  {
                cb.setVisibility(View.VISIBLE);
            }

            View root_view = viewHolder.getConvertView();
            if(null == viewHolder.getSelfTag(SELF_TAG_ID))   {
                viewHolder.setSelfTag(SELF_TAG_ID, new Object());

                ItemHolder it = UtilFun.cast(getItem(position));
                cb.setTag(it.getTag());

                root_view.setBackgroundColor(0 == position % 2 ?
                        ResourceHelper.INSTANCE.getMCRLVLineOne() : ResourceHelper.INSTANCE.getMCRLVLineTwo());
                root_view.setOnClickListener(mCLAdapter);

                ItemHolder itPrv = position > 0 ? UtilFun.cast(getItem(position - 1)) : null;
                initItemShow(viewHolder, it.getData(), null != itPrv ? itPrv.getData() : null);
            }

            return root_view;
        }

        private void initItemShow(ViewHolder vh, MainAdapterItem item, MainAdapterItem prvItem) {
            if(null == prvItem || !prvItem.year.equals(item.year)) {
                vh.setText(R.id.tv_year_number, item.year);
            } else  {
                //vh.setText(R.id.tv_year_number, item.year);
                vh.getView(R.id.tv_year_number).setVisibility(View.INVISIBLE);
            }

            vh.setText(R.id.tv_month_number,
                    item.month.startsWith("0") ? item.month.substring(1) : item.month);
            vh.setText(R.id.tv_day_number, item.dayNumber);
            vh.setText(R.id.tv_day_in_week, item.dayInWeek);

            ValueShow vs = vh.getView(R.id.vs_daily_info);
            HashMap<String, Object> hm_attr = new HashMap<>();
            hm_attr.put(ValueShow.ATTR_PAY_COUNT, item.day.mPayCount);
            hm_attr.put(ValueShow.ATTR_PAY_AMOUNT, item.day.mPayAmount);
            hm_attr.put(ValueShow.ATTR_INCOME_COUNT, item.day.mIncomeCount);
            hm_attr.put(ValueShow.ATTR_INCOME_AMOUNT, item.day.mIncomeAmount);
            vs.adjustAttribute(hm_attr);
        }

        /**
         * get daily data need delete
         * @return      daily data
         */
        List<String> getWaitDeleteDays() {
            if(!mAction.isDelete()) {
                return Collections.emptyList();
            }

            List<String> ret = new LinkedList<>();
            int count = mLVShow.getChildCount();
            for(int pos = 0; pos < count; pos++)    {
                View v = mLVShow.getChildAt(pos);
                CheckBox cb = v.findViewById(R.id.cb_del);
                if(cb.isChecked())  {
                    ret.add((String) cb.getTag());
                }
            }
            return ret;
        }
    }
}


