package wxm.KeepAccount.ui.acutility;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.fragment.EditData.TFEditRecordInfo;

public class ACRecordInfoEdit extends AppCompatActivity {
    public final static String  IT_PARA_RECORDTYPE = "record_type";

    private TFEditRecordInfo        mTFRecordInfo;

    // for menu
    private MenuItem    mMISwitch;
    private MenuItem    mMISave;
    private MenuItem    mMIGiveup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_record_info_edit);

        // init view
        mTFRecordInfo = new TFEditRecordInfo();
        Intent it = getIntent();
        mTFRecordInfo.setCurData("", it.getStringExtra(IT_PARA_RECORDTYPE));

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_holder, mTFRecordInfo);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acm_preview_edit, menu);

        mMISwitch = menu.findItem(R.id.mi_switch);
        mMISave   = menu.findItem(R.id.mi_save);
        mMIGiveup = menu.findItem(R.id.mi_giveup);
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
