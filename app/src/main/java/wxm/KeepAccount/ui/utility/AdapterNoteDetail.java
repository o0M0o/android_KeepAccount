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
import wxm.KeepAccount.utility.ContextUtil;

/**
 * adapter for note detail
 * Created by ookoo on 2017/1/23.
 */
public class AdapterNoteDetail extends SimpleAdapter {
    private final static String LOG_TAG = "AdapterNoteDetail";
    public final static String K_NODE = "node";

    private final static int CL_NOT_SELECTED;
    private final static int CL_SELECTED;

    static {
        Resources res = ContextUtil.getInstance().getResources();

        CL_NOT_SELECTED = res.getColor(R.color.red_ff725f_half);
        CL_SELECTED = res.getColor(R.color.red_ff725f);
    }

    private Context mCTSelf;


    /**
     * 如果设置为true则数据可以删除
     */
    private boolean mBLCanDelete = false;
    private ArrayList<INote> mALDelNotes;

    public AdapterNoteDetail(Context context, List<? extends Map<String, ?>> data,
                             String[] from, int[] to) {
        super(context, data, R.layout.li_daily_show_detail, from, to);
        mCTSelf = context;
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
     *
     * @param bdel 若为true则可以删除数据
     */
    public void setCanDelete(boolean bdel) {
        mBLCanDelete = bdel;
    }


    /**
     * 获得待删除的节点
     *
     * @return 待删除节点链表
     */
    public List<INote> getWantDeleteNotes() {
        return mALDelNotes;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        FastViewHolder vh = FastViewHolder.get(mCTSelf, convertView,
                R.layout.li_daily_show_detail);

        HashMap<String, INote> hm_d = UtilFun.cast_t(getItem(position));
        INote data = hm_d.get(K_NODE);
        if (data.isPayNote()) {
            vh.getView(R.id.rl_income).setVisibility(View.GONE);
            initPay(vh, data);
        } else {
            vh.getView(R.id.rl_pay).setVisibility(View.GONE);
            initIncome(vh, data);
        }

        initDelAction(vh, hm_d.get(K_NODE), mBLCanDelete);
        return vh.getConvertView();
    }


    /**
     * 初始化删除动作
     *
     * @param vh    view holder
     * @param data  数据
     * @param bflag 若为ture则数据可删除
     */
    private void initDelAction(FastViewHolder vh, INote data, boolean bflag) {
        if (!bflag)
            mALDelNotes.clear();

        RelativeLayout rl_del = vh.getView(R.id.rl_delete);
        rl_del.setVisibility(bflag ? View.VISIBLE : View.GONE);
        rl_del.setOnClickListener(view -> {
            if (mALDelNotes.contains(data)) {
                mALDelNotes.remove(data);
                rl_del.setBackgroundColor(CL_NOT_SELECTED);
            } else {
                mALDelNotes.add(data);
                rl_del.setBackgroundColor(CL_SELECTED);
            }
        });
    }

    /**
     * 当前节点是pay时初始化
     *
     * @param vh   view holder
     * @param data 数据
     */
    private void initPay(FastViewHolder vh, INote data) {
        PayNoteItem pn = UtilFun.cast_t(data);

        TextView tv = vh.getView(R.id.tv_pay_title);
        tv.setText(pn.getInfo());

        BudgetItem bi = pn.getBudget();
        String b_name = bi == null ? "" : bi.getName();
        if (!UtilFun.StringIsNullOrEmpty(b_name)) {
            tv = vh.getView(R.id.tv_pay_budget);
            tv.setText(b_name);
        } else {
            tv = vh.getView(R.id.tv_pay_budget);
            tv.setVisibility(View.INVISIBLE);

            ImageView iv = vh.getView(R.id.iv_pay_budget);
            iv.setVisibility(View.INVISIBLE);
        }

        tv = vh.getView(R.id.tv_pay_amount);
        tv.setText(String.format(Locale.CHINA, "- %.02f", pn.getVal()));

        tv = vh.getView(R.id.tv_pay_time);
        tv.setText(pn.getTs().toString().substring(11, 16));

        // for look detail
        ImageView iv = vh.getView(R.id.iv_pay_action);
        iv.setVisibility(View.GONE);

        RelativeLayout rl_pay = vh.getView(R.id.rl_pay);
        rl_pay.setOnClickListener(v -> {
            Intent intent;
            intent = new Intent(rl_pay.getContext(), ACPreveiwAndEdit.class);
            intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_ID, data.getId());
            intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, GlobalDef.STR_RECORD_PAY);

            (rl_pay.getContext()).startActivity(intent);
        });

        // for budget
        if (UtilFun.StringIsNullOrEmpty(b_name)) {
            RelativeLayout rl = vh.getView(R.id.rl_budget);
            rl.setVisibility(View.GONE);
        }

        // for note
        String nt = pn.getNote();
        if (UtilFun.StringIsNullOrEmpty(nt)) {
            RelativeLayout rl = vh.getView(R.id.rl_pay_note);
            rl.setVisibility(View.GONE);
        } else {
            tv = vh.getView(R.id.tv_pay_note);
            tv.setText(nt);
        }
    }

    /**
     * 当前节点是income时初始化
     *
     * @param vh   view holder
     * @param data 数据
     */
    private void initIncome(FastViewHolder vh, INote data) {
        IncomeNoteItem i_n = UtilFun.cast_t(data);

        TextView tv = vh.getView(R.id.tv_income_title);
        tv.setText(i_n.getInfo());

        tv = vh.getView(R.id.tv_income_amount);
        tv.setText(String.format(Locale.CHINA, "%.02f", i_n.getVal()));

        tv = vh.getView(R.id.tv_income_time);
        tv.setText(i_n.getTs().toString().substring(11, 16));

        // for look detail
        ImageView iv = vh.getView(R.id.iv_income_action);
        iv.setVisibility(View.GONE);
        RelativeLayout rl_income = vh.getView(R.id.rl_income);
        rl_income.setOnClickListener(v -> {
            Intent intent;
            intent = new Intent(rl_income.getContext(), ACPreveiwAndEdit.class);
            intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_ID, data.getId());
            intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, GlobalDef.STR_RECORD_INCOME);

            (rl_income.getContext()).startActivity(intent);
        });

        // for note
        String nt = i_n.getNote();
        if (UtilFun.StringIsNullOrEmpty(nt)) {
            RelativeLayout rl = vh.getView(R.id.rl_income_note);
            rl.setVisibility(View.GONE);
        } else {
            tv = vh.getView(R.id.tv_income_note);
            tv.setText(nt);
        }
    }
}
