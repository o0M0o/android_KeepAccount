package wxm.KeepAccount.ui.fragment.utility;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.BudgetItem;
import wxm.KeepAccount.Base.data.INote;
import wxm.KeepAccount.Base.data.IncomeNoteItem;
import wxm.KeepAccount.Base.data.PayNoteItem;
import wxm.KeepAccount.Base.define.GlobalDef;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.DataBase.NoteShowDataHelper;
import wxm.KeepAccount.ui.DataBase.NoteShowInfo;
import wxm.KeepAccount.ui.acutility.ACPreveiwAndEdit;

/**
 * for note pad content detail
 * Created by ookoo on 2016/12/12.
 */
public class FrgCalendarContent extends FrgUtilityBase {
    private final static String  K_NODE     = "node";

    // for ui
    @BindView(R.id.tv_month_day)
    TextView mTVMonthDay;

    @BindView(R.id.tv_year_month)
    TextView mTVYearMonth;

    @BindView(R.id.header_day_balance)
    TextView mTVBalance;

    @BindView(R.id.lv_body)
    ListView mLVBody;

    @BindColor(R.color.darkred)
    int mCLPay;

    @BindColor(R.color.darkslategrey)
    int mCLIncome;

    // for data
    private String          mSZHotDay;
    private List<INote>     mLSDayContents;


    private static class ContentViewHolder {
        RelativeLayout  mRLPay;
        RelativeLayout  mRLIncome;
    }

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgFLNotePadContent";
        View rootView = layoutInflater.inflate(R.layout.frg_calendar_content, viewGroup, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    /**
     * 设置展示类容
     * @param day           for day
     * @param content       for content
     */
    public void setDay(String day, List<INote> content)  {
        mSZHotDay = day;
        mLSDayContents = content;

        loadContent();
    }

    @Override
    protected void initUiComponent(View view) {
        mSZHotDay       = null;
        mLSDayContents  = null;
    }

    @Override
    protected void initUiInfo() {
        loadContent();
    }

    /// PRIVATE BEGIN
    private void loadContent()  {
        if(isDetached())
            return;

        if(UtilFun.StringIsNullOrEmpty(mSZHotDay))  {
            setVisibility(View.INVISIBLE);
            return;
        }

        String[] arr = mSZHotDay.split("-");
        if(3 != arr.length) {
            setVisibility(View.INVISIBLE);
            return;
        }

        // for header;
        setVisibility(View.VISIBLE);
        mTVMonthDay.setText(arr[2]);
        mTVYearMonth.setText(String.format(Locale.CHINA, "%s年%s月", arr[0], arr[1]));

        NoteShowInfo ni = NoteShowDataHelper.getInstance().getDayInfo().get(mSZHotDay);
        BigDecimal bb = null != ni ? ni.getBalance() : BigDecimal.ZERO;
        mTVBalance.setText(String.format(Locale.CHINA, "%s %.02f",
                0 > bb.compareTo(BigDecimal.ZERO) ? "-" : "+",
                Math.abs(bb.floatValue())));
        mTVBalance.setTextColor(0 > bb.compareTo(BigDecimal.ZERO) ?  mCLPay : mCLIncome);

        // for list body
        LinkedList<HashMap<String, INote>> c_para = new LinkedList<>();
        if(!UtilFun.ListIsNullOrEmpty(mLSDayContents)) {
            for (INote ci : mLSDayContents) {
                HashMap<String, INote> hm = new HashMap<>();
                hm.put(K_NODE, ci);

                c_para.add(hm);
            }
        }

        SelfAdapter ap = new SelfAdapter(getActivity(), c_para,
                            new String[]{}, new int[]{});
        mLVBody.setAdapter(ap);
        ap.notifyDataSetChanged();
    }

    private void setVisibility(int vis) {
        mTVMonthDay.setVisibility(vis);
        mTVYearMonth.setVisibility(vis);
        mLVBody.setVisibility(vis);
    }
    /// PRIVATE END

    /**
     * 列表adapter
     */
    private class SelfAdapter extends SimpleAdapter {
        private final static String TAG = "SelfAdapter";

        SelfAdapter(Context context, List<? extends Map<String, ?>> data,
                    String[] from, int[] to) {
            super(context, data, R.layout.li_daily_show_detail, from, to);
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


        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            if(null != v)   {
                HashMap<String, INote> hm_d = UtilFun.cast_t(getItem(position));

                ContentViewHolder ch = new ContentViewHolder();
                ch.mRLPay    = UtilFun.cast_t(v.findViewById(R.id.rl_pay));
                ch.mRLIncome = UtilFun.cast_t(v.findViewById(R.id.rl_income));

                loadContentViewHolder(ch, hm_d.get(K_NODE));
            }

            return v;
        }


        /**
         * 加载内容视图
         * @param ch        内容视图容器
         * @param data      数据
         */
        private void loadContentViewHolder(ContentViewHolder ch, INote data)    {
            if(data.isPayNote())    {
                ch.mRLIncome.setVisibility(View.GONE);
                initPay(ch.mRLPay, data);
            } else {
                ch.mRLPay.setVisibility(View.GONE);
                initIncome(ch.mRLIncome, data);
            }
        }

        private void initPay(RelativeLayout rl_pay, INote data) {
            PayNoteItem pn = UtilFun.cast_t(data);

            TextView tv = UtilFun.cast_t(rl_pay.findViewById(R.id.tv_pay_title));
            tv.setText(pn.getInfo());

            BudgetItem bi = pn.getBudget();
            String b_name = bi == null ? "" : bi.getName();
            if(!UtilFun.StringIsNullOrEmpty(b_name)) {
                tv = UtilFun.cast_t(rl_pay.findViewById(R.id.tv_pay_budget));
                tv.setText(b_name);
            } else  {
                tv = UtilFun.cast_t(rl_pay.findViewById(R.id.tv_pay_budget));
                tv.setVisibility(View.INVISIBLE);

                ImageView iv = UtilFun.cast_t(rl_pay.findViewById(R.id.iv_pay_budget));
                iv.setVisibility(View.INVISIBLE);
            }

            tv = UtilFun.cast_t(rl_pay.findViewById(R.id.tv_pay_amount));
            tv.setText(String.format(Locale.CHINA, "- %.02f", pn.getVal()));

            tv = UtilFun.cast_t(rl_pay.findViewById(R.id.tv_pay_time));
            tv.setText(pn.getTs().toString().substring(11, 16));

            ImageView iv = UtilFun.cast_t(rl_pay.findViewById(R.id.iv_pay_action));
            iv.setOnClickListener(v -> {
                Intent intent;
                intent = new Intent(rl_pay.getContext(), ACPreveiwAndEdit.class);
                intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_ID, data.getId());
                intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, GlobalDef.STR_RECORD_PAY);

                (rl_pay.getContext()).startActivity(intent);
            });
            //iv.setImageDrawable(ACTION_EDIT == mActionType ? mDADedit : mDADelete);

            // for budget
            if(UtilFun.StringIsNullOrEmpty(b_name)) {
                RelativeLayout rl = UtilFun.cast_t(rl_pay.findViewById(R.id.rl_budget));
                ToolUtil.setViewGroupVisible(rl, View.INVISIBLE);
            }

            // for note
            String nt = pn.getNote();
            if(UtilFun.StringIsNullOrEmpty(nt)) {
                RelativeLayout rl = UtilFun.cast_t(rl_pay.findViewById(R.id.rl_pay_note));
                ToolUtil.setViewGroupVisible(rl, View.INVISIBLE);
            } else  {
                tv = UtilFun.cast_t(rl_pay.findViewById(R.id.tv_pay_note));
                tv.setText(nt);
            }
        }

