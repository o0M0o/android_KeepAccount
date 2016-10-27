package wxm.KeepAccount.ui.fragment.ListView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.db.BudgetItem;
import wxm.KeepAccount.Base.db.PayNoteItem;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acinterface.ACNoteShow;
import wxm.KeepAccount.ui.acutility.ACBudgetEdit;
import wxm.KeepAccount.ui.fragment.base.LVShowDataBase;

/**
 * 预算数据视图辅助类
 * Created by 123 on 2016/9/15.
 */
public class BudgetViewHelper  extends LVShowDataBase {
    private final static String TAG = "BudgetViewHelper";

    //for action
    private final static int ACTION_NONE    = 0;
    private final static int ACTION_DELETE  = 1;
    private final static int ACTION_EDIT    = 2;
    private int mActionType = ACTION_NONE;

    // for delete
    private final LinkedList<Integer> mLLDelBudget    = new LinkedList<>();
    //private LinkedList<View>    mLLDelVW        = new LinkedList<>();

    // original data
    private HashMap<BudgetItem, List<PayNoteItem>>  mHMData;

    // for expand or hide actions
    private ImageView   mIVActions;
    private GridLayout mGLActions;
    private boolean     mBActionExpand;
    private Drawable mDAExpand;
    private Drawable    mDAHide;

