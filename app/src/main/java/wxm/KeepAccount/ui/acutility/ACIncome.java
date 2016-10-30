package wxm.KeepAccount.ui.acutility;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.db.IncomeNoteItem;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.fragment.EditData.TFIncome;
import wxm.KeepAccount.ui.fragment.base.ITFBase;

public class ACIncome extends AppCompatActivity {
    private final TFIncome mTFIncome = new TFIncome();

    private final static String     CHANGETO_PREVIEW = "预览";
    private final static String     CHANGETO_EDIT    = "编辑";

    private MenuItem    mMISwitch;
    private MenuItem    mMISave;
    private MenuItem    mMIGiveup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_income);
        if(null == savedInstanceState)  {
            Intent it = getIntent();
            int id = it.getIntExtra(AppGobalDef.INTENT_LOAD_RECORD_ID, -1);
            IncomeNoteItem pi = -1 != id ? AppModel.getPayIncomeUtility().GetIncomeNoteById(id) : null;
            ITFBase ib = mTFIncome;
            ib.setCurData(pi == null ? AppGobalDef.STR_CREATE : AppGobalDef.STR_MODIFY, pi);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fl_income, mTFIncome);
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acm_budget, menu);

        mMISwitch = menu.findItem(R.id.mi_switch);
        mMISave   = menu.findItem(R.id.mi_save);
        mMIGiveup = menu.findItem(R.id.mi_giveup);
        mMISwitch.setTitle(mTFIncome.isPreviewPage() ? CHANGETO_EDIT : CHANGETO_PREVIEW);
        mMISave.setVisible(mTFIncome.isEditPage());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_switch : {
                mMISave.setVisible(mTFIncome.isPreviewPage());
                mMISwitch.setTitle(mTFIncome.isEditPage() ? CHANGETO_EDIT : CHANGETO_PREVIEW);
                if(mTFIncome.isPreviewPage())
                    mTFIncome.toEditPage();
                else
                    mTFIncome.toPreviewPage();
            }
            break;

            case R.id.mi_save: {
                ITFBase ib = mTFIncome;
                if(ib.onAccept())    {
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
