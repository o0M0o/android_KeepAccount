package wxm.KeepAccount.ui.utility;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    public final static String K_NODE = "node";
    private final static String LOG_TAG = "AdapterNoteDetail";
    private Context mCTSelf;

    /**
     * if set to true can delete data
     */
    private boolean mBLCanDelete = false;
    private ArrayList<INote> mALDelNotes;

    public AdapterNoteDetail(Context context, List<? extends Map<String, ?>> data,
                             String[] from, int[] to) {
        super(context, data, R.layout.li_data_detail, from, to);
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
     * set whether can delete data
     * @param bdel      if true then can delete data
     */
    public void setCanDelete(boolean bdel) {
        mBLCanDelete = bdel;
    }


    /**
     * get nodes wait deleted
     * @return  nodes wait deleted
     */
    public List<INote> getWantDeleteNotes() {
        return mALDelNotes;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        FastViewHolder vh = FastViewHolder.get(mCTSelf, convertView,
                R.layout.li_data_detail);

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
     * init delete action
     * @param vh        view holder
     * @param data      data
     * @param bflag     if true then data can delete
     */
    private void initDelAction(FastViewHolder vh, INote data, boolean bflag) {
        if (!bflag)
            mALDelNotes.clear();

        RelativeLayout rl_del = vh.getView(R.id.rl_delete);
        rl_del.setVisibility(bflag ? View.VISIBLE : View.GONE);
        rl_del.setOnClickListener(view -> {
            if (mALDelNotes.contains(data)) {
                mALDelNotes.remove(data);
                rl_del.setBackgroundColor(ResourceHelper.mCRLVItemNoSel);
            } else {
                mALDelNotes.add(data);
                rl_del.setBackgroundColor(ResourceHelper.mCRLVItemSel);
            }
        });
    }

    /**
     * init pay node
     * @param vh    view holder
     * @param data  pay data
     */
    private void initPay(FastViewHolder vh, INote data) {
        PayNoteItem pn = data.toPayNote();

        vh.setText(R.id.tv_pay_title, pn.getInfo());

        BudgetItem bi = pn.getBudget();
        String b_name = bi == null ? "" : bi.getName();
        if (!UtilFun.StringIsNullOrEmpty(b_name)) {
            vh.setText(R.id.tv_pay_budget, b_name);
        } else {
            vh.getView(R.id.tv_pay_budget).setVisibility(View.GONE);
            vh.getView(R.id.iv_pay_budget).setVisibility(View.GONE);
        }

        vh.setText(R.id.tv_pay_amount, String.format(Locale.CHINA, "- %s", pn.getValToStr()));
        vh.setText(R.id.tv_pay_time, pn.getTs().toString().substring(11, 16));

        // for look detail
        vh.getView(R.id.iv_pay_action).setVisibility(View.GONE);

        vh.getView(R.id.rl_pay).setOnClickListener(v -> {
            Intent intent;
            intent = new Intent(mCTSelf, ACPreveiwAndEdit.class);
            intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_ID, data.getId());
            intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, GlobalDef.STR_RECORD_PAY);

            mCTSelf.startActivity(intent);
        });

        // for budget
        if (UtilFun.StringIsNullOrEmpty(b_name)) {
            vh.getView(R.id.rl_budget).setVisibility(View.GONE);
        }

        // for note
        String nt = pn.getNote();
        if (UtilFun.StringIsNullOrEmpty(nt)) {
            vh.getView(R.id.rl_pay_note).setVisibility(View.GONE);
        } else {
            vh.setText(R.id.tv_pay_note, nt);
        }
    }

    /**
     * init income node
     * @param vh    view holder
     * @param data  income data
     */
    private void initIncome(FastViewHolder vh, INote data) {
        IncomeNoteItem i_n = data.toIncomeNote();

        vh.setText(R.id.tv_income_title, i_n.getInfo());

        vh.setText(R.id.tv_income_amount, i_n.getValToStr());
        vh.setText(R.id.tv_income_time, i_n.getTs().toString().substring(11, 16));

        // for look detail
        vh.getView(R.id.iv_income_action).setVisibility(View.GONE);

        vh.getView(R.id.rl_income).setOnClickListener(v -> {
            Intent intent;
            intent = new Intent(mCTSelf, ACPreveiwAndEdit.class);
            intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_ID, data.getId());
            intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, GlobalDef.STR_RECORD_INCOME);

            mCTSelf.startActivity(intent);
        });

        // for note
        String nt = i_n.getNote();
        if (UtilFun.StringIsNullOrEmpty(nt)) {
            vh.getView(R.id.rl_income_note).setVisibility(View.GONE);
        } else {
            vh.setText(R.id.tv_income_note, nt);
        }
    }
}
