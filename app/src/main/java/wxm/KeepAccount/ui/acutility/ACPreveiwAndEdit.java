package wxm.KeepAccount.ui.acutility;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.data.BudgetDataUtility;
import wxm.KeepAccount.Base.data.PayIncomeDataUtility;
import wxm.KeepAccount.Base.db.BudgetItem;
import wxm.KeepAccount.Base.db.IncomeNoteItem;
import wxm.KeepAccount.Base.db.PayNoteItem;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.fragment.EditData.TFPreviewAndEdit;
import wxm.KeepAccount.ui.fragment.base.ITFBase;

public class ACPreveiwAndEdit extends AppCompatActivity {
    private ITFBase     mTFBase;

    private final static String     CHANGETO_PREVIEW = "预览";
    private final static String     CHANGETO_EDIT    = "编辑";

    private MenuItem    mMISwitch;
    private MenuItem    mMISave;
    private MenuItem    mMIGiveup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_preview_and_edit);
        if(null == savedInstanceState)  {
            Intent it = getIntent();
            String type = it.getStringExtra(AppGobalDef.INTENT_LOAD_RECORD_TYPE);
            if(UtilFun.StringIsNullOrEmpty(type)
                    || (!AppGobalDef.STR_RECORD_PAY.equals(type) && !AppGobalDef.STR_RECORD_INCOME.equals(type)
                                && !AppGobalDef.STR_RECORD_BUDGET.equals(type)))
                return;

            Object ob;
            PayIncomeDataUtility puit = AppModel.getPayIncomeUtility();
            BudgetDataUtility buit = AppModel.getBudgetUtility();
            int id = it.getIntExtra(AppGobalDef.INTENT_LOAD_RECORD_ID, -1);
            if(AppGobalDef.STR_RECORD_PAY.equals(type)) {
                PayNoteItem pi = -1 != id ? puit.GetPayNoteById(id) : null;
                ob = pi;
            } else if(AppGobalDef.STR_RECORD_BUDGET.equals(type)) {
                BudgetItem bi = -1 != id ? buit.GetBudgetById(id) : null;
                ob = bi;
            } else  {
                IncomeNoteItem ii = -1 != id ? puit.GetIncomeNoteById(id) : null;
                ob = ii;
            }

            TFPreviewAndEdit tpe = new TFPreviewAndEdit();
            tpe.setCurData(type, ob == null ? AppGobalDef.STR_CREATE : AppGobalDef.STR_MODIFY, ob);
            mTFBase = tpe;

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fl_holder, tpe);
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acm_preview_edit, menu);

        mMISwitch = menu.findItem(R.id.mi_switch);
        mMISave   = menu.findItem(R.id.mi_save);
        mMIGiveup = menu.findItem(R.id.mi_giveup);
        mMISwitch.setTitle(mTFBase.isPreviewPage() ? CHANGETO_EDIT : CHANGETO_PREVIEW);
        mMISave.setVisible(mTFBase.isEditPage());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_switch : {
                mMISave.setVisible(mTFBase.isPreviewPage());
                mMISwitch.setTitle(mTFBase.isEditPage() ? CHANGETO_EDIT : CHANGETO_PREVIEW);
                if(mTFBase.isPreviewPage())
                    mTFBase.toEditPage();
                else
                    mTFBase.toPreviewPage();
            }
            break;

            case R.id.mi_save: {
                if(mTFBase.onAccept())    {
                    int ret_data = AppGobalDef.INTRET_SURE;
                    Intent data = new Intent();
                    setResult(ret_data, data);
                    finish();
                }
            }
            break;

            case R.id.mi_giveup:    {
                int ret_data = AppGobalDef.INTRET_GIVEUP;
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
}
