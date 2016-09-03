package wxm.KeepAccount.ui.acinterface;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.wxm.andriodutillib.capricorn.RayMenu;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.db.BudgetItem;
import wxm.KeepAccount.Base.utility.ACSBAdapter;
import wxm.KeepAccount.Base.utility.ContextUtil;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acutility.ACBudgetEdit;


/**
 * 显示预算数据
 */
public class ACShowBudget extends AppCompatActivity implements View.OnClickListener {
    public static final String INTENT_LOAD_BUDGET   = "LOAD_BUDGET";

    public static final String FIELD_VAL_ENABLE     = "ENABLE";
    public static final String FIELD_VAL_DISABLE    = "DISABLE";

    public static final String FIELD_DELETE_STATUS  = "DELETE_STATUS";
    public static final String FIELD_EDIT_STATUS    = "EDIT_STATUS";

    public static final String FIELD_BUDGET_ID      = "BUDGET_ID";
    public static final String FIELD_BUDGET_NAME    = "BUDGET_NAME";
    public static final String FIELD_BUDGET_AMOUNT  = "BUDGET_AMOUNT";
    public static final String FIELD_BUDGET_DATE    = "BUDGET_DATE";


    private static final String TAG = "ACShowBudget";
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
                new String[]{FIELD_BUDGET_NAME, FIELD_BUDGET_AMOUNT, FIELD_BUDGET_DATE},
                new int[]{R.id.tv_budget_name, R.id.tv_budget_amount, R.id.tv_budget_date});

        mLVBudget.setAdapter(mLVAdapter);
    }

    /**
     * 加载budget数据并显示
     */
    private void loadBudget()   {
        mLVListData.clear();

        List<BudgetItem> bils = AppModel.getBudgetUtility().GetBudget();
        if(null != bils)    {
            for(BudgetItem i : bils)    {
                HashMap<String, String> hm = new HashMap<>();
                hm.put(FIELD_BUDGET_ID, String.valueOf(i.get_id()));
                hm.put(FIELD_BUDGET_NAME, i.getName());
                hm.put(FIELD_BUDGET_AMOUNT
                        ,String.format(Locale.CHINA ,"预算金额 : %.02f" ,i.getAmount()));
                hm.put(FIELD_BUDGET_DATE
                        ,String.format(Locale.CHINA ,"开始时间 : %s\n结束时间 : %s"
                                ,ToolUtil.DateToDateStr(i.getStartDate())
                                ,ToolUtil.DateToDateStr(i.getEndDate())));

                hm.put(FIELD_EDIT_STATUS, FIELD_VAL_DISABLE);
                hm.put(FIELD_DELETE_STATUS, FIELD_VAL_DISABLE);

                mLVListData.add(hm);
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
                }
                break;

                default:
                    Log.e(TAG, "未支持的点击，view id = " + vid);
                    break;
            }
        }
    }
}
