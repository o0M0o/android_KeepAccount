package wxm.KeepAccount.ui.data.show.note.ListView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.OnClick;
import wxm.KeepAccount.ui.base.Adapter.LVAdapter;
import wxm.androidutil.util.FastViewHolder;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.BudgetItem;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.define.PayNoteItem;
import wxm.KeepAccount.ui.base.Helper.ResourceHelper;
import wxm.KeepAccount.ui.data.edit.Note.ACPreveiwAndEdit;
import wxm.KeepAccount.ui.data.show.note.ACNoteShow;
import wxm.KeepAccount.ui.data.show.note.ShowData.FilterShowEvent;
import wxm.KeepAccount.ui.utility.ListViewHelper;
import wxm.KeepAccount.utility.ContextUtil;
import wxm.KeepAccount.utility.ToolUtil;
import wxm.uilib.IconButton.IconButton;

/**
 * budget listview
 * Created by 123 on 2016/9/15.
 */
public class LVBudget extends LVBase {
    //for action
    private final static int ACTION_DELETE = 1;
    private final static int ACTION_EDIT = 2;
    private int mActionType = ACTION_EDIT;

    private boolean mBODownOrder = true;

    class MainAdapterItem   {
        public String  tag;
        public String  show;
        public String  id;

        public String  title;
        public String  note;
        public String  amount;

        MainAdapterItem()    {
            show = EShowFold.FOLD.getName();
        }
    }
    protected final LinkedList<MainAdapterItem> mMainPara;

    class SubAdapterItem   {
        public String  tag;
        public String  subTag;
        public String  id;

        public String  month;
        public String  time;
        public String  dayNumber;
        public String  dayInWeek;
        public String  note;
        public String  title;
        public String  amount;

        SubAdapterItem()    {
        }
    }
    protected final HashMap<String, LinkedList<SubAdapterItem>> mHMSubPara;

    class BudgetActionHelper extends ActionHelper    {
        @BindView(R.id.ib_sort)
        IconButton mIBSort;

        @BindView(R.id.ib_report)
        IconButton mIBReport;

        BudgetActionHelper() {
            super();
        }

        @Override
        protected void initActs() {
            mIBReport.setVisibility(View.GONE);

            mIBSort.setActIcon(mBODownOrder ?
                    R.drawable.ic_sort_up_1 : R.drawable.ic_sort_down_1);
            mIBSort.setActName(mBODownOrder ?
                    R.string.cn_sort_up_by_name : R.string.cn_sort_down_by_name);
        }

