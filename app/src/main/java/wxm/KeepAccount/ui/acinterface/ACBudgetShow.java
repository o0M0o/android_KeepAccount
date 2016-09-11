package wxm.KeepAccount.ui.acinterface;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.wxm.andriodutillib.capricorn.RayMenu;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.db.BudgetItem;
import wxm.KeepAccount.Base.utility.ContextUtil;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acutility.ACBudgetEdit;


/**
 * 显示预算数据
 */
public class ACBudgetShow extends AppCompatActivity implements View.OnClickListener {
    public static final String INTENT_LOAD_BUDGET   = "LOAD_BUDGET";

    public static final String FIELD_VAL_ENABLE     = "ENABLE";
    public static final String FIELD_VAL_DISABLE    = "DISABLE";

    public static final String FIELD_DELETE_STATUS  = "DELETE_STATUS";
    public static final String FIELD_EDIT_STATUS    = "EDIT_STATUS";

    public static final String FIELD_BUDGET_ID      = "BUDGET_ID";
    public static final String FIELD_BUDGET_NAME    = "BUDGET_NAME";
    public static final String FIELD_BUDGET_AMOUNT  = "BUDGET_AMOUNT";
    public static final String FIELD_BUDGET_NOTE    = "BUDGET_NOTE";


    private static final String TAG = "ACBudgetShow";
    private static final int[] ITEM_DRAWABLES = {
            R.drawable.ic_leave
            ,R.drawable.ic_edit
            ,R.drawable.ic_add};


