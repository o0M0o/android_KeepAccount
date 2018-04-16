package wxm.KeepAccount.ui.utility;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import wxm.KeepAccount.ui.base.Helper.ViewHelper;
import wxm.KeepAccount.utility.ContextUtil;
import wxm.androidutil.util.FastViewHolder;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.BudgetItem;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.define.INote;
import wxm.KeepAccount.define.IncomeNoteItem;
import wxm.KeepAccount.define.PayNoteItem;
import wxm.KeepAccount.ui.data.edit.Note.ACPreveiwAndEdit;
import wxm.uilib.SwipeLayout.SwipeLayout;

/**
 * adapter for note detail
 * Created by WangXM on 2017/1/23.
 */
public class AdapterNoteDetail extends SimpleAdapter {
    class vwTag {
        View mContent;
        View mRight;
    }

    public final static String K_NODE = "node";
    private Context mCTSelf;

    public AdapterNoteDetail(Context context, List<? extends Map<String, ?>> data) {
        super(context, data, R.layout.liit_data_swipe_holder, new String[]{}, new int[]{});
        mCTSelf = context;
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
        FastViewHolder vh = FastViewHolder.get(mCTSelf, convertView, R.layout.liit_data_swipe_holder);
        HashMap<String, INote> hmData = UtilFun.cast_t(getItem(position));
        INote itemData = hmData.get(K_NODE);

        SwipeLayout sl = vh.getView(R.id.swipe);
        vwTag vt = (vwTag)sl.getTag();
        if(null == vt) {
            ViewHelper vhSwipe = new ViewHelper(sl);

            vt = new vwTag();
            if(itemData.isPayNote()) {
                vhSwipe.setVisibility(R.id.rl_income, View.GONE);

                vt.mContent = vhSwipe.getChildView(R.id.rl_pay);
                initPay(vt.mContent, itemData);
            } else  {
                vhSwipe.setVisibility(R.id.rl_pay, View.GONE);

                vt.mContent = vhSwipe.getChildView(R.id.rl_income);
                initIncome(vt.mContent, itemData);
            }

            vt.mRight = vhSwipe.getChildView(R.id.cl_swipe_right);
            ViewHelper vhRight = new ViewHelper(vt.mRight);
            vhRight.setTag(R.id.iv_delete, itemData);
            vhRight.setTag(R.id.iv_edit, itemData);

            vhRight.getChildView(R.id.iv_delete).setOnClickListener(v -> {
                Dialog alertDialog = new AlertDialog.Builder(mCTSelf).
                        setTitle("删除数据").
                        setMessage("此操作不能恢复，是否继续操作!").
                        setPositiveButton("是", (dialog, which) -> {
                            INote data = (INote)v.getTag();
                            List<Integer> al = Collections.singletonList(data.getId());

                            if(data.isPayNote())    {
                                ContextUtil.getPayIncomeUtility().deletePayNotes(al);
                            } else  {
                                ContextUtil.getPayIncomeUtility().deleteIncomeNotes(al);
                            }
                        }).
                        setNegativeButton("否", (dialog, which) -> {}).
                        create();
                alertDialog.show();
            });

            vhRight.getChildView(R.id.iv_edit).setOnClickListener(v -> {
                Intent intent;
                INote data = (INote)v.getTag();
                intent = new Intent(mCTSelf, ACPreveiwAndEdit.class);
                intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_ID, data.getId());
                intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE,
                        data.isPayNote() ? GlobalDef.STR_RECORD_PAY : GlobalDef.STR_RECORD_INCOME);

                mCTSelf.startActivity(intent);
            });

            sl.setTag(vt);
        }

        return vh.getConvertView();
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

        // for note
        String nt = i_n.getNote();
        if (UtilFun.StringIsNullOrEmpty(nt)) {
            vHelper.setVisibility(R.id.rl_income_note, View.GONE);
        } else {
            vHelper.setText(R.id.tv_income_note, nt);
        }
    }
}
