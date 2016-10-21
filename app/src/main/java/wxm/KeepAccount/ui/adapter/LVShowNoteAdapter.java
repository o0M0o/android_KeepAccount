package wxm.KeepAccount.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;

/**
 * Created by 123 on 2016/10/21.
 *
 * listview adapter for note show
 * have items as below
 *   -- 月度项
 *   -- 收入项
 *   -- 支出项
 */
public class LVShowNoteAdapter extends SimpleAdapter
        implements View.OnClickListener {
    private final static String TAG = "LVShowNoteAdapter";

    /// for item data begin
    public final static String KEY_TAG         = "k_tag";

    // for color
    public final static String KEY_BACK_COLOR  = "k_back_color";

    // for item type begin
    public final static String KEY_TYPE        = "k_type";
    public final static String VAL_MONTH       = "v_month";
    public final static String VAL_PAY         = "v_pay";
    public final static String VAL_INCOME      = "v_income";
    // for item type end

    // for item show_or_hide begin
    public final static String KEY_ITEM_SHOW_OR_HIDE    = "ki_show_or_hide";
    public final static String KEY_SHOW_OR_HIDE         = "k_show_or_hide";
    public final static String VAL_SHOW                 = "v_show";
    public final static String VAL_HIDE                 = "v_hide";
    // for item show_or_hide end

    // for others
    public final static String KEY_SIMPLE_SHOW     = "k_simple_show";

    public final static String KEY_DAY_NUMBER      = "k_day_number";
    public final static String KEY_DAY_IN_WEEK     = "k_day_in_week";
    public final static String KEY_ARISE_TIME      = "k_arise_time";
    public final static String KEY_ARISE_AMOUNT    = "k_arise_amount";
    public final static String KEY_RECORD_INFO     = "k_record_info";
    /// for item data end

    private ListView    mLVHolder;
    private Context     mCTHolder;

    public LVShowNoteAdapter(Context context, ListView lv,
                             List<? extends Map<String, ?>> mdata,
                             String[] from, int[] to) {
        super(context, mdata, R.layout.li_test_detail, from, to);
        mLVHolder = lv;
        mCTHolder = context;
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
    public View getView(final int position, View view, ViewGroup arg2) {
        View v = super.getView(position, view, arg2);
        if(null != v)   {
            HashMap<String, String> hm = UtilFun.cast(getItem(position));
            RelativeLayout rl_month = UtilFun.cast_t(v.findViewById(R.id.lo_month));
            RelativeLayout rl_income = UtilFun.cast_t(v.findViewById(R.id.lo_income));
            RelativeLayout rl_pay = UtilFun.cast_t(v.findViewById(R.id.lo_pay));
            if(VAL_HIDE.equals(hm.get(KEY_ITEM_SHOW_OR_HIDE)))  {
                setLayoutVisible(rl_income, View.INVISIBLE);
                setLayoutVisible(rl_pay, View.INVISIBLE);
                setLayoutVisible(rl_month, View.INVISIBLE);
            }   else {
                v.setBackgroundColor(Integer.parseInt(hm.get(KEY_BACK_COLOR)));
                v.setOnClickListener(this);

                String it = hm.get(KEY_TYPE);
                switch (it) {
                    case VAL_MONTH: {
                        setLayoutVisible(rl_income, View.INVISIBLE);
                        setLayoutVisible(rl_pay, View.INVISIBLE);
                        init_month(rl_month, hm);
                    }
                    break;

                    case VAL_PAY: {
                        setLayoutVisible(rl_month, View.INVISIBLE);
                        setLayoutVisible(rl_income, View.INVISIBLE);
                        init_pay(rl_pay, hm);
                    }
                    break;

                    case VAL_INCOME: {
                        setLayoutVisible(rl_month, View.INVISIBLE);
                        setLayoutVisible(rl_pay, View.INVISIBLE);
                        init_income(rl_income, hm);
                    }
                    break;
                }
            }
        }

        return v;
    }

    /**
     * 初始化“月信息”节点
     * @param rl    father view
     * @param hm    data for view
     */
    private void init_month(RelativeLayout rl, HashMap<String, String> hm)  {
        TextView tv = UtilFun.cast(rl.findViewById(R.id.tv_show));
        ToolUtil.throwExIf(null == tv);
        tv.setText(hm.get(KEY_SIMPLE_SHOW));

        ImageView iv = UtilFun.cast_t(rl.findViewById(R.id.iv_show_hide));
        if(VAL_HIDE.equals(hm.get(KEY_SHOW_OR_HIDE)))   {
            iv.setImageDrawable(mCTHolder.getResources().getDrawable(R.drawable.ic_show));
        } else  {
            iv.setImageDrawable(mCTHolder.getResources().getDrawable(R.drawable.ic_hide));
        }

        iv.setOnClickListener(this);
    }

    /**
     * 初始化“收入信息”节点
     * @param rl            father view
     * @param mHMData       data for view
     */
    private void init_income(RelativeLayout rl, HashMap<String, String> mHMData)  {
        boolean b_show_day = !UtilFun.StringIsNullOrEmpty(mHMData.get(KEY_DAY_NUMBER));
        TextView tv;
        if(b_show_day) {
            tv = UtilFun.cast(rl.findViewById(R.id.tv_day_number));
            ToolUtil.throwExIf(null == tv);
            tv.setText(mHMData.get(KEY_DAY_NUMBER));

            tv = UtilFun.cast(rl.findViewById(R.id.tv_day_in_week));
            ToolUtil.throwExIf(null == tv);
            tv.setText(mHMData.get(KEY_DAY_IN_WEEK));

        } else  {
            RelativeLayout rll = UtilFun.cast(rl.findViewById(R.id.rl_left_show));
            ToolUtil.throwExIf(null == rll);
            rll.setVisibility(View.INVISIBLE);
        }

        tv = UtilFun.cast(rl.findViewById(R.id.tv_record_type));
        ToolUtil.throwExIf(null == tv);
        tv.setText(mHMData.get(KEY_RECORD_INFO));

        tv = UtilFun.cast(rl.findViewById(R.id.tv_time));
        ToolUtil.throwExIf(null == tv);
        tv.setText(mHMData.get(KEY_ARISE_TIME));

        tv = UtilFun.cast(rl.findViewById(R.id.tv_amount));
        ToolUtil.throwExIf(null == tv);
        tv.setText(mHMData.get(KEY_ARISE_AMOUNT));
    }

    /**
     * 初始化“支出信息”节点
     * @param rl       father view
     * @param hm       data for view
     */
    private void init_pay(RelativeLayout rl, HashMap<String, String> hm)  {
        init_income(rl, hm);
    }

    /**
     * 设置layout可见性
     * 仅调整可见性，其它设置保持不变
     * @param visible  若为 :
     *                  1. {@code View.INVISIBLE}, 不可见
     *                  2. {@code View.VISIBLE}, 可见
     */
    private void setLayoutVisible(ViewGroup rl, int visible)    {
        ViewGroup.LayoutParams param = rl.getLayoutParams();
        param.width = rl.getWidth();
        param.height = View.INVISIBLE != visible ? rl.getHeight() : 0;
        rl.setLayoutParams(param);
    }

    @Override
    public void onClick(View v) {
        if(v instanceof RelativeLayout || v instanceof LinearLayout) {
            click_listitem(v);
        } else if(v instanceof ImageView)   {
            if(R.id.iv_show_hide == v.getId()) {
                click_show_hide(v);
            }
        } else  {
            Log.e(TAG, "can not process onClick at view : " + v.toString());
        }
    }

    /**
     * "展开"或者"隐藏"子节点
     * @param v  点击的view
     */
    private void click_show_hide(View v) {
        int pos = mLVHolder.getPositionForView(v);
        if(ListView.INVALID_POSITION != pos) {
            ImageView iv = UtilFun.cast(v);
            HashMap<String, String> hd = UtilFun.cast(getItem(pos));
            if(null != hd)  {
                if(VAL_HIDE.equals(hd.get(KEY_SHOW_OR_HIDE)))   {
                    show_hide_monthly(pos, View.VISIBLE);

                    hd.put(KEY_SHOW_OR_HIDE, VAL_SHOW);
                    iv.setImageDrawable(mCTHolder.getResources().getDrawable(R.drawable.ic_hide));
                } else  {
                    show_hide_monthly(pos, View.INVISIBLE);

                    hd.put(KEY_SHOW_OR_HIDE, VAL_HIDE);
                    iv.setImageDrawable(mCTHolder.getResources().getDrawable(R.drawable.ic_show));
                }
            }
        }
    }

    /**
     * 点击listview节点
     * @param v  点击的view
     */
    private void click_listitem(View v) {
        int pos = mLVHolder.getPositionForView(v);
        if (ListView.INVALID_POSITION != pos) {
            Toast.makeText(mCTHolder, "invoke click at " + pos, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void show_hide_monthly(int pos, int visible)    {
        int max_pos = getCount() - 1;
        int cc = 0;
        for(int ipos = pos + 1; ipos <= max_pos; ++ipos)    {
            HashMap<String, String> hd = UtilFun.cast(getItem(ipos));
            if(VAL_MONTH.equals(hd.get(KEY_TYPE))) {
                if(0 < cc) {
                    mLVHolder.setAdapter(this);
                    notifyDataSetChanged();
                }

                return;
            }   else    {
                cc++;
                hd.put(KEY_ITEM_SHOW_OR_HIDE, visible == View.VISIBLE ? VAL_SHOW : VAL_HIDE);
            }
        }
    }
}
