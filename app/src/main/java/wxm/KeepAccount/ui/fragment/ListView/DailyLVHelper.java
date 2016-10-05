package wxm.KeepAccount.ui.fragment.ListView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.wxm.andriodutillib.capricorn.RayMenu;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.db.BudgetItem;
import wxm.KeepAccount.Base.db.IncomeNoteItem;
import wxm.KeepAccount.Base.db.PayNoteItem;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acinterface.ACNoteShow;
import wxm.KeepAccount.ui.acutility.ACNoteEdit;

/**
 * 日数据视图辅助类
 * Created by 123 on 2016/9/10.
 */
public class DailyLVHelper extends ListViewBase
        implements OnClickListener {
    private final static String TAG = "DailyLVHelper";

    // for action
    private final static int ACTION_NONE    = 0;
    private final static int ACTION_DELETE  = 1;
    private final static int ACTION_EDIT    = 2;
    private int mActionType = ACTION_NONE;

    // delete data
    private final LinkedList<Integer> mDelPay;
    private final LinkedList<Integer> mDelIncome;

    // for raymenu
    private static final int[] ITEM_DRAWABLES = {
                                    R.drawable.ic_leave
                                    ,R.drawable.ic_switch
                                    ,R.drawable.ic_edit
                                    ,R.drawable.ic_delete
                                    ,R.drawable.ic_add};

    public DailyLVHelper()    {
        super();

        mDelPay    = new LinkedList<>();
        mDelIncome = new LinkedList<>();
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        mSelfView       = inflater.inflate(R.layout.lv_pager, container, false);
        mBFilter        = false;

        // init ray menu
        RayMenu rayMenu = UtilFun.cast(mSelfView.findViewById(R.id.rm_show_record));
        assert null != rayMenu;
        for (int ITEM_DRAWABLE : ITEM_DRAWABLES) {
            ImageView item = new ImageView(mSelfView.getContext());
            item.setImageResource(ITEM_DRAWABLE);

            final int position = ITEM_DRAWABLE;
            rayMenu.addItem(item, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnRayMenuClick(position);
                }
            });// Add a menu item
        }

        return mSelfView;
    }

    @Override
    public void loadView() {
        reloadData();
        refreshView();
    }

    @Override
    public void checkView() {
        if(getRootActivity().getDayNotesDirty())
            loadView();
    }

    @Override
    public void filterView(List<String> ls_tag) {
        if(null != ls_tag) {
            mBFilter = true;
            mFilterPara.clear();
            mFilterPara.addAll(ls_tag);
            refreshView();
        } else  {
            mBFilter = false;
            refreshView();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(resultCode)  {
            case AppGobalDef.INTRET_RECORD_ADD    :
            case AppGobalDef.INTRET_RECORD_MODIFY :
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
                if(ACTION_DELETE == mActionType) {
                    boolean dirty = false;
                    if(!ToolUtil.ListIsNullOrEmpty(mDelPay)) {
                        AppModel.getPayIncomeUtility().DeletePayNotes(mDelPay);
                        mDelPay.clear();
                        dirty = true;
                    }

                    if(!ToolUtil.ListIsNullOrEmpty(mDelIncome)) {
                        AppModel.getPayIncomeUtility().DeleteIncomeNotes(mDelIncome);
                        mDelIncome.clear();
                        dirty = true;
                    }

                    if(dirty) {
                        getRootActivity().setNotesDirty();
                        reloadData();
                    }

                    mActionType = ACTION_NONE;
                    refreshView();
                }

                break;

            case R.id.bt_giveup :
                mActionType = ACTION_NONE;
                mDelPay.clear();
                mDelIncome.clear();
                refreshAttachLayout();
                break;
        }
    }

    /**
     * 重新加载数据
     */
    private void reloadData() {
        mMainPara.clear();
        mHMSubPara.clear();

        HashMap<String, ArrayList<Object>> hm_data = getRootActivity().getNotesByDay();
        parseNotes(hm_data);
    }

    /**
     * 仅更新视图
     */
    @Override
    protected void refreshView()  {
        refreshAttachLayout();

        // load show data
        LinkedList<HashMap<String, String>> n_mainpara = new LinkedList<>();
        if(mBFilter) {
            for (HashMap<String, String> i : mMainPara) {
                String cur_tag = i.get(ListViewBase.MPARA_TAG);
                for (String ii : mFilterPara) {
                    if (cur_tag.equals(ii)) {
                        n_mainpara.add(i);
                        break;
                    }
                }
            }
        } else  {
            n_mainpara.addAll(mMainPara);
        }

        // 设置listview adapter
        ListView lv = UtilFun.cast(mSelfView.findViewById(R.id.tabvp_lv_main));
        SelfAdapter mSNAdapter = new SelfAdapter(mSelfView.getContext(), n_mainpara,
                new String[]{ListViewBase.MPARA_TITLE, ListViewBase.MPARA_ABSTRACT},
                new int[]{R.id.tv_title, R.id.tv_abstract});
        lv.setAdapter(mSNAdapter);
        mSNAdapter.notifyDataSetChanged();
    }

    private void refreshAttachLayout()    {
        setAttachLayoutVisible(ACTION_NONE != mActionType || mBFilter ?
                                View.VISIBLE : View.INVISIBLE);
        setFilterLayoutVisible(mBFilter ? View.VISIBLE : View.INVISIBLE);
        setAccpetGiveupLayoutVisible(ACTION_NONE != mActionType ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 初始化次级(详细数据)视图
     * @param v         主级视图
     * @param hm        主级视图附带数据
     */
    private void init_detail_view(View v, HashMap<String, String> hm) {
        // get sub para
        LinkedList<HashMap<String, String>> llhm = null;
        if(ListViewBase.MPARA_SHOW_UNFOLD.equals(hm.get(ListViewBase.MPARA_SHOW))) {
            llhm = mHMSubPara.get(hm.get(ListViewBase.MPARA_TAG));
        }

        if(null == llhm) {
            llhm = new LinkedList<>();
        }

        // init sub adapter
        ListView mLVShowDetail = UtilFun.cast(v.findViewById(R.id.lv_show_detail));
        assert null != mLVShowDetail;
        SelfSubAdapter mAdapter= new SelfSubAdapter( mSelfView.getContext(), mLVShowDetail,
                llhm, new String[]{ListViewBase.SPARA_TITLE, ListViewBase.SPARA_DETAIL},
                new int[]{R.id.tv_title, R.id.tv_detail});
        mLVShowDetail.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        ToolUtil.setListViewHeightBasedOnChildren(mLVShowDetail);
    }

    /**
     * raymenu点击事件
     * @param resid 点击发生的资源ID
     */
    private void OnRayMenuClick(int resid)  {
        switch (resid)  {
            case R.drawable.ic_add : {
                ACNoteShow ac = getRootActivity();
                Intent intent = new Intent(ac, ACNoteEdit.class);
                intent.putExtra(ACNoteEdit.PARA_ACTION, ACNoteEdit.LOAD_NOTE_ADD);

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                intent.putExtra(AppGobalDef.STR_RECORD_DATE,
                        String.format(Locale.CHINA
                                , "%d-%02d-%02d"
                                , cal.get(Calendar.YEAR)
                                , cal.get(Calendar.MONTH) + 1
                                , cal.get(Calendar.DAY_OF_MONTH)));

                ac.startActivityForResult(intent, 1);
            }
            break;

            case R.drawable.ic_switch : {
                getRootActivity().switchShow();
            }
            break;

            case R.drawable.ic_delete: {
                if (ACTION_DELETE != mActionType) {
                    mActionType = ACTION_DELETE;
                    refreshView();
                }
                break;
            }

            case R.drawable.ic_edit:     {
                if (ACTION_EDIT != mActionType) {
                    mActionType = ACTION_EDIT;
                    refreshView();
                }
            }
            break;


            case R.drawable.ic_leave :  {
                Activity ac = getRootActivity();
                ac.setResult(AppGobalDef.INTRET_USR_LOGOUT);
                ac.finish();
            }
            break;

            default:
                Log.e(TAG, "未处理的resid : " + resid);
                break;
        }
    }


    /**
     * 解析支出/收入数据
     * @param notes   待解析数据（日期---数据HashMap）
     */
    private void parseNotes(HashMap<String, ArrayList<Object>> notes)   {
        ArrayList<String> set_k = new ArrayList<>(notes.keySet());
        Collections.sort(set_k);
        for (String k : set_k) {
            String title = ToolUtil.FormatDateString(k);
            ArrayList<Object> v = notes.get(k);

            LinkedList<HashMap<String, String>> cur_llhm = new LinkedList<>();
            int pay_cout = 0;
            int income_cout = 0;
            BigDecimal pay_amount = BigDecimal.ZERO;
            BigDecimal income_amount = BigDecimal.ZERO;
            for (Object r : v) {
                HashMap<String, String> map = new HashMap<>();
                String show;
                String nt;
                if (r instanceof PayNoteItem) {
                    PayNoteItem pi = UtilFun.cast(r);
                    pay_cout += 1;
                    pay_amount = pay_amount.add(pi.getVal());

                    map.put(ListViewBase.SPARA_TITLE, pi.getInfo());
                    map.put(ListViewBase.SPARA_ID, String.valueOf(pi.getId()));
                    map.put(ListViewBase.SPARA_TAG, ListViewBase.SPARA_TAG_PAY);

                    BudgetItem bi = pi.getBudget();
                    if(null != bi)
                        show = String.format(Locale.CHINA, "金额 : %.02f\n使用预算 : %s",
                                    pi.getVal(), bi.getName());
                    else
                        show = String.format(Locale.CHINA, "金额 : %.02f", pi.getVal());

                    nt = pi.getNote();
                } else {
                    IncomeNoteItem ii = UtilFun.cast(r);
                    income_cout += 1;
                    income_amount = income_amount.add(ii.getVal());

                    map.put(ListViewBase.SPARA_TITLE, ii.getInfo());
                    map.put(ListViewBase.SPARA_ID, String.valueOf(ii.getId()));
                    map.put(ListViewBase.SPARA_TAG, ListViewBase.SPARA_TAG_INCOME);
                    show = String.format(Locale.CHINA, "金额 : %.02f", ii.getVal());

                    nt = ii.getNote();
                }

                if(!UtilFun.StringIsNullOrEmpty(nt)) {
                    show = String.format(Locale.CHINA, "%s\n备注 : %s", show ,nt);
                }

                map.put(ListViewBase.SPARA_DETAIL, show);
                map.put(ListViewBase.MPARA_TAG, title);
                cur_llhm.add(map);
            }

            String show_str =
                    String.format(Locale.CHINA,
                            "支出项 ： %d    总金额 ：%.02f\n收入项 ： %d    总金额 ：%.02f",
                            pay_cout, pay_amount, income_cout, income_amount);

            HashMap<String, String> map = new HashMap<>();
            map.put(ListViewBase.MPARA_TITLE, title);
            map.put(ListViewBase.MPARA_ABSTRACT, show_str);
            map.put(ListViewBase.MPARA_TAG, title);
            if(checkUnfoldItem(title))
                map.put(ListViewBase.MPARA_SHOW, ListViewBase.MPARA_SHOW_UNFOLD);
            else
                map.put(ListViewBase.MPARA_SHOW, ListViewBase.MPARA_SHOW_FOLD);

            mMainPara.add(map);

            mHMSubPara.put(title, cur_llhm);
        }
    }


    /**
     * 首级列表adapter
     */
    private class SelfAdapter extends SimpleAdapter {
        private final static String TAG = "SelfAdapter";

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
            View v = super.getView(position, view, arg2);
            if(null != v)   {
                //Log.i(TAG, "create view at pos = " + position);
                final HashMap<String, String> hm = UtilFun.cast(getItem(position));
                final View fv = v;
                final Resources res = v.getResources();
                ImageButton ib = UtilFun.cast(v.findViewById(R.id.ib_hide_show));
                ib.getBackground().setAlpha(0);
                ib.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Log.i(TAG, "onIbClick at pos = " + pos);
                        Resources res = v.getResources();
                        ImageButton ib = UtilFun.cast(v);
                        if(ListViewBase.MPARA_SHOW_FOLD.equals(hm.get(ListViewBase.MPARA_SHOW)))    {
                            hm.put(ListViewBase.MPARA_SHOW, ListViewBase.MPARA_SHOW_UNFOLD);
                            init_detail_view(fv, hm);
                            ib.setImageDrawable(res.getDrawable(R.drawable.ic_hide));
                            addUnfoldItem(hm.get(ListViewBase.MPARA_TAG));
                        }   else    {
                            hm.put(ListViewBase.MPARA_SHOW, ListViewBase.MPARA_SHOW_FOLD);
                            init_detail_view(fv, hm);
                            ib.setImageDrawable(res.getDrawable(R.drawable.ic_show));
                            removeUnfoldItem(hm.get(ListViewBase.MPARA_TAG));
                        }
                    }
                });

                if(ListViewBase.MPARA_SHOW_FOLD.equals(hm.get(ListViewBase.MPARA_SHOW)))    {
                    //init_detail_view(fv, hm);
                    ib.setImageDrawable(res.getDrawable(R.drawable.ic_hide));
                }   else    {
                    init_detail_view(fv, hm);
                    ib.setImageDrawable(res.getDrawable(R.drawable.ic_show));
                }

                if(0 == position % 2)   {
                    v.setBackgroundColor(res.getColor(R.color.lightsteelblue));
                } else  {
                    v.setBackgroundColor(res.getColor(R.color.paleturquoise));
                }
            }

            return v;
        }
    }


    /**
     * 次级列表adapter
     */
    private class SelfSubAdapter  extends SimpleAdapter implements OnClickListener {
        private final static String TAG = "SelfSubAdapter";
        private final ListView        mRootView;

        SelfSubAdapter(Context context, ListView fv,
                       List<? extends Map<String, ?>> sdata,
                       String[] from, int[] to) {
            super(context, sdata, R.layout.li_daily_show_detail, from, to);
            mRootView = fv;
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
                //Log.i(TAG, "create view at pos = " + position);
                HashMap<String, String> hm = UtilFun.cast(getItem(position));
                final Resources res = v.getResources();

                // for action
                ImageButton ib_action = UtilFun.cast(v.findViewById(R.id.ib_action));
                assert null != ib_action;
                if(ACTION_NONE == mActionType)    {
                    ib_action.setVisibility(View.INVISIBLE);
                }   else    {
                    ib_action.setVisibility(View.VISIBLE);

                    if(ACTION_DELETE == mActionType)
                        ib_action.setImageDrawable(res.getDrawable(R.drawable.ic_delete));
                    else
                        ib_action.setImageDrawable(res.getDrawable(R.drawable.ic_edit));

                    ib_action.getBackground().setAlpha(0);
                    ib_action.setOnClickListener(this);
                }

                // for image & background
                Bitmap nicon;
                if(ListViewBase.SPARA_TAG_PAY.equals(hm.get(ListViewBase.SPARA_TAG)))   {
                    v.setBackgroundColor(res.getColor(R.color.burlywood));
                    nicon = BitmapFactory.decodeResource(res, R.drawable.ic_show_pay);
                } else  {
                    v.setBackgroundColor(res.getColor(R.color.darkseagreen));
                    nicon = BitmapFactory.decodeResource(res, R.drawable.ic_show_income);
                }

                ImageView iv = UtilFun.cast(v.findViewById(R.id.iv_show));
                iv.setBackgroundColor(Color.TRANSPARENT);
                iv.setImageBitmap(nicon);
            }

            return v;
        }

        @Override
        public void onClick(View v) {
            int vid = v.getId();
            Resources res = v.getResources();

            int pos = mRootView.getPositionForView(v);
            HashMap<String, String> hm = UtilFun.cast(getItem(pos));
            String tp = hm.get(ListViewBase.SPARA_TAG);
            int did = Integer.parseInt(hm.get(ListViewBase.SPARA_ID));

            switch (vid)    {
                case R.id.ib_action:       {
                    ImageButton ib_action = UtilFun.cast(v);
                    if(ACTION_DELETE == mActionType)    {
                        if(ib_action.isSelected())  {
                            if (ListViewBase.SPARA_TAG_PAY.equals(tp)) {
                                mDelPay.removeFirstOccurrence(did);
                            } else {
                                mDelIncome.removeFirstOccurrence(did);
                            }

                            ib_action.getBackground().setAlpha(0);
                        }   else    {
                            if (ListViewBase.SPARA_TAG_PAY.equals(tp)) {
                                mDelPay.add(did);
                            } else {
                                mDelIncome.add(did);
                            }

                            ib_action.getBackground().setAlpha(255);
                            ib_action.setBackgroundColor(res.getColor(R.color.red));
                        }

                        ib_action.setSelected(!ib_action.isSelected());
                    } else  {
                        ACNoteShow ac = getRootActivity();
                        Intent intent = new Intent(ac, ACNoteEdit.class);
                        intent.putExtra(ACNoteEdit.PARA_ACTION, ACNoteEdit.LOAD_NOTE_MODIFY);
                        if (ListViewBase.SPARA_TAG_PAY.equals(tp)) {
                            intent.putExtra(ACNoteEdit.PARA_NOTE_PAY, did);
                        } else {
                            intent.putExtra(ACNoteEdit.PARA_NOTE_INCOME, did);
                        }

                        ac.startActivityForResult(intent, 1);
                    }
                }
                break;
            }
        }
    }
}
