package wxm.KeepAccount.ui.acutility;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import butterknife.ButterKnife;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.define.BaseAppCompatActivity;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.fragment.EditData.TFEditRecordInfo;
import wxm.KeepAccount.ui.fragment.utility.FrgUsrAdd;

public class ACRecordInfoEdit extends BaseAppCompatActivity {
    public final static String  IT_PARA_RECORDTYPE = "record_type";
    private TFEditRecordInfo    mTFRecordInfo = new TFEditRecordInfo();

    @Override
    protected void leaveActivity() {
        int ret_data = AppGobalDef.INTRET_GIVEUP;
        Intent data = new Intent();
        setResult(ret_data, data);
        finish();
    }

    @Override
    protected void initUi(Bundle savedInstanceState) {
        Intent it = getIntent();
        mTFRecordInfo.setCurData("", it.getStringExtra(IT_PARA_RECORDTYPE));

        LOG_TAG = "ACRecordInfoEdit";
        mFGSupportHolder = mTFRecordInfo;
        super.initUi(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acm_preview_edit, menu);

        MenuItem mMISwitch = menu.findItem(R.id.mi_switch);
        MenuItem mMIGiveup = menu.findItem(R.id.mi_giveup);
        mMISwitch.setVisible(false);
        mMIGiveup.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_save: {
                if(mTFRecordInfo.onAccept())    {
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
