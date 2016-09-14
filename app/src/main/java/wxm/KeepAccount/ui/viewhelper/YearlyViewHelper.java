package wxm.KeepAccount.ui.viewhelper;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import wxm.KeepAccount.ui.fragment.STListViewFragment;

/**
 * 年数据视图辅助类
 * Created by 123 on 2016/9/10.
 */
public class YearlyViewHelper  implements ILVViewHelper {
    private final static String TAG = "YearlyViewHelper";
    private View    mSelfView;
    private boolean mBFilter;

    private LinkedList<HashMap<String, String>> mMainPara;
    private HashMap<String, LinkedList<HashMap<String, String>>>    mHMSubPara;
    private LinkedList<String>                                      mFilterPara;

    public YearlyViewHelper()    {
        mMainPara = new LinkedList<>();
        mHMSubPara = new HashMap<>();
        mFilterPara = new LinkedList<>();
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        mSelfView = inflater.inflate(R.layout.lv_pager, container, false);
        // init ray menu
        RayMenu rayMenu = UtilFun.cast(mSelfView.findViewById(R.id.rm_show_record));
        assert null != rayMenu;
        rayMenu.setVisibility(View.INVISIBLE);

        return mSelfView;
    }

    @Override
    public View getView() {
        return mSelfView;
    }

    @Override
    public void loadView() {
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

        // format output
        HashMap<String, ArrayList<Object>> hm_data =
                AppModel.getPayIncomeUtility().GetAllNotesToYear();

        parseNotes(hm_data);
    }

    /**
     * 不重新加载数据，仅更新视图
     */
    private void refreshView()  {
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
     * 解析数据
     * @param notes  待解析数据
     */
    private void parseNotes(HashMap<String, ArrayList<Object>> notes)   {
        mMainPara.clear();
        mHMSubPara.clear();

        ArrayList<String> set_k = new ArrayList<>(notes.keySet());
        Collections.sort(set_k);
        for (String k : set_k) {
            String title = ToolUtil.FormatDateString(k);
            ArrayList<Object> v = notes.get(k);
            parseOneYear(title, v);
        }
    }

    /**
     * 解析一年的数据
     * @param tag       此年数据的tag
     * @param notes     此年数据
     */
    private void parseOneYear(String tag, List<Object> notes)    {
        int pay_cout = 0;
        int income_cout = 0;
        BigDecimal pay_amount = BigDecimal.ZERO;
        BigDecimal income_amount = BigDecimal.ZERO;
        HashMap<String, ArrayList<Object>> hm_data = new HashMap<>();
        for (Object r : notes) {
            String h_k;
            if (r instanceof PayNoteItem) {
                PayNoteItem pi = UtilFun.cast(r);
                pay_cout += 1;
                pay_amount = pay_amount.add(pi.getVal());
                h_k = pi.getTs().toString().substring(0, 7);
            } else {
                IncomeNoteItem ii = UtilFun.cast(r);
                income_cout += 1;
                income_amount = income_amount.add(ii.getVal());
                h_k = ii.getTs().toString().substring(0, 7);
            }

            ArrayList<Object> h_v = hm_data.get(h_k);
            if (null == h_v) {
                ArrayList<Object> v = new ArrayList<>();
                v.add(r);
                hm_data.put(h_k, v);
            } else {
                h_v.add(r);
            }
        }

        String show_str =
                String.format(Locale.CHINA,
                        "支出项 ： %d    总金额 ：%.02f\n收入项 ： %d    总金额 ：%.02f",
                        pay_cout, pay_amount, income_cout, income_amount);

        HashMap<String, String> map = new HashMap<>();
        map.put(STListViewFragment.MPARA_TITLE, tag);
        map.put(STListViewFragment.MPARA_ABSTRACT, show_str);
        map.put(STListViewFragment.MPARA_STATUS, STListViewFragment.MPARA_TAG_HIDE);
        map.put(STListViewFragment.MPARA_TAG, tag);
        mMainPara.add(map);

        parseMonths(tag, hm_data);
    }

    /**
     * 解析一个月的数据
     * @param tag       此月数据的tag
     * @param hm_data   此月数据
     */
    private void parseMonths(String tag, HashMap<String, ArrayList<Object>> hm_data)    {
        ArrayList<String> set_k = new ArrayList<>(hm_data.keySet());
        Collections.sort(set_k);
        LinkedList<HashMap<String, String>> cur_llhm = new LinkedList<>();
        for(String k : set_k) {
            ArrayList<Object> notes = hm_data.get(k);

            int pay_cout = 0;
            int income_cout = 0;
            BigDecimal pay_amount = BigDecimal.ZERO;
            BigDecimal income_amount = BigDecimal.ZERO;
            for (Object r : notes) {
                if (r instanceof PayNoteItem) {
                    PayNoteItem pi = UtilFun.cast(r);
                    pay_cout += 1;
                    pay_amount = pay_amount.add(pi.getVal());
                } else {
                    IncomeNoteItem ii = UtilFun.cast(r);
                    income_cout += 1;
                    income_amount = income_amount.add(ii.getVal());
                }
            }

            String sub_tag = ToolUtil.FormatDateString(k);
            String show =
                    String.format(Locale.CHINA,
                            "%s\n支出项 ： %d    总金额 ：%.02f\n收入项 ： %d    总金额 ：%.02f",
                            sub_tag,
                            pay_cout, pay_amount, income_cout, income_amount);

            HashMap<String, String> map = new HashMap<>();
            map.put(STListViewFragment.SPARA_SHOW, show);
            map.put(STListViewFragment.MPARA_TAG, tag);
            map.put(STListViewFragment.SPARA_TAG, sub_tag);
            cur_llhm.add(map);
        }

        mHMSubPara.put(tag, cur_llhm);
    }



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
        SelfSubAdapter mAdapter= new SelfSubAdapter( mSelfView.getContext(), llhm,
                new String[]{STListViewFragment.SPARA_SHOW},
                new int[]{R.id.tv_show});
        mLVShowDetail.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        ToolUtil.setListViewHeightBasedOnChildren(mLVShowDetail);
    }