        @OnClick({R.id.ib_sort, R.id.ib_refresh})
        public void onActionClick(View v) {
            switch (v.getId()) {
                case R.id.ib_sort: {
                    mBODownOrder = !mBODownOrder;

                    mIBSort.setActIcon(mBODownOrder ? R.drawable.ic_sort_up_1 : R.drawable.ic_sort_down_1);
                    mIBSort.setActName(mBODownOrder ? R.string.cn_sort_up_by_name : R.string.cn_sort_down_by_name);

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
                        reloadView(v.getContext(), false);
                    }
                }
                break;

                case R.id.ib_add: {
                    ACNoteShow ac = getRootActivity();
                    Intent intent = new Intent(ac, ACPreveiwAndEdit.class);
                    intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, GlobalDef.STR_RECORD_BUDGET);
                    ac.startActivityForResult(intent, 1);
                }
                break;
            }
        }
    }


    public LVBudget() {
        super();
        LOG_TAG = "LVBudget";

        mMainPara = new LinkedList<>();
        mHMSubPara = new HashMap<>();
        mAHActs = new BudgetActionHelper();
    }

    /**
     * filter event
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFilterShowEvent(FilterShowEvent event) {
    }

    /**
     * addition action
     * @param v     action view
     */
    @OnClick({R.id.ib_sort, R.id.ib_refresh, R.id.ib_delete, R.id.ib_add})
    public void onActionClick(View v) {

    }


    @OnClick({R.id.bt_accpet, R.id.bt_giveup})
    public void onAccpetOrGiveupClick(View v) {
        int vid = v.getId();
        switch (vid) {
            case R.id.bt_accpet:
                if (ACTION_DELETE == mActionType) {
                    mActionType = ACTION_EDIT;

                    MainAdapter sad = UtilFun.cast_t(mLVShow.getAdapter());
                    List<Integer> ls_dels = sad.getWaitDeleteItems();
                    if (!UtilFun.ListIsNullOrEmpty(ls_dels)) {
                        ContextUtil.getBudgetUtility().removeDatas(ls_dels);
                    }
                }

                loadUIUtility(true);
                break;

            case R.id.bt_giveup:
                mActionType = ACTION_EDIT;
                loadUIUtility(true);
                break;
        }
    }

    @Override
    protected void loadUI() {
        loadUIUtility(false);
    }

    @Override
    protected void refreshData() {
        super.refreshData();

        Activity h = this.getActivity();
        Executors.newCachedThreadPool().submit(() -> {
            parseNotes();

            if(!(h.isDestroyed() || h.isFinishing()))   {
                h.runOnUiThread(() ->   {
                    loadUIUtility(true);
                });
            }
        });
    }

    /// BEGIN PRIVATE
    private void refreshAttachLayout() {
        setAttachLayoutVisible(ACTION_EDIT != mActionType ? View.VISIBLE : View.GONE);
        setFilterLayoutVisible(View.GONE);
        setAccpetGiveupLayoutVisible(ACTION_EDIT != mActionType ? View.VISIBLE : View.GONE);
    }

    /**
     * load UI
     * @param b_fully   if true then reload data
     */
    protected void loadUIUtility(boolean b_fully) {
        refreshAttachLayout();

        if (b_fully) {
            LinkedList<MainAdapterItem> n_mainpara = new LinkedList<>();
            n_mainpara.addAll(mMainPara);

            // 设置listview adapter
            MainAdapter mSNAdapter = new MainAdapter(ContextUtil.getInstance(), n_mainpara);
            mLVShow.setAdapter(mSNAdapter);
            mSNAdapter.notifyDataSetChanged();
        }
    }


    /**
     * reorder data
     */
    private void reorderData() {
        Collections.reverse(mMainPara);
    }

    /**
     * parse data
     */
    private void parseNotes() {
        mMainPara.clear();
        mHMSubPara.clear();

        HashMap<BudgetItem, List<PayNoteItem>> mHMData = ContextUtil.getBudgetUtility().getBudgetWithPayNote();
        Set<BudgetItem> set_bi = mHMData.keySet();
        ArrayList<BudgetItem> ls_bi = new ArrayList<>(set_bi);
        Collections.sort(ls_bi, (o1, o2) -> mBODownOrder ? o1.getName().compareTo(o2.getName())
                : o2.getName().compareTo(o1.getName()));
        for (BudgetItem i : ls_bi) {
            List<PayNoteItem> ls_pay = mHMData.get(i);
            String tag = String.valueOf(i.get_id());

            String show_str = String.format(Locale.CHINA,
                    "(总额)%.02f/(剩余)%.02f",
                    i.getAmount(), i.getRemainderAmount());

            MainAdapterItem map = new MainAdapterItem();
            map.title = i.getName();
            String nt = i.getNote();
            if (!UtilFun.StringIsNullOrEmpty(nt)) {
                map.note = nt;
            }
            map.amount = show_str;
            map.tag = tag;
            map.id = tag;
            map.show = EShowFold.getByFold(!checkUnfoldItem(tag)).getName();
            mMainPara.add(map);

            parseSub(tag, ls_pay);
        }
    }


    private void parseSub(String main_tag, List<PayNoteItem> ls_pay) {
        LinkedList<SubAdapterItem> cur_llhm = new LinkedList<>();
        if (!UtilFun.ListIsNullOrEmpty(ls_pay)) {
            Collections.sort(ls_pay, (o1, o2) -> o1.getTs().compareTo(o2.getTs()));

            for (PayNoteItem i : ls_pay) {
                String all_date = i.getTs().toString();
                SubAdapterItem map = new SubAdapterItem();
                map.month = all_date.substring(0, 7);

                String km = all_date.substring(8, 10);
                km = km.startsWith("0") ? km.replaceFirst("0", " ") : km;
                map.dayNumber = km;
                try {
                    Timestamp ts = ToolUtil.StringToTimestamp(all_date);
                    Calendar day = Calendar.getInstance();
                    day.setTimeInMillis(ts.getTime());
                    map.dayInWeek = ToolUtil.getDayInWeek(day.get(Calendar.DAY_OF_WEEK));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String nt = i.getNote();
                if (!UtilFun.StringIsNullOrEmpty(nt)) {
                    map.note = nt.length() > 10 ? nt.substring(0, 10) + "..." : nt;
                }

                map.time = all_date.substring(11, 16);
                map.title = i.getInfo();
                map.amount = i.getValToStr();
                map.tag = main_tag;
                map.subTag = i.getTs().toString().substring(0, 10);
                map.id = String.valueOf(i.getId());
                cur_llhm.add(map);
            }
        }
        mHMSubPara.put(main_tag, cur_llhm);
    }

    /**
     * load detail view
     * @param lv        UI
     * @param tag       data tag
     */
    private void load_detail_view(ListView lv, String tag) {
        LinkedList<SubAdapterItem> llhm = mHMSubPara.get(tag);
        if (!UtilFun.ListIsNullOrEmpty(llhm)) {
            SubAdapter mAdapter = new SubAdapter(getContext(), llhm);
            lv.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            ListViewHelper.setListViewHeightBasedOnChildren(lv);
        }
    }
    /// END PRIVATE

    /**
     * main adapter
     */
    protected class MainAdapter extends LVAdapter {
        private ArrayList<Integer> mALWaitDeleteItems = new ArrayList<>();

        private View.OnClickListener mCLAdapter = v -> {
            int vid = v.getId();
            int pos = mLVShow.getPositionForView(v);

            MainAdapterItem hm = UtilFun.cast(getItem(pos));

            switch (vid) {
                case R.id.rl_delete: {
                    String k_tag = hm.id;
                    Integer id = Integer.parseInt(k_tag);
                    if (mALWaitDeleteItems.contains(id)) {
                        mALWaitDeleteItems.remove((Object) id);
                        v.setBackgroundColor(ResourceHelper.mCRLVItemNoSel);
                    } else {
                        mALWaitDeleteItems.add(id);
                        v.setBackgroundColor(ResourceHelper.mCRLVItemSel);
                    }
                }
                break;

                case R.id.iv_edit: {
                    int tag_id = Integer.parseInt(hm.id);
                    Activity ac = getRootActivity();
                    Intent it = new Intent(ac, ACPreveiwAndEdit.class);
                    it.putExtra(GlobalDef.INTENT_LOAD_RECORD_ID, tag_id);
                    it.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, GlobalDef.STR_RECORD_BUDGET);
                    ac.startActivityForResult(it, 1);
                }
                break;
            }
        };


        MainAdapter(Context context, List<?> mdata)   {
            super(context, mdata, R.layout.li_budget_show);
        }

        public List<Integer> getWaitDeleteItems() {
            return mALWaitDeleteItems;
        }


        @Override
        public View getView(final int position, View view, ViewGroup arg2) {
            FastViewHolder viewHolder = FastViewHolder.get(getRootActivity(),
                    view, R.layout.li_budget_show);

            final MainAdapterItem hm = UtilFun.cast(getItem(position));
            final ListView lv = viewHolder.getView(R.id.lv_show_detail);
            final String tag = hm.tag;
            if (EShowFold.FOLD == EShowFold.getByName(hm.show)) {
                lv.setVisibility(View.GONE);
            } else {
                lv.setVisibility(View.VISIBLE);
                load_detail_view(lv, tag);
            }

            View.OnClickListener local_cl = v -> {
                boolean bf = EShowFold.FOLD == EShowFold.getByName(hm.show);
                hm.show = EShowFold.getByFold(!bf).getName();

                if (bf) {
                    lv.setVisibility(View.VISIBLE);
                    load_detail_view(lv, tag);

                    addUnfoldItem(tag);
                } else {
                    lv.setVisibility(View.GONE);

                    removeUnfoldItem(tag);
                }
            };

            // for background color
            ConstraintLayout rl = viewHolder.getView(R.id.cl_header);
            rl.setOnClickListener(local_cl);
            rl.setBackgroundColor(0 == position % 2 ?
                    ResourceHelper.mCRLVLineOne : ResourceHelper.mCRLVLineTwo);

            // for delete
            View rl_del = viewHolder.getView(R.id.rl_delete);
            rl_del.setVisibility(mActionType == ACTION_EDIT ? View.GONE : View.VISIBLE);
            rl_del.setOnClickListener(mCLAdapter);

            // for note
            String nt = hm.note;
            boolean b_nt = UtilFun.StringIsNullOrEmpty(nt);
            viewHolder.getView(R.id.iv_note).setVisibility(b_nt ? View.GONE : View.VISIBLE);
            viewHolder.getView(R.id.tv_budget_note).setVisibility(b_nt ? View.GONE : View.VISIBLE);
            if (!b_nt)
                viewHolder.setText(R.id.tv_budget_note, nt);

            viewHolder.setText(R.id.tv_budget_name, hm.title);
            viewHolder.setText(R.id.tv_budget_amount, hm.amount);

            // for action
            viewHolder.getView(R.id.iv_edit).setOnClickListener(mCLAdapter);
            return viewHolder.getConvertView();
        }
    }


    /**
     * sub adapter
     */
    private class SubAdapter extends LVAdapter {
        SubAdapter(Context context, List<?> sdata)    {
            super(context, sdata, R.layout.li_budget_show_detail);
        }

        @Override
        public View getView(final int position, View view, ViewGroup arg2) {
            FastViewHolder vh = FastViewHolder.get(getRootActivity(),
                    view, R.layout.li_budget_show_detail);

            SubAdapterItem hm = UtilFun.cast(getItem(position));
            // for note
            String nt = hm.note;
            if (UtilFun.StringIsNullOrEmpty(nt)) {
                vh.getView(R.id.rl_pay_note).setVisibility(View.GONE);
            }

            // for show
            vh.setText(R.id.tv_month, hm.month);
            vh.setText(R.id.tv_day_number, hm.dayNumber);
            vh.setText(R.id.tv_day_in_week, hm.dayInWeek);
            vh.setText(R.id.tv_pay_title, hm.title);
            vh.setText(R.id.tv_pay_amount, hm.amount);
            vh.setText(R.id.tv_pay_note, hm.note);
            vh.setText(R.id.tv_pay_time, hm.time);

            // for look action
            vh.getView(R.id.iv_look).setOnClickListener(v -> {
                ACNoteShow ac = getRootActivity();
                Intent intent;
                intent = new Intent(ac, ACPreveiwAndEdit.class);
                intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_ID, Integer.valueOf(hm.id));
                intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE,
                        GlobalDef.STR_RECORD_PAY);

                ac.startActivityForResult(intent, 1);
            });
            return vh.getConvertView();
        }
    }
}