    private final ArrayList<HashMap<String, String>> mLVListData = new ArrayList<>();
    private ACSBAdapter mLVAdapter;
    private ListView    mLVBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_budget_show);

        initView(savedInstanceState);
        loadBudget();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acbar_back_help, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.acb_mi_help : {
                Intent intent = new Intent(this, ACHelp.class);
                intent.putExtra(ACHelp.STR_HELP_TYPE, ACHelp.STR_HELP_RECORD);

                startActivityForResult(intent, 1);
            }
            break;

            case R.id.acb_mi_leave: {
                int ret_data = AppGobalDef.INTRET_USR_LOGOUT;

                Intent data = new Intent();
                setResult(ret_data, data);
                finish();
            }
            break;

            default:
                return super.onOptionsItemSelected(item);

        }

        return true;
    }


    /**
     * 初始化视图
     * @param savedInstanceState activity创建参数
     */
    private void initView(Bundle savedInstanceState) {
        // init ray menu
        RayMenu rayMenu = UtilFun.cast(findViewById(R.id.rm_budget_show));
        assert null != rayMenu;
        final int itemCount = ITEM_DRAWABLES.length;
        for (int i = 0; i < itemCount; i++) {
            ImageView item = new ImageView(this);
            item.setImageResource(ITEM_DRAWABLES[i]);

            final int position = ITEM_DRAWABLES[i];
            rayMenu.addItem(item, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnRayMenuClick(position);
                }
            });// Add a menu item
        }

        // init lv adapter
        mLVBudget = UtilFun.cast(findViewById(R.id.lv_budget));
        mLVAdapter= new ACSBAdapter(this,
                ContextUtil.getInstance(),
                mLVListData,
                new String[]{FIELD_BUDGET_NAME, FIELD_BUDGET_AMOUNT, FIELD_BUDGET_NOTE},
                new int[]{R.id.tv_budget_name, R.id.tv_budget_amount, R.id.tv_budget_note});

        mLVBudget.setAdapter(mLVAdapter);
    }

    /**
     * 加载budget数据并显示
     */
    private void loadBudget()   {
        mLVListData.clear();

        List<BudgetItem> bils = AppModel.getBudgetUtility().GetBudget();
        if(!ToolUtil.ListIsNullOrEmpty(bils))    {
            for(BudgetItem i : bils)    {
                AppModel.getBudgetUtility().RefrashBudgetById(i.get_id());
            }

            bils = AppModel.getBudgetUtility().GetBudget();
            if(!ToolUtil.ListIsNullOrEmpty(bils)) {
                for (BudgetItem i : bils) {
                    HashMap<String, String> hm = new HashMap<>();
                    hm.put(FIELD_BUDGET_ID, String.valueOf(i.get_id()));
                    hm.put(FIELD_BUDGET_NAME, i.getName());
                    hm.put(FIELD_BUDGET_AMOUNT
                            , String.format(Locale.CHINA
                                    , "预算金额 : %.02f(已使用 : %.02f)\n预算余额 : %.02f"
                                    , i.getAmount(), i.getAmount().subtract(i.getRemainderAmount())
                                    , i.getRemainderAmount()));

                    hm.put(FIELD_BUDGET_NOTE, "备注 : " + i.getNote());

                    hm.put(FIELD_EDIT_STATUS, FIELD_VAL_DISABLE);
                    hm.put(FIELD_DELETE_STATUS, FIELD_VAL_DISABLE);

                    mLVListData.add(hm);
                }
            }
        }

        mLVAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Boolean bModify = false;
        if (AppGobalDef.INTRET_SURE == resultCode) {
            Log.i(TAG, "从'添加记录'页面返回");
            bModify = true;
        } else if (AppGobalDef.INTRET_GIVEUP == resultCode) {
            Log.i(TAG, "从详情页面返回");
            bModify = false;
        } else {
            Log.d(TAG, String.format("不处理的resultCode(%d)!", resultCode));
        }

        if (bModify) {
            loadBudget();
        }
    }

    /**
     * raymenu点击事件
     * @param resid 点击发生的资源ID
     */
    private void OnRayMenuClick(int resid)  {
        switch (resid)  {
            case R.drawable.ic_add :    {
                Intent intent = new Intent(this, ACBudgetEdit.class);
                startActivityForResult(intent, 1);
            }
            break;

            case R.drawable.ic_edit:     {
                for(HashMap<String, String> imap : mLVListData) {
                    imap.put(FIELD_DELETE_STATUS, FIELD_VAL_ENABLE);
                    imap.put(FIELD_EDIT_STATUS, FIELD_VAL_ENABLE);
                }

                mLVAdapter.notifyDataSetChanged();
            }
            break;

            case R.drawable.ic_leave :  {
                int ret_data = AppGobalDef.INTRET_USR_LOGOUT;

                Intent data = new Intent();
                setResult(ret_data, data);
                finish();
            }
            break;

            default:
                Log.e(TAG, "未处理的resid : " + resid);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int pos = mLVBudget.getPositionForView(v);
        if(ListView.INVALID_POSITION != pos) {
            HashMap<String, String> map = mLVListData.get(pos);
            String dataid = map.get(FIELD_BUDGET_ID);
            assert null != dataid;
            int it_id = Integer.parseInt(dataid);

            int vid = v.getId();
            switch (vid) {
                case R.id.ib_budget_edit: {
                    BudgetItem bi = AppModel.getBudgetUtility().GetBudgetById(it_id);
                    Intent intent = new Intent(this, ACBudgetEdit.class);
                    intent.putExtra(INTENT_LOAD_BUDGET, bi);

                    startActivityForResult(intent, 1);
                }
                break;

                case R.id.ib_budget_delete: {
                    AppModel.getBudgetUtility().DeleteBudgetById(it_id);
                    loadBudget();
                }
                break;

                default:
                    Log.e(TAG, "未支持的点击，view id = " + vid);
                    break;
            }
        }
    }

    /**
     * 本activity用的列表adapter
     */
    public class ACSBAdapter extends SimpleAdapter {
        private ACBudgetShow mHome;
        private ArrayList<HashMap<String, String>> mLVList;

        private static final int    BTDRAW_WIDTH    = 48;
        private static final int    BTDRAW_HEIGHT   = 48;

        public ACSBAdapter(ACBudgetShow home,
                           Context context, List<? extends Map<String, ?>> data,
                           String[] from,
                           int[] to) {
            super(context, data, R.layout.li_budget, from, to);

            mHome = home;
            mLVList = UtilFun.cast(data);
        }

        @Override
        public View getView(final int position, View view, ViewGroup arg2) {
            View v = super.getView(position, view, arg2);
            if(null != v)   {
                ImageButton ibedit = UtilFun.cast(v.findViewById(R.id.ib_budget_edit));
                ImageButton ibdelete = UtilFun.cast(v.findViewById(R.id.ib_budget_delete));
                assert null != ibedit && null != ibdelete;

                HashMap<String, String> map = mLVList.get(position);
                String editst = map.get(ACBudgetShow.FIELD_EDIT_STATUS);
                String delst = map.get(ACBudgetShow.FIELD_DELETE_STATUS);
                assert null != editst && null != delst;

                if(editst.equals(ACBudgetShow.FIELD_VAL_ENABLE))    {
                    ibedit.setVisibility(View.VISIBLE);
                    ibedit.getBackground().setAlpha(0);
                    ibedit.setOnClickListener(mHome);

                    Drawable dr = ibedit.getDrawable();
                    dr.setBounds(0, 0, BTDRAW_WIDTH, BTDRAW_HEIGHT);
                    ibedit.setImageDrawable(dr);

                    ibedit.setMaxWidth(BTDRAW_WIDTH);
                    ibedit.setMinimumWidth(BTDRAW_WIDTH);
                    ibedit.setMaxHeight(BTDRAW_HEIGHT);
                    ibedit.setMinimumHeight(BTDRAW_HEIGHT);
                }   else    {
                    ibedit.setVisibility(View.INVISIBLE);
                }

                if(delst.equals(ACBudgetShow.FIELD_VAL_ENABLE))    {
                    ibdelete.setVisibility(View.VISIBLE);
                    ibdelete.getBackground().setAlpha(0);
                    ibdelete.setOnClickListener(mHome);

                    Drawable dr = ibdelete.getDrawable();
                    dr.setBounds(0, 0, BTDRAW_WIDTH, BTDRAW_HEIGHT);
                    ibdelete.setImageDrawable(dr);
                }   else    {
                    ibdelete.setVisibility(View.INVISIBLE);
                }
            }

            return v;
        }
    }
}