    /**
     * 首级adapter
     */
    public class SelfAdapter extends SimpleAdapter {
        private final static String TAG = "SelfAdapter";

        public SelfAdapter(Context context,
                           List<? extends Map<String, ?>> mdata,
                           String[] from, int[] to) {
            super(context, mdata, R.layout.li_yearly_show, from, to);
        }

        @Override
        public View getView(final int position, View view, ViewGroup arg2) {
            View v = super.getView(position, view, arg2);
            if(null != v)   {
                //Log.i(TAG, "create view at pos = " + position);

                final View pv = v;
                ImageButton ib = UtilFun.cast(v.findViewById(R.id.ib_hide_show));
                ib.getBackground().setAlpha(0);
                ib.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Log.i(TAG, "onIbClick at pos = " + pos);
                        Resources res = pv.getResources();
                        ImageButton ib = UtilFun.cast(pv.findViewById(R.id.ib_hide_show));
                        HashMap<String, String> hm = UtilFun.cast(getItem(position));
                        if(STListViewFragment.MPARA_TAG_HIDE.equals(hm.get(STListViewFragment.MPARA_STATUS)))    {
                            hm.put(STListViewFragment.MPARA_STATUS, STListViewFragment.MPARA_TAG_SHOW);
                            init_detail_view(pv, hm);
                            ib.setImageDrawable(res.getDrawable(R.drawable.ic_hide));
                        }   else    {
                            hm.put(STListViewFragment.MPARA_STATUS, STListViewFragment.MPARA_TAG_HIDE);
                            init_detail_view(pv, hm);
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
     * 次级adapter
     */
    public class SelfSubAdapter  extends SimpleAdapter {
        private final static String TAG = "SelfSubAdapter";

        public SelfSubAdapter(Context context,
                              List<? extends Map<String, ?>> sdata,
                              String[] from, int[] to) {
            super(context, sdata, R.layout.li_yearly_show_detail, from, to);
        }

        @Override
        public View getView(final int position, View view, ViewGroup arg2) {
            View v = super.getView(position, view, arg2);
            if(null != v)   {
                ImageButton ib = UtilFun.cast(v.findViewById(R.id.ib_look));
                ib.getBackground().setAlpha(0);
                ib.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Context ct = mSelfView.getContext();
                        if(ct instanceof ACNoteShow)    {
                            ACNoteShow as = UtilFun.cast(ct);
                            as.jumpByTabName(STListViewFragment.TAB_TITLE_MONTHLY);

                            HashMap<String, String> hp = UtilFun.cast(getItem(position));
                            final String hp_tag = hp.get(STListViewFragment.SPARA_TAG);
                            as.filterView(Collections.singletonList(hp_tag));
                        }
                    }
                });

                Resources res = v.getResources();
                if(0 == position % 2)   {
                    v.setBackgroundColor(res.getColor(R.color.wheat));
                } else  {
                    v.setBackgroundColor(res.getColor(R.color.salmon));
                }
            }

            return v;
        }
    }
}
