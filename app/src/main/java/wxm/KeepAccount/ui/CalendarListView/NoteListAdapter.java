package wxm.KeepAccount.ui.CalendarListView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.BudgetItem;
import wxm.KeepAccount.Base.data.INote;
import wxm.KeepAccount.Base.data.IncomeNoteItem;
import wxm.KeepAccount.Base.data.PayNoteItem;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.fragment.utility.FrgCalendarData;
import wxm.calendarlv_library.BaseCalendarListAdapter;
import wxm.calendarlv_library.CalendarHelper;


public class NoteListAdapter extends BaseCalendarListAdapter<INote> {
    private static class HeaderViewHolder {
        TextView dayText;
        TextView yearMonthText;
    }

    private static class ContentViewHolder {
        RelativeLayout      mRLPay;
        RelativeLayout      mRLIncome;
    }


    public NoteListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getSectionHeaderView(String date, View convertView, ViewGroup parent) {
        HeaderViewHolder headerViewHolder;
        if (convertView != null) {
            headerViewHolder = (HeaderViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.listitem_calendar_header, null);
            headerViewHolder = new HeaderViewHolder();
            headerViewHolder.dayText = (TextView) convertView.findViewById(R.id.header_day);
            headerViewHolder.yearMonthText = (TextView) convertView.findViewById(R.id.header_year_month);
            convertView.setTag(headerViewHolder);
        }

        Calendar calendar = CalendarHelper.getCalendarByYearMonthDay(date);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String dayStr = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        if (day < 10) {
            dayStr = "0" + dayStr;
        }
        headerViewHolder.dayText.setText(dayStr);
        headerViewHolder.yearMonthText.setText(FrgCalendarData.YEAR_MONTH_FORMAT.format(calendar.getTime()));
        return convertView;
    }

    @Override
    public View getItemView(INote model, String date, int pos, View convertView, ViewGroup parent) {
        ContentViewHolder contentViewHolder;
        if (convertView != null) {
            contentViewHolder = (ContentViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.li_daily_show_detail, null);
            contentViewHolder = createContentViewHolder(convertView, model);
        }

        loadContentViewHolder(contentViewHolder, model);
        return convertView;
    }


    /**
     * 创建内容视图容器
     * @param pv    父视图
     * @param data  数据
     * @return      内容视图容器
     */
    private ContentViewHolder createContentViewHolder(View pv, INote data)  {
        ContentViewHolder ch = new ContentViewHolder();

        ch.mRLPay = UtilFun.cast_t(pv.findViewById(R.id.rl_pay));
        ch.mRLIncome = UtilFun.cast_t(pv.findViewById(R.id.rl_income));
        ToolUtil.setViewGroupVisible(data.isPayNote() ? ch.mRLIncome : ch.mRLPay, View.INVISIBLE);

        pv.setTag(ch);
        return ch;
    }


    /**
     * 加载内容视图
     * @param ch        内容视图容器
     * @param data      数据
     */
    private void loadContentViewHolder(ContentViewHolder ch, INote data)    {
        if(data.isPayNote())    {
            initPay(ch.mRLPay, data);
        } else {
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

        //ImageView iv = UtilFun.cast_t(mRLPay.findViewById(R.id.iv_pay_action));
        //iv.setOnClickListener(this);
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
        tv.setText(String.format(Locale.CHINA, "- %.02f", i_n.getVal()));

        tv = UtilFun.cast_t(rl_income.findViewById(R.id.tv_income_time));
        tv.setText(i_n.getTs().toString().substring(11, 16));

        //ImageView iv = UtilFun.cast_t(rl_income.findViewById(R.id.iv_income_action));
        //iv.setOnClickListener(this);
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