    public BudgetViewHelper()    {
        super();
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        mSelfView       = inflater.inflate(R.layout.lv_newpager, container, false);
        mBFilter        = false;

        // for action expand
        Resources res = mSelfView.getResources();
        mDAExpand = res.getDrawable(R.drawable.ic_to_up);
        mDAHide = res.getDrawable(R.drawable.ic_to_down);

        mIVActions = UtilFun.cast_t(mSelfView.findViewById(R.id.iv_expand));
        mGLActions = UtilFun.cast_t(mSelfView.findViewById(R.id.rl_action));

        mIVActions.setImageDrawable(mDAExpand);
        setLayoutVisible(mGLActions, View.INVISIBLE);

        mIVActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBActionExpand = !mBActionExpand;
                if(mBActionExpand)  {
                    mIVActions.setImageDrawable(mDAHide);
                    setLayoutVisible(mGLActions, View.VISIBLE);
                } else  {
                    mIVActions.setImageDrawable(mDAExpand);
                    setLayoutVisible(mGLActions, View.INVISIBLE);
                }
            }
        });

        RelativeLayout rl = UtilFun.cast_t(mSelfView.findViewById(R.id.rl_act_add));
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ACNoteShow ac = getRootActivity();
                Intent intent = new Intent(ac, ACBudgetEdit.class);
                ac.startActivityForResult(intent, 1);
            }
        });

        rl = UtilFun.cast_t(mSelfView.findViewById(R.id.rl_act_delete));
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionType = ACTION_DELETE;
                refreshView();
            }
        });

        rl = UtilFun.cast_t(mSelfView.findViewById(R.id.rl_act_refresh));
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionType = ACTION_EDIT;
                reloadView(v.getContext(), true);
            }
        });

        return mSelfView;
    }


    @Override
    public void loadView() {
        if(AppModel.getPayIncomeUtility().getDataLastChangeTime().after(mTSLastLoadViewTime)) {
            reloadData();
        }

        refreshView();
        mTSLastLoadViewTime.setTime(Calendar.getInstance().getTimeInMillis());
    }

    @Override
    public void filterView(List<String> ls_tag) {
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(resultCode)  {
            case AppGobalDef.INTRET_SURE:
                reloadData();
                refreshView();
                break;

            default:
                Log.d(TAG, String.format("不处理的resultCode(%d)!", resultCode));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int vid = v.getId();
        switch (vid)    {
            case R.id.bt_accpet :
                if(ACTION_DELETE == mActionType)    {
                    if(!ToolUtil.ListIsNullOrEmpty(mLLDelBudget)) {
                        AppModel.getBudgetUtility().DeleteBudgetById(mLLDelBudget);
                    }

                    reloadData();
                }

                mActionType = ACTION_DELETE;
                mLLDelBudget.clear();
                refreshAttachLayout();
                break;

            case R.id.bt_giveup :
                mActionType = ACTION_NONE;
                mLLDelBudget.clear();
                refreshAttachLayout();
                break;
        }
    }

    @Override
    protected void refreshView() {
        refreshAttachLayout();

        // 设置listview adapter
        ListView lv = UtilFun.cast(mSelfView.findViewById(R.id.lv_show));
        SelfAdapter mSNAdapter = new SelfAdapter(mSelfView.getContext(), lv, mMainPara,
                new String[]{K_TITLE, K_ABSTRACT},
                new int[]{R.id.tv_title, R.id.tv_abstract});
        lv.setAdapter(mSNAdapter);
        mSNAdapter.notifyDataSetChanged();
    }


    private void refreshAttachLayout()    {
        setAttachLayoutVisible(ACTION_NONE != mActionType ? View.VISIBLE : View.INVISIBLE);
        setFilterLayoutVisible(View.INVISIBLE);
        setAccpetGiveupLayoutVisible(ACTION_NONE != mActionType ? View.VISIBLE : View.INVISIBLE);
    }


    /**
     * 重新加载数据
     */
    private void reloadData() {
        mMainPara.clear();
        mHMSubPara.clear();

        // format output
        mHMData = AppModel.getBudgetUtility().GetBudgetWithPayNote();
        parseNotes();
    }

    /**
     * 解析数据
     */
    private void parseNotes()   {
        mMainPara.clear();
        mHMSubPara.clear();

        for (BudgetItem i : mHMData.keySet()) {
            List<PayNoteItem> ls_pay = mHMData.get(i);
            String tag = String.valueOf(i.get_id());

            String show_str = String.format(Locale.CHINA,
                    "总额度  : %.02f\n剩余额度 : %.02f",
                    i.getAmount(), i.getRemainderAmount());
            String nt = i.getNote();
            if(!UtilFun.StringIsNullOrEmpty(nt))    {
                show_str = String.format(Locale.CHINA,
                        "%s\n备注 : %s",
                        show_str, nt);
            }


            HashMap<String, String> map = new HashMap<>();
            map.put(K_TITLE, i.getName());
            map.put(K_ABSTRACT, show_str);
            map.put(K_TAG, tag);
            if(checkUnfoldItem(tag))
                map.put(K_SHOW, V_SHOW_UNFOLD);
            else
                map.put(K_SHOW, V_SHOW_FOLD);
            mMainPara.add(map);

            parseSub(tag, ls_pay);
        }
    }


    private void parseSub(String main_tag, List<PayNoteItem> ls_pay)   {
        LinkedList<HashMap<String, String>> cur_llhm = new LinkedList<>();
        if(!ToolUtil.ListIsNullOrEmpty(ls_pay)) {
            Collections.sort(ls_pay, new Comparator<PayNoteItem>() {
                @Override
                public int compare(PayNoteItem o1, PayNoteItem o2) {
                    return o1.getTs().compareTo(o2.getTs());
                }
            });

            for(PayNoteItem i : ls_pay)     {
                String all_date = i.getTs().toString();
                HashMap<String, String> map = new HashMap<>();
                map.put(K_MONTH, all_date.substring(0, 7));

                String km = all_date.substring(8, 10);
                km = km.startsWith("0") ? km.replaceFirst("0", " ") : km;
                map.put(K_DAY_NUMEBER, km);
                try {
                    Timestamp ts = ToolUtil.StringToTimestamp(all_date);
                    Calendar day = Calendar.getInstance();
                    day.setTimeInMillis(ts.getTime());
                    map.put(K_DAY_IN_WEEK, getDayInWeek(day.get(Calendar.DAY_OF_WEEK)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                map.put(K_TITLE, i.getInfo());
                map.put(K_AMOUNT, String.format(Locale.CHINA, "%.02f", i.getVal()));
                map.put(K_TAG, main_tag);
                map.put(K_SUB_TAG, i.getTs().toString().substring(0, 10));
                map.put(K_ID, String.valueOf(i.getId()));
                cur_llhm.add(map);
            }
        }
        mHMSubPara.put(main_tag, cur_llhm);
    }


    /**
     * 初始化次级(详细数据)视图
     * @param v         主级视图
     * @param hm        主级视图附带数据
     */
    private void init_detail_view(View v, HashMap<String, String> hm) {
        // get sub para
        LinkedList<HashMap<String, String>> llhm = null;
        if(V_SHOW_UNFOLD.equals(hm.get(K_SHOW))) {
            llhm = mHMSubPara.get(hm.get(K_TAG));
        }

        if(null == llhm) {
            llhm = new LinkedList<>();
        }

        // init sub adapter
        ListView mLVShowDetail = UtilFun.cast(v.findViewById(R.id.lv_show_detail));
        assert null != mLVShowDetail;
        SelfSubAdapter mAdapter= new SelfSubAdapter( mSelfView.getContext(), mLVShowDetail,
                                        llhm, new String[]{}, new int[]{});
        mLVShowDetail.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        ToolUtil.setListViewHeightBasedOnChildren(mLVShowDetail);
    }

    /**
     * 首级adapter
     */
    private class SelfAdapter extends SimpleAdapter  {
        private final static String TAG = "SelfAdapter";
        private final ListView        mRootView;

        private int         mClOne;
        private int         mClTwo;

        private int         mClSel;
        private int         mClNoSel;

        private Drawable    mDAFold;
        private Drawable    mDAUnFold;
        private Drawable    mDADelete;
        private Drawable    mDAEdit;

        SelfAdapter(Context context, ListView fv,
                    List<? extends Map<String, ?>> mdata,
                    String[] from, int[] to) {
            super(context, mdata, R.layout.li_budget_show, from, to);
            mRootView = fv;

            Resources res   = context.getResources();
            mClOne = res.getColor(R.color.color_1);
            mClTwo = res.getColor(R.color.color_2);

            mClSel = res.getColor(R.color.powderblue);
            mClNoSel = res.getColor(R.color.trans_1);

            mDAFold = res.getDrawable(R.drawable.ic_hide_1);
            mDAUnFold = res.getDrawable(R.drawable.ic_show_1);
            mDADelete = res.getDrawable(R.drawable.ic_delete_1);
            mDAEdit = res.getDrawable(R.drawable.ic_edit);
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
            if(null != v)   {
                // for background color
                final HashMap<String, String> hm = UtilFun.cast(getItem(position));
                final View fv = v;

                RelativeLayout rl = UtilFun.cast_t(v.findViewById(R.id.rl_header));
                rl.setBackgroundColor(0 == position % 2 ? mClOne : mClTwo);

                // for fold/unfold
                ImageButton ib = UtilFun.cast(v.findViewById(R.id.ib_hide_show));
                ib.getBackground().setAlpha(0);
                ib.setImageDrawable(V_SHOW_FOLD.equals(hm.get(K_SHOW)) ?  mDAFold : mDAUnFold);
                ib.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageButton ib = UtilFun.cast(v);
                        boolean bf = V_SHOW_FOLD.equals(hm.get(K_SHOW));
                        hm.put(K_SHOW, bf ? V_SHOW_UNFOLD : V_SHOW_FOLD);
                        init_detail_view(fv, hm);

                        ib.setImageDrawable(bf ? mDAFold : mDAUnFold);
                        if(bf)
                            addUnfoldItem(hm.get(K_TAG));
                        else
                            removeUnfoldItem(hm.get(K_TAG));
                    }
                });

                // for action
                ImageButton ib_action = UtilFun.cast(v.findViewById(R.id.ib_action));
                if(ACTION_NONE == mActionType)    {
                    ib_action.setVisibility(View.INVISIBLE);
                }   else    {
                    ib_action.setVisibility(View.VISIBLE);
                    ib_action.setImageDrawable(ACTION_DELETE == mActionType ? mDADelete : mDAEdit);

                    ib_action.getBackground().setAlpha(0);
                }
                ib_action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageButton ib_action = UtilFun.cast(v);
                        int tag_id = Integer.parseInt(hm.get(K_TAG));
                        if(ACTION_DELETE == mActionType)    {
                            if(mLLDelBudget.contains(tag_id))  {
                                mLLDelBudget.remove((Object)tag_id);
                                ib_action.getBackground().setAlpha(0);
                                ib_action.setBackgroundColor(mClNoSel);
                            }   else    {
                                mLLDelBudget.add(tag_id);
                                ib_action.getBackground().setAlpha(255);
                                ib_action.setBackgroundColor(mClSel);
                            }
                        } else  {
                            Activity ac = getRootActivity();
                            Intent it = new Intent(ac, ACBudgetEdit.class);
                            it.putExtra(ACBudgetEdit.INTENT_LOAD_BUDGETID, tag_id);
                            ac.startActivityForResult(it, 1);
                        }
                    }
                });
            }

            return v;
        }
    }


    /**
     * 次级adapter
     */
    private class SelfSubAdapter  extends SimpleAdapter {
        private final static String TAG = "SelfSubAdapter";
        private final ListView   mRootView;

        private int         mCLSel;
        private int         mCLNoSel;

        SelfSubAdapter(Context context, ListView fv,
                       List<? extends Map<String, ?>> sdata,
                       String[] from, int[] to) {
            super(context, sdata, R.layout.li_budget_show_detail, from, to);
            mRootView = fv;

            Resources res   = context.getResources();
            mCLSel = res.getColor(R.color.powderblue);
            mCLNoSel = res.getColor(R.color.trans_full);
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
            if(null != v)   {
                final HashMap<String, String> hm = UtilFun.cast(getItem(position));
                final String sub_tag = hm.get(K_SUB_TAG);

                // for date
                TextView tv = UtilFun.cast_t(v.findViewById(R.id.tv_month));
                tv.setText(hm.get(K_MONTH));

                tv = UtilFun.cast_t(v.findViewById(R.id.tv_day_number));
                tv.setText(hm.get(K_DAY_NUMEBER));

                tv = UtilFun.cast_t(v.findViewById(R.id.tv_day_in_week));
                tv.setText(hm.get(K_DAY_IN_WEEK));

                // for pay
                tv = UtilFun.cast_t(v.findViewById(R.id.tv_pay_title));
                tv.setText(hm.get(K_TITLE));

                tv = UtilFun.cast_t(v.findViewById(R.id.tv_pay_amount));
                tv.setText(hm.get(K_AMOUNT));

                ImageView iv = UtilFun.cast_t(v.findViewById(R.id.iv_look));
                iv.setBackgroundColor(mLLSubFilter.contains(sub_tag) ? mCLSel : mCLNoSel);
                //iv.getBackground().setAlpha(mLLSubFilter.contains(sub_tag) ? 255 : 0);
                /*
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageView ibv = UtilFun.cast_t(v);
                        boolean bsel = mLLSubFilter.contains(sub_tag);
                        ibv.getBackground().setAlpha(!bsel ? 255 : 0);
                        if(!bsel) {
                            mLLSubFilter.add(sub_tag);
                            mLLSubFilterVW.add(v);

                            if(!mBSelectSubFilter) {
                                mBSelectSubFilter = true;
                                refreshAttachLayout();
                            }
                        }   else    {
                            mLLSubFilter.remove(sub_tag);
                            mLLSubFilterVW.remove(v);

                            if(mLLSubFilter.isEmpty()) {
                                mLLSubFilterVW.clear();;
                                mBSelectSubFilter = false;
                                refreshAttachLayout();
                            }
                        }
                    }
                });
                */
            }

            return v;
        }
    }
}