        private void initIncome(RelativeLayout rl_income, INote data) {
            IncomeNoteItem i_n = UtilFun.cast_t(data);

            TextView tv = UtilFun.cast_t(rl_income.findViewById(R.id.tv_income_title));
            tv.setText(i_n.getInfo());

            tv = UtilFun.cast_t(rl_income.findViewById(R.id.tv_income_amount));
            tv.setText(String.format(Locale.CHINA, "+ %.02f", i_n.getVal()));

            tv = UtilFun.cast_t(rl_income.findViewById(R.id.tv_income_time));
            tv.setText(i_n.getTs().toString().substring(11, 16));

            ImageView iv = UtilFun.cast_t(rl_income.findViewById(R.id.iv_income_action));
            iv.setOnClickListener(v -> {
                Intent intent;
                intent = new Intent(rl_income.getContext(), ACPreveiwAndEdit.class);
                intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_ID, data.getId());
                intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, GlobalDef.STR_RECORD_INCOME);

                (rl_income.getContext()).startActivity(intent);
            });
            //iv.setImageDrawable(ACTION_EDIT == mActionType ? mDADedit : mDADelete);

            //int did = Integer.parseInt(hd.get(K_ID));
            //iv.setBackgroundColor(mDelIncome.contains(did) ? mCLSel : mCLNoSel);

            // for note
            String nt = i_n.getNote();
            if(UtilFun.StringIsNullOrEmpty(nt)) {
                RelativeLayout rl = UtilFun.cast_t(rl_income.findViewById(R.id.rl_income_note));
                ToolUtil.setViewGroupVisible(rl, View.INVISIBLE);
            } else  {
                tv = UtilFun.cast_t(rl_income.findViewById(R.id.tv_income_note));
                tv.setText(nt);
            }
        }

    }
}
