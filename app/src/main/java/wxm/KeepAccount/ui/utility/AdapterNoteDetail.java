package wxm.KeepAccount.ui.utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import wxm.KeepAccount.ui.base.FrgUitlity.FrgAdvBase;
import wxm.KeepAccount.ui.base.Helper.ViewHelper;
import wxm.KeepAccount.ui.base.SwipeLayout.SwipeLayout;
import wxm.KeepAccount.utility.ContextUtil;
import wxm.androidutil.util.FastViewHolder;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.BudgetItem;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.define.INote;
import wxm.KeepAccount.define.IncomeNoteItem;
import wxm.KeepAccount.define.PayNoteItem;
import wxm.KeepAccount.ui.base.Helper.ResourceHelper;
import wxm.KeepAccount.ui.data.edit.Note.ACPreveiwAndEdit;

/**
 * adapter for note detail
 * Created by WangXM on 2017/1/23.
 */
public class AdapterNoteDetail extends SimpleAdapter {
    class vwTag {
        public View mContent;
        public View mRight;
    }

    public final static String K_NODE = "node";
    private Context mCTSelf;
    private FrgAdvBase  mFrgHolder;



    public AdapterNoteDetail(FrgAdvBase frgHolder, Context context, List<? extends Map<String, ?>> data,
                             String[] from, int[] to) {
        super(context, data, R.layout.li_data_detail, from, to);
        mCTSelf = context;
        mFrgHolder = frgHolder;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        FastViewHolder vh = FastViewHolder.get(mCTSelf, convertView, R.layout.li_data_detail_holder);
        HashMap<String, INote> hmData = UtilFun.cast_t(getItem(position));

        SwipeLayout sl = vh.getView(R.id.swipe);
        vwTag vt = (vwTag)sl.getTag();
        if(null == vt) {
            vt = new vwTag();
            vt.mContent = LayoutInflater.from(mCTSelf).inflate(R.layout.li_data_detail, null);
            vt.mRight = LayoutInflater.from(mCTSelf).inflate(R.layout.vw_delte, null);
            vt.mRight.setTag(hmData.get(K_NODE));
            vt.mRight.setOnClickListener(v -> {
                INote data = (INote)v.getTag();
                List<Integer> al = Collections.singletonList(data.getId());

                if(data.isPayNote())    {
                    ContextUtil.getPayIncomeUtility().deletePayNotes(al);
                } else  {
                    ContextUtil.getPayIncomeUtility().deleteIncomeNotes(al);
                }
            });

            sl.setContentView(vt.mContent);
            sl.setRightView(vt.mRight);

            sl.setTag(vt);
        }

        initInfo(vt.mContent, hmData);
        return vh.getConvertView();
    }


    private void initInfo(View vwInfo, HashMap<String, INote> hmData)  {
        INote data = hmData.get(K_NODE);
        if (data.isPayNote()) {
            vwInfo.findViewById(R.id.rl_income).setVisibility(View.GONE);
            initPay(vwInfo, data);
        } else {
            vwInfo.findViewById(R.id.rl_pay).setVisibility(View.GONE);
            initIncome(vwInfo, data);
        }
    }

    /**
     * init pay node
     * @param vh    view holder
     * @param data  pay data
     */
    private void initPay(View vh, INote data) {
        PayNoteItem pn = data.toPayNote();
        ViewHelper vHelper = new ViewHelper(vh);

        vHelper.setText(R.id.tv_pay_title, pn.getInfo());

        BudgetItem bi = pn.getBudget();
        String b_name = bi == null ? "" : bi.getName();
        if (!UtilFun.StringIsNullOrEmpty(b_name)) {
            vHelper.setText(R.id.tv_pay_budget, b_name);
        } else {
            vHelper.setVisibility(R.id.tv_pay_budget, View.GONE);
            vHelper.setVisibility(R.id.iv_pay_budget, View.GONE);
        }

        vHelper.setText(R.id.tv_pay_amount, String.format(Locale.CHINA, "- %s", pn.getValToStr()));
        vHelper.setText(R.id.tv_pay_time, pn.getTs().toString().substring(11, 16));

        // for look detail
        vHelper.setVisibility(R.id.iv_pay_action, View.GONE);

        vHelper.getChildView(R.id.rl_pay).setOnClickListener(v -> {
            Intent intent;
            intent = new Intent(mCTSelf, ACPreveiwAndEdit.class);
            intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_ID, data.getId());
            intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, GlobalDef.STR_RECORD_PAY);

            mCTSelf.startActivity(intent);
        });

        // for budget
        if (UtilFun.StringIsNullOrEmpty(b_name)) {
            vHelper.setVisibility(R.id.rl_budget, View.GONE);
        }

        // for note
        String nt = pn.getNote();
        if (UtilFun.StringIsNullOrEmpty(nt)) {
            vHelper.setVisibility(R.id.rl_pay_note, View.GONE);
        } else {
            vHelper.setText(R.id.tv_pay_note, nt);
        }
    }

    /**
     * init income node
     * @param vh    view holder
     * @param data  income data
     */
    private void initIncome(View vh, INote data) {
        IncomeNoteItem i_n = data.toIncomeNote();
        ViewHelper vHelper = new ViewHelper(vh);

        vHelper.setText(R.id.tv_income_title, i_n.getInfo());

        vHelper.setText(R.id.tv_income_amount, i_n.getValToStr());
        vHelper.setText(R.id.tv_income_time, i_n.getTs().toString().substring(11, 16));

        // for look detail
        vHelper.setVisibility(R.id.iv_income_action, View.GONE);

        vHelper.getChildView(R.id.rl_income).setOnClickListener(v -> {
            Intent intent;
            intent = new Intent(mCTSelf, ACPreveiwAndEdit.class);
            intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_ID, data.getId());
            intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, GlobalDef.STR_RECORD_INCOME);

            mCTSelf.startActivity(intent);
        });

        // for note
        String nt = i_n.getNote();
        if (UtilFun.StringIsNullOrEmpty(nt)) {
            vHelper.setVisibility(R.id.rl_income_note, View.GONE);
        } else {
            vHelper.setText(R.id.tv_income_note, nt);
        }
    }
}
