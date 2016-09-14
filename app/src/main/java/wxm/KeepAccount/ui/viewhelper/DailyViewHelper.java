package wxm.KeepAccount.ui.viewhelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import wxm.KeepAccount.Base.db.IncomeNoteItem;
import wxm.KeepAccount.Base.db.PayNoteItem;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acinterface.ACNoteShow;
import wxm.KeepAccount.ui.acutility.ACNoteEdit;
import wxm.KeepAccount.ui.fragment.STListViewFragment;

/**
 * 日数据视图辅助类
 * Created by 123 on 2016/9/10.
 */
public class DailyViewHelper extends LVViewHelperBase implements ILVViewHelper {
    private final static String TAG = "DailyViewHelper";
    private boolean mBShowDelete;
    private boolean mBShowEdit;
    private boolean mBFilter;

    private static final int[] ITEM_DRAWABLES = {
                                    R.drawable.ic_leave
                                    ,R.drawable.ic_switch
                                    ,R.drawable.ic_edit
                                    ,R.drawable.ic_delete
                                    ,R.drawable.ic_add};

    public DailyViewHelper()    {
        super();
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        mSelfView       = inflater.inflate(R.layout.lv_pager, container, false);
        mBShowDelete    = false;
        mBShowEdit      = false;
        mBFilter        = false;

        // init ray menu
        RayMenu rayMenu = UtilFun.cast(mSelfView.findViewById(R.id.rm_show_record));
        assert null != rayMenu;
        final int itemCount = ITEM_DRAWABLES.length;
        for (int i = 0; i < itemCount; i++) {
            ImageView item = new ImageView(mSelfView.getContext());
            item.setImageResource(ITEM_DRAWABLES[i]);

            final int position = ITEM_DRAWABLES[i];
            rayMenu.addItem(item, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnRayMenuClick(position);
                }
            });// Add a menu item
        }

        return mSelfView;
    }

    @Override
    public View getView() {
        return mSelfView;
    }

    @Override
    public void loadView() {
        setAttachLayoutVisible(View.INVISIBLE);
        setFilterLayoutVisible(View.INVISIBLE);
        setAttachLayoutVisible(View.INVISIBLE);

        reloadData();
        refreshView();
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
            case AppGobalDef.INTRET_DAILY_DETAIL :
                reloadData();
                refreshView();
                break;

            default:
                Log.d(TAG, String.format("不处理的resultCode(%d)!", resultCode));
                break;
        }
    }

    /**
     * 重新加载数据
     */
    private void reloadData() {
        mMainPara.clear();
        mHMSubPara.clear();

        HashMap<String, ArrayList<Object>> hm_data =
                AppModel.getPayIncomeUtility().GetAllNotesToDay();
        parseNotes(hm_data);
    }

    /**
     * 不重新加载数据，仅更新视图
     */
    private void refreshView()  {
        // set layout
        setAttachLayoutVisible(mBFilter || mBShowDelete || mBShowEdit ?
                                View.VISIBLE : View.INVISIBLE);
        setFilterLayoutVisible(mBFilter ? View.VISIBLE : View.INVISIBLE);
        setAccpetGiveupLayoutVisible(mBShowDelete || mBShowEdit ?
                                View.VISIBLE : View.INVISIBLE);

        // update data
        LinkedList<HashMap<String, String>> n_mainpara = new LinkedList<>();
        if(mBFilter) {
            for (HashMap<String, String> i : mMainPara) {
                String cur_tag = i.get(STListViewFragment.MPARA_TAG);
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
                new String[]{STListViewFragment.MPARA_TITLE, STListViewFragment.MPARA_ABSTRACT},
                new int[]{R.id.tv_title, R.id.tv_abstract});
        lv.setAdapter(mSNAdapter);
        mSNAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化次级(详细数据)视图
     * @param v         主级视图
     * @param hm        主级视图附带数据
     */
    private void init_detail_view(View v, HashMap<String, String> hm) {
        // get sub para
        LinkedList<HashMap<String, String>> llhm = null;
        if(STListViewFragment.MPARA_TAG_SHOW.equals(hm.get(STListViewFragment.MPARA_STATUS))) {
            llhm = mHMSubPara.get(hm.get(STListViewFragment.MPARA_TAG));
        }

        if(null == llhm) {
            llhm = new LinkedList<>();
        }

        // init sub adapter
        ListView mLVShowDetail = UtilFun.cast(v.findViewById(R.id.lv_show_detail));
        assert null != mLVShowDetail;
        SelfSubAdapter mAdapter= new SelfSubAdapter( mSelfView.getContext(), mLVShowDetail,
                llhm, new String[]{STListViewFragment.SPARA_SHOW},
                new int[]{R.id.tv_show});
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
            case R.drawable.ic_add :    {
                Context ct = mSelfView.getContext();
                if(ct instanceof FragmentActivity) {
                    FragmentActivity fa = UtilFun.cast(ct);

                    Intent intent = new Intent(ct, ACNoteEdit.class);
                    intent.putExtra(ACNoteEdit.PARA_ACTION, ACNoteEdit.LOAD_NOTE_ADD);

                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(System.currentTimeMillis());
                    intent.putExtra(AppGobalDef.STR_RECORD_DATE,
                            String.format(Locale.CHINA
                                    , "%d-%02d-%02d"
                                    , cal.get(Calendar.YEAR)
                                    , cal.get(Calendar.MONTH) + 1
                                    , cal.get(Calendar.DAY_OF_MONTH)));

                    fa.startActivityForResult(intent, 1);
                }
            }
            break;

            case R.drawable.ic_switch :     {
                Context ct = mSelfView.getContext();
                if(ct instanceof ACNoteShow)    {
                    ACNoteShow as = UtilFun.cast(ct);
                    as.switchShow();
                }
            }
            break;

            case R.drawable.ic_delete:     {
                ListView lv = UtilFun.cast(mSelfView.findViewById(R.id.tabvp_lv_main));
                int cc = lv.getChildCount();
                if(0 < cc)  {
                    mBShowDelete = !mBShowDelete;
                    mBShowEdit = !mBShowDelete && mBShowEdit;
                    if(mBShowDelete)
                        refreshView();
                    else
                        loadView();
                }
            }
            break;

            case R.drawable.ic_edit:     {
                ListView lv = UtilFun.cast(mSelfView.findViewById(R.id.tabvp_lv_main));
                int cc = lv.getChildCount();
                if(0 < cc)  {
                    mBShowEdit = !mBShowEdit;
                    if(mBShowEdit && mBShowDelete)  {
                        mBShowDelete = false;
                        loadView();
                    } else  {
                        refreshView();
                    }
                }
            }
            break;


            case R.drawable.ic_leave :  {
                Context ct = mSelfView.getContext();
                if(ct instanceof Activity) {
                    Activity ac = UtilFun.cast(ct);
                    ac.setResult(AppGobalDef.INTRET_USR_LOGOUT);
                    ac.finish();
                }
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
                if (r instanceof PayNoteItem) {
                    PayNoteItem pi = UtilFun.cast(r);
                    pay_cout += 1;
                    pay_amount = pay_amount.add(pi.getVal());

                    map.put(STListViewFragment.SPARA_ID, String.valueOf(pi.getId()));
                    map.put(STListViewFragment.SPARA_TAG, STListViewFragment.SPARA_TAG_PAY);
                    show = String.format(Locale.CHINA, "%s\n金额 : %.02f"
                                        ,pi.getInfo() ,pi.getVal());
                } else {
                    IncomeNoteItem ii = UtilFun.cast(r);
                    income_cout += 1;
                    income_amount = income_amount.add(ii.getVal());

                    map.put(STListViewFragment.SPARA_ID, String.valueOf(ii.getId()));
                    map.put(STListViewFragment.SPARA_TAG, STListViewFragment.SPARA_TAG_INCOME);
                    show = String.format(Locale.CHINA, "%s\n金额 : %.02f"
                            ,ii.getInfo() ,ii.getVal());
                }

                map.put(STListViewFragment.SPARA_SHOW, show);
                map.put(STListViewFragment.MPARA_TAG, title);
                cur_llhm.add(map);
            }

            String show_str =
                    String.format(Locale.CHINA,
                            "支出项 ： %d    总金额 ：%.02f\n收入项 ： %d    总金额 ：%.02f",
                            pay_cout, pay_amount, income_cout, income_amount);

            HashMap<String, String> map = new HashMap<>();
            map.put(STListViewFragment.MPARA_TITLE, title);
            map.put(STListViewFragment.MPARA_ABSTRACT, show_str);
            map.put(STListViewFragment.MPARA_STATUS, STListViewFragment.MPARA_TAG_HIDE);
            map.put(STListViewFragment.MPARA_TAG, title);
            mMainPara.add(map);

            mHMSubPara.put(title, cur_llhm);
        }
    }


    /**
     * 首级列表adapter
     */
    public class SelfAdapter extends SimpleAdapter {
        private final static String TAG = "SelfAdapter";

        public SelfAdapter(Context context,
                         List<? extends Map<String, ?>> mdata,
                         String[] from, int[] to) {
            super(context, mdata, R.layout.li_daily_show, from, to);
        }

        @Override
        public View getView(final int position, View view, ViewGroup arg2) {
            View v = super.getView(position, view, arg2);
            if(null != v)   {
                //Log.i(TAG, "create view at pos = " + position);
                final View fv = v;
                ImageButton ib = UtilFun.cast(v.findViewById(R.id.ib_hide_show));
                ib.getBackground().setAlpha(0);
                ib.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Log.i(TAG, "onIbClick at pos = " + pos);
                        Resources res = v.getResources();
                        ImageButton ib = UtilFun.cast(v);
                        HashMap<String, String> hm = UtilFun.cast(getItem(position));
                        if(STListViewFragment.MPARA_TAG_HIDE.equals(hm.get(STListViewFragment.MPARA_STATUS)))    {
                            hm.put(STListViewFragment.MPARA_STATUS, STListViewFragment.MPARA_TAG_SHOW);
                            init_detail_view(fv, hm);
                            ib.setImageDrawable(res.getDrawable(R.drawable.ic_hide));
                        }   else    {
                            hm.put(STListViewFragment.MPARA_STATUS, STListViewFragment.MPARA_TAG_HIDE);
                            init_detail_view(fv, hm);
                            ib.setImageDrawable(res.getDrawable(R.drawable.ic_show));
                        }
                    }
                });

                Resources res = v.getResources();
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
    public class SelfSubAdapter  extends SimpleAdapter implements View.OnClickListener {
        private final static String TAG = "SelfSubAdapter";
        private ListView        mRootView;

        public SelfSubAdapter(Context context, ListView fv,
                             List<? extends Map<String, ?>> sdata,
                             String[] from, int[] to) {
            super(context, sdata, R.layout.li_daily_show_detail, from, to);
            mRootView = fv;
        }

        @Override
        public View getView(final int position, View view, ViewGroup arg2) {
            View v = super.getView(position, view, arg2);
            if(null != v)   {
                //Log.i(TAG, "create view at pos = " + position);
                HashMap<String, String> hm = UtilFun.cast(getItem(position));
                final Resources res = v.getResources();

                // for button
                ImageButton ib = UtilFun.cast(v.findViewById(R.id.ib_delete));
                assert null != ib;
                ib.getBackground().setAlpha(0);
                ib.setVisibility(mBShowDelete ? View.VISIBLE : View.INVISIBLE);
                if(mBShowDelete)    {
                    ib.setOnClickListener(this);
                }

                ib = UtilFun.cast(v.findViewById(R.id.ib_edit));
                assert null != ib;
                ib.getBackground().setAlpha(0);
                ib.setVisibility(mBShowEdit ? View.VISIBLE : View.INVISIBLE);
                if(mBShowEdit)  {
                    ib.setOnClickListener(this);
                }

                // for image & background
                Bitmap nicon;
                if(STListViewFragment.SPARA_TAG_PAY.equals(hm.get(STListViewFragment.SPARA_TAG)))   {
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
            String tp = hm.get(STListViewFragment.SPARA_TAG);
            int did = Integer.parseInt(hm.get(STListViewFragment.SPARA_ID));

            switch (vid)    {
                case R.id.ib_delete :       {
                    if(!v.isSelected()) {
                        List<Integer> ls = Collections.singletonList(did);
                        if (STListViewFragment.SPARA_TAG_PAY.equals(tp)) {
                            AppModel.getPayIncomeUtility().DeletePayNotes(ls);
                        } else {
                            AppModel.getPayIncomeUtility().DeleteIncomeNotes(ls);
                        }

                        v.setSelected(true);
                        v.getBackground().setAlpha(255);
                        v.setBackgroundColor(res.getColor(R.color.red));
                    }
                }
                break;

                case R.id.ib_edit : {
                    Context ct = mSelfView.getContext();
                    if(ct instanceof Activity) {
                        Activity ac = UtilFun.cast(ct);
                        Intent intent = new Intent(ac, ACNoteEdit.class);
                        intent.putExtra(ACNoteEdit.PARA_ACTION, ACNoteEdit.LOAD_NOTE_MODIFY);
                        if (STListViewFragment.SPARA_TAG_PAY.equals(tp)) {
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
