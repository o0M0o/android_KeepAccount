package wxm.KeepAccount.ui.viewhelper;

import android.content.Context;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.db.IncomeNoteItem;
import wxm.KeepAccount.Base.db.PayNoteItem;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.base.fragment.LVFRGContent;

/**
 * 日数据视图辅助类
 * Created by 123 on 2016/9/10.
 */
public class DailyViewHelper implements ILVViewHelper {
    private final static String TAG = "DailyViewHelper";
    private View    mSelfView;

    private LinkedList<HashMap<String, String>>                     mMainPara;
    private HashMap<String, LinkedList<HashMap<String, String>>>    mHMSubPara;

    public DailyViewHelper()    {
        mMainPara = new LinkedList<>();
        mHMSubPara = new HashMap<>();
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        mSelfView = inflater.inflate(R.layout.lv_pager, container, false);
        return mSelfView;
    }

    @Override
    public View getView() {
        return mSelfView;
    }

    @Override
    public void loadView() {
        // get days info from record
        HashMap<String, ArrayList<Object>> hm_data =
                AppModel.getPayIncomeUtility().GetAllNotesToDay();

        // format output
        parseNotes(hm_data);

        // 设置listview adapter
        ListView lv = UtilFun.cast(mSelfView.findViewById(R.id.tabvp_lv_main));
        SelfAdapter mSNAdapter = new SelfAdapter(mSelfView.getContext(), mMainPara,
                                    new String[]{LVFRGContent.MPARA_TITLE, LVFRGContent.MPARA_ABSTRACT},
                                    new int[]{R.id.tv_title, R.id.tv_abstract});
        lv.setAdapter(mSNAdapter);
        mSNAdapter.notifyDataSetChanged();
    }

    private void parseNotes(HashMap<String, ArrayList<Object>> notes)   {
        mMainPara.clear();
        mHMSubPara.clear();

        ArrayList<String> set_k = new ArrayList<String>(notes.keySet());
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

                    map.put(LVFRGContent.SPARA_TAG, LVFRGContent.SPARA_TAG_PAY);
                    show = String.format(Locale.CHINA, "%s\n金额 : %.02f"
                                        ,pi.getInfo() ,pi.getVal());
                } else {
                    IncomeNoteItem ii = UtilFun.cast(r);
                    income_cout += 1;
                    income_amount = income_amount.add(ii.getVal());

                    map.put(LVFRGContent.SPARA_TAG, LVFRGContent.SPARA_TAG_INCOME);
                    show = String.format(Locale.CHINA, "%s\n金额 : %.02f"
                            ,ii.getInfo() ,ii.getVal());
                }

                map.put(LVFRGContent.SPARA_SHOW, show);
                map.put(LVFRGContent.MPARA_TAG, title);
                cur_llhm.add(map);
            }

            String show_str =
                    String.format(Locale.CHINA,
                            "支出项 ： %d    总金额 ：%.02f\n收入项 ： %d    总金额 ：%.02f",
                            pay_cout, pay_amount, income_cout, income_amount);

            HashMap<String, String> map = new HashMap<>();
            map.put(LVFRGContent.MPARA_TITLE, title);
            map.put(LVFRGContent.MPARA_ABSTRACT, show_str);
            map.put(LVFRGContent.MPARA_STATUS, LVFRGContent.MPARA_TAG_HIDE);
            map.put(LVFRGContent.MPARA_TAG, title);
            mMainPara.add(map);

            mHMSubPara.put(title, cur_llhm);
        }
    }

    protected void onIbClick(View vw, int pos) {
        //Log.i(TAG, "onIbClick at pos = " + pos);
        Resources res = vw.getResources();
        ImageButton ib = UtilFun.cast(vw.findViewById(R.id.ib_hide_show));
        HashMap<String, String> hm = mMainPara.get(pos);
        if(LVFRGContent.MPARA_TAG_HIDE.equals(hm.get(LVFRGContent.MPARA_STATUS)))    {
            hm.put(LVFRGContent.MPARA_STATUS, LVFRGContent.MPARA_TAG_SHOW);
            init_detail_view(vw, hm);
            ib.setImageDrawable(res.getDrawable(R.drawable.ic_hide));
        }   else    {
            hm.put(LVFRGContent.MPARA_STATUS, LVFRGContent.MPARA_TAG_HIDE);
            init_detail_view(vw, hm);
            ib.setImageDrawable(res.getDrawable(R.drawable.ic_show));
        }
    }


    private void init_detail_view(View v, HashMap<String, String> hm) {
        // get sub para
        LinkedList<HashMap<String, String>> llhm = null;
        if(LVFRGContent.MPARA_TAG_SHOW.equals(hm.get(LVFRGContent.MPARA_STATUS))) {
            llhm = mHMSubPara.get(hm.get(LVFRGContent.MPARA_TAG));
        }

        if(null == llhm) {
            llhm = new LinkedList<>();
        }

        // init sub adapter
        ListView mLVShowDetail = UtilFun.cast(v.findViewById(R.id.lv_show_detail));
        assert null != mLVShowDetail;
        SelfSubAdapter mAdapter= new SelfSubAdapter( mSelfView.getContext(), llhm,
                                            new String[]{LVFRGContent.SPARA_SHOW},
                                            new int[]{R.id.tv_show});
        mLVShowDetail.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(mLVShowDetail, mAdapter);
    }


    private void setListViewHeightBasedOnChildren(ListView listView, SelfSubAdapter sap) {
        int totalHeight = 0;
        for (int i = 0, len = sap.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = sap.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (sap.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }



    public class SelfAdapter extends SimpleAdapter {
        private final static String TAG = "SelfAdapter";

        public SelfAdapter(Context context,
                         List<? extends Map<String, ?>> mdata,
                         String[] from, int[] to) {
            super(context, mdata, R.layout.li_show, from, to);
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
                        onIbClick(pv, position);
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


    public class SelfSubAdapter  extends SimpleAdapter {
        private final static String TAG = "SelfSubAdapter";

        private LinkedList<HashMap<String, String>> mLVSubList;

        public SelfSubAdapter(Context context,
                             List<? extends Map<String, ?>> sdata,
                             String[] from, int[] to) {
            super(context, sdata, R.layout.li_show_detail, from, to);
            mLVSubList = UtilFun.cast(sdata);
        }

        @Override
        public View getView(final int position, View view, ViewGroup arg2) {
            View v = super.getView(position, view, arg2);
            if(null != v)   {
                Log.i(TAG, "create view at pos = " + position);

                Bitmap nicon = null;
                Resources res = v.getResources();
                HashMap<String, String> hm = mLVSubList.get(position);
                if(LVFRGContent.SPARA_TAG_PAY.equals(hm.get(LVFRGContent.SPARA_TAG)))   {
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
    }
}
