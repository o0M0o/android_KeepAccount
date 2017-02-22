package wxm.KeepAccount.ui.utility;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.define.BudgetItem;
import wxm.KeepAccount.define.INote;
import wxm.KeepAccount.define.IncomeNoteItem;
import wxm.KeepAccount.define.PayNoteItem;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.data.edit.Note.ACPreveiwAndEdit;

/**
 * adapter for note detail
 * Created by ookoo on 2017/1/23.
 */
public class AdapterNoteDetail extends SimpleAdapter {
    private final static String LOG_TAG = "AdapterNoteDetail";
    public final static String  K_NODE     = "node";

    private int mCLNoSelected;
    private int mCLSelected;


    private static class ContentViewHolder {
        RelativeLayout  mRLPay;
        RelativeLayout  mRLIncome;

        RelativeLayout  mRLDelete;
    }

    /**
     * 如果设置为true则数据可以删除
     */
    private boolean mBLCanDelete = false;
    private ArrayList<INote>    mALDelNotes;

    public AdapterNoteDetail(Context context, List<? extends Map<String, ?>> data,
                             String[] from, int[] to) {
        super(context, data, R.layout.li_daily_show_detail, from, to);

        Resources res = context.getResources();
        mCLNoSelected = res.getColor(R.color.red_ff725f_half);
        mCLSelected   = res.getColor(R.color.red_ff725f);

        mALDelNotes = new ArrayList<>();
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

    /**
     * 设置是否可以删除数据
     * @param bdel  若为true则可以删除数据
     */
    public void setCanDelete(boolean bdel)  {
        mBLCanDelete = bdel;
    }


    /**
     * 获得待删除的节点
     * @return  待删除节点链表
     */
    public List<INote>  getWantDeleteNotes()    {
        return mALDelNotes;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        if(null != v)   {
            HashMap<String, INote> hm_d = UtilFun.cast_t(getItem(position));

            ContentViewHolder ch = new ContentViewHolder();
            ch.mRLPay    = UtilFun.cast_t(v.findViewById(R.id.rl_pay));
            ch.mRLIncome = UtilFun.cast_t(v.findViewById(R.id.rl_income));

            ch.mRLDelete = UtilFun.cast_t(v.findViewById(R.id.rl_delete));

            loadContentViewHolder(ch, hm_d.get(K_NODE));
            initDelAction(ch, hm_d.get(K_NODE), mBLCanDelete);
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

    /**
     * 初始化删除动作
     * @param ch            UI容器
     * @param data          数据
     * @param bflag         若为ture则数据可删除
     */
    private void initDelAction(ContentViewHolder ch, INote data, boolean bflag)     {
        if(!bflag)
            mALDelNotes.clear();

        ch.mRLDelete.setVisibility(bflag ? View.VISIBLE : View.GONE);
        ch.mRLDelete.setOnClickListener(view -> {
            if(mALDelNotes.contains(data))  {
                mALDelNotes.remove(data);
                ch.mRLDelete.setBackgroundColor(mCLNoSelected);
            } else  {
                mALDelNotes.add(data);
                ch.mRLDelete.setBackgroundColor(mCLSelected);
            }
        });
    }

    /**
     * 当前节点是pay时初始化
     * @param rl_pay  pay节点
     * @param data    数据
     */
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

        // for look detail
        ImageView iv = UtilFun.cast_t(rl_pay.findViewById(R.id.iv_pay_action));
        iv.setVisibility(View.GONE);
        rl_pay.setOnClickListener(v -> {
            Intent intent;
            intent = new Intent(rl_pay.getContext(), ACPreveiwAndEdit.class);
            intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_ID, data.getId());
            intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, GlobalDef.STR_RECORD_PAY);

            (rl_pay.getContext()).startActivity(intent);
        });

        // for budget
        if(UtilFun.StringIsNullOrEmpty(b_name)) {
            RelativeLayout rl = UtilFun.cast_t(rl_pay.findViewById(R.id.rl_budget));
            rl.setVisibility(View.GONE);
        }

        // for note
        String nt = pn.getNote();
        if(UtilFun.StringIsNullOrEmpty(nt)) {
            RelativeLayout rl = UtilFun.cast_t(rl_pay.findViewById(R.id.rl_pay_note));
            rl.setVisibility(View.GONE);
        } else  {
            tv = UtilFun.cast_t(rl_pay.findViewById(R.id.tv_pay_note));
            tv.setText(nt);
        }
    }

    /**
     * 当前节点是income时初始化
     * @param rl_income     income节点
     * @param data          数据
     */
    private void initIncome(RelativeLayout rl_income, INote data) {
        IncomeNoteItem i_n = UtilFun.cast_t(data);

        TextView tv = UtilFun.cast_t(rl_income.findViewById(R.id.tv_income_title));
        tv.setText(i_n.getInfo());

        tv = UtilFun.cast_t(rl_income.findViewById(R.id.tv_income_amount));
        tv.setText(String.format(Locale.CHINA, "+ %.02f", i_n.getVal()));

        tv = UtilFun.cast_t(rl_income.findViewById(R.id.tv_income_time));
        tv.setText(i_n.getTs().toString().substring(11, 16));

        // for look detail
        ImageView iv = UtilFun.cast_t(rl_income.findViewById(R.id.iv_income_action));
        iv.setVisibility(View.GONE);
        rl_income.setOnClickListener(v -> {
            Intent intent;
            intent = new Intent(rl_income.getContext(), ACPreveiwAndEdit.class);
            intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_ID, data.getId());
            intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, GlobalDef.STR_RECORD_INCOME);

            (rl_income.getContext()).startActivity(intent);
        });

        // for note
        String nt = i_n.getNote();
        if(UtilFun.StringIsNullOrEmpty(nt)) {
            RelativeLayout rl = UtilFun.cast_t(rl_income.findViewById(R.id.rl_income_note));
            rl.setVisibility(View.GONE);
        } else  {
            tv = UtilFun.cast_t(rl_income.findViewById(R.id.tv_income_note));
            tv.setText(nt);
        }
    }
}
