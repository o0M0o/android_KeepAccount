package wxm.KeepAccount.ui.fragment.ShowData;

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
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
import wxm.KeepAccount.Base.db.PayNoteItem;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acinterface.ACNoteShow;
import wxm.KeepAccount.ui.acutility.ACBudgetEdit;

/**
 * 预算数据视图辅助类
 * Created by 123 on 2016/9/15.
 */
public class BudgetViewHelper  extends LVViewHelperBase {
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

    private static final int[] ITEM_DRAWABLES = {
            R.drawable.ic_edit
            ,R.drawable.ic_delete
            ,R.drawable.ic_refrash
            ,R.drawable.ic_add};

    public BudgetViewHelper()    {
        super();
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
            rayMenu.addItem(item, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnRayMenuClick(position);
                }
            });
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
        loadView();
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
        ListView lv = UtilFun.cast(mSelfView.findViewById(R.id.tabvp_lv_main));
        SelfAdapter mSNAdapter = new SelfAdapter(mSelfView.getContext(), lv, mMainPara,
                new String[]{STListViewFragment.MPARA_TITLE, STListViewFragment.MPARA_ABSTRACT},
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
                    "金额 : %.02f\n剩余总额 : %.02f",
                    i.getAmount(), i.getRemainderAmount());
            String nt = i.getNote();
            if(!UtilFun.StringIsNullOrEmpty(nt))    {
                show_str = String.format(Locale.CHINA,
                        "%s\n备注 : %s",
                        show_str, nt);
            }

            HashMap<String, String> map = new HashMap<>();
            map.put(STListViewFragment.MPARA_TITLE, i.getName());
            map.put(STListViewFragment.MPARA_ABSTRACT, show_str);
            map.put(STListViewFragment.MPARA_TAG, tag);
            if(checkUnfoldItem(tag))
                map.put(STListViewFragment.MPARA_SHOW, STListViewFragment.MPARA_SHOW_UNFOLD);
            else
                map.put(STListViewFragment.MPARA_SHOW, STListViewFragment.MPARA_SHOW_FOLD);
            mMainPara.add(map);

            parseSub(tag, ls_pay);
        }
    }


    private void parseSub(String main_tag, List<PayNoteItem> ls_pay)   {
        LinkedList<HashMap<String, String>> cur_llhm = new LinkedList<>();
        if(!ToolUtil.ListIsNullOrEmpty(ls_pay)) {
            for(PayNoteItem i : ls_pay)     {
                String sub_tag = String.valueOf(i.getId());
                String title = ToolUtil.FormatDateString(i.getTs().toString().substring(0, 10));
                String show = String.format(Locale.CHINA,
                        "%s\n金额 : %.02f", i.getInfo(),i.getVal());
                String nt = i.getNote();
                if(!UtilFun.StringIsNullOrEmpty(nt))    {
                    show = String.format(Locale.CHINA,
                            "%s\n备注 : %s", show, nt);
                }

                HashMap<String, String> map = new HashMap<>();
                map.put(STListViewFragment.SPARA_TITLE, title);
                map.put(STListViewFragment.SPARA_DETAIL, show);
                map.put(STListViewFragment.MPARA_TAG, main_tag);
                map.put(STListViewFragment.SPARA_TAG, sub_tag);
                map.put(STListViewFragment.SPARA_ID, sub_tag);
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
        if(STListViewFragment.MPARA_SHOW_UNFOLD.equals(hm.get(STListViewFragment.MPARA_SHOW))) {
            llhm = mHMSubPara.get(hm.get(STListViewFragment.MPARA_TAG));
        }

        if(null == llhm) {
            llhm = new LinkedList<>();
        }

        // init sub adapter
        ListView mLVShowDetail = UtilFun.cast(v.findViewById(R.id.lv_show_detail));
        assert null != mLVShowDetail;
        SelfSubAdapter mAdapter= new SelfSubAdapter( mSelfView.getContext(), mLVShowDetail,
                llhm, new String[]{STListViewFragment.SPARA_TITLE, STListViewFragment.SPARA_DETAIL},
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
            case R.drawable.ic_add :
                ACNoteShow ac = getRootActivity();
                Intent intent = new Intent(ac, ACBudgetEdit.class);
                ac.startActivityForResult(intent, 1);
                break;

            case R.drawable.ic_delete:
                if(ACTION_DELETE != mActionType) {
                    mActionType = ACTION_DELETE;
                    refreshView();
                }
                break;

            case R.drawable.ic_edit:
                if(ACTION_EDIT != mActionType) {
                    mActionType = ACTION_EDIT;
                    refreshView();
                }
                break;

            case R.drawable.ic_refrash :
                mActionType = ACTION_NONE;
                loadView();
                break;

            default:
                Log.e(TAG, "未处理的resid : " + resid);
                break;
        }
    }


    /**
     * 首级adapter
     */
    public class SelfAdapter extends SimpleAdapter implements View.OnClickListener {
        private final static String TAG = "SelfAdapter";
        private final ListView        mRootView;

        public SelfAdapter(Context context, ListView fv,
                           List<? extends Map<String, ?>> mdata,
                           String[] from, int[] to) {
            super(context, mdata, R.layout.li_budget_show, from, to);
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
                // for background color
                Resources res = v.getResources();
                if(0 == position % 2)   {
                    v.setBackgroundColor(res.getColor(R.color.lightsteelblue));
                } else  {
                    v.setBackgroundColor(res.getColor(R.color.paleturquoise));
                }

                // for fold/unfold
                ImageButton ib = UtilFun.cast(v.findViewById(R.id.ib_hide_show));
                ib.getBackground().setAlpha(0);
                ib.setOnClickListener(this);

                HashMap<String, String> hm = UtilFun.cast(getItem(position));
                if(STListViewFragment.MPARA_SHOW_FOLD.equals(hm.get(STListViewFragment.MPARA_SHOW)))    {
                    //init_detail_view(fv, hm);
                    ib.setImageDrawable(res.getDrawable(R.drawable.ic_hide));
                }   else    {
                    init_detail_view(v, hm);
                    ib.setImageDrawable(res.getDrawable(R.drawable.ic_show));
                }

                // for action
                ImageButton ib_action = UtilFun.cast(v.findViewById(R.id.ib_action));
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
            }

            return v;
        }

        @Override
        public void onClick(View v) {
            int vid = v.getId();
            Resources res = v.getResources();

            int pos = mRootView.getPositionForView(v);
            HashMap<String, String> hm = UtilFun.cast(getItem(pos));
            View fv = mRootView.getChildAt(pos);

            switch (vid)    {
                case R.id.ib_hide_show :
                    ImageButton ib = UtilFun.cast(v);
                    if(STListViewFragment.MPARA_SHOW_FOLD.equals(hm.get(STListViewFragment.MPARA_SHOW)))    {
                        hm.put(STListViewFragment.MPARA_SHOW, STListViewFragment.MPARA_SHOW_UNFOLD);
                        init_detail_view(fv, hm);
                        ib.setImageDrawable(res.getDrawable(R.drawable.ic_hide));
                        addUnfoldItem(hm.get(STListViewFragment.MPARA_TAG));
                    }   else    {
                        hm.put(STListViewFragment.MPARA_SHOW, STListViewFragment.MPARA_SHOW_FOLD);
                        init_detail_view(fv, hm);
                        ib.setImageDrawable(res.getDrawable(R.drawable.ic_show));
                        removeUnfoldItem(hm.get(STListViewFragment.MPARA_TAG));
                    }
                    break;

                case R.id.ib_action :
                    ImageButton ib_action = UtilFun.cast(v);
                    int tag_id = Integer.parseInt(hm.get(STListViewFragment.MPARA_TAG));
                    if(ACTION_DELETE == mActionType)    {
                        if(ib_action.isSelected())  {
                            mLLDelBudget.removeFirstOccurrence(tag_id);
                            ib_action.getBackground().setAlpha(0);
                        }   else    {
                            mLLDelBudget.add(tag_id);
                            ib_action.getBackground().setAlpha(255);
                            ib_action.setBackgroundColor(res.getColor(R.color.red));
                        }

                        ib_action.setSelected(!ib_action.isSelected());
                    } else  {
                        Activity ac = getRootActivity();
                        Intent it = new Intent(ac, ACBudgetEdit.class);
                        it.putExtra(ACBudgetEdit.INTENT_LOAD_BUDGETID, tag_id);
                        ac.startActivityForResult(it, 1);
                    }
                    break;
            }
        }
    }


    /**
     * 次级adapter
     */
    public class SelfSubAdapter  extends SimpleAdapter {
        private final static String TAG = "SelfSubAdapter";
        private final ListView        mRootView;

        public SelfSubAdapter(Context context, ListView fv,
                              List<? extends Map<String, ?>> sdata,
                              String[] from, int[] to) {
            super(context, sdata, R.layout.li_budget_show_detail, from, to);
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
                Resources res = v.getResources();
                Bitmap nicon = BitmapFactory.decodeResource(res, R.drawable.ic_show_pay);
                v.setBackgroundColor(res.getColor(R.color.burlywood));
                ImageView iv = UtilFun.cast(v.findViewById(R.id.iv_show));
                iv.setBackgroundColor(Color.TRANSPARENT);
                iv.setImageBitmap(nicon);

                // for button
                ImageButton ib = UtilFun.cast(v.findViewById(R.id.ib_look));
                assert null != ib;
                ib.setVisibility(View.INVISIBLE);
            }

            return v;
        }
    }
}
