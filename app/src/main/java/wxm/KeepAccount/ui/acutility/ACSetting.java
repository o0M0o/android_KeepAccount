package wxm.KeepAccount.ui.acutility;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import wxm.KeepAccount.ui.fragment.Setting.TFSettingBase;
import wxm.KeepAccount.ui.fragment.utility.FrgSetting;

/**
 * for app setting
 */
public class ACSetting extends BaseAppCompatActivity {
    private FrgSetting mFGSetting = new FrgSetting();

    @Override
    protected void leaveActivity() {
        if(FrgSetting.PAGE_IDX_MAIN != mFGSetting.getCurrentItem()) {
            mFGSetting.change_page(FrgSetting.PAGE_IDX_MAIN);
        } else {
            int ret_data = AppGobalDef.INTRET_GIVEUP;
            Intent data = new Intent();
            setResult(ret_data, data);
            finish();
        }
    }

    @Override
    protected void initFrgHolder() {
        LOG_TAG = "ACRecordInfoEdit";
        mFGHolder = mFGSetting;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acm_save_giveup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_save: {
                if(FrgSetting.PAGE_IDX_MAIN != mFGSetting.getCurrentItem()) {
                    final TFSettingBase tb = mFGSetting.getCurrentPage();
                    if(tb.isSettingDirty()) {
                        Dialog alertDialog = new AlertDialog.Builder(this).
                                setTitle("配置已经更改").
                                setMessage("是否保存更改的配置?").
                                setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        tb.updateSetting();
                                        mFGSetting.change_page(FrgSetting.PAGE_IDX_MAIN);
                                    }
                                }).
                                setNegativeButton("否", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mFGSetting.change_page(FrgSetting.PAGE_IDX_MAIN);
                                    }
                                }).
                                create();
                        alertDialog.show();
                    } else  {
                        mFGSetting.change_page(FrgSetting.PAGE_IDX_MAIN);
                    }
                } else  {
                    int ret_data = AppGobalDef.INTRET_SURE;
                    Intent data = new Intent();
                    setResult(ret_data, data);
                    finish();
                }
            }
            break;

            case R.id.mi_giveup:    {
                if(FrgSetting.PAGE_IDX_MAIN != mFGSetting.getCurrentItem()) {
                    mFGSetting.change_page(FrgSetting.PAGE_IDX_MAIN);
                } else {
                    int ret_data = AppGobalDef.INTRET_GIVEUP;
                    Intent data = new Intent();
                    setResult(ret_data, data);
                    finish();
                }
            }
            break;

            default:
                return super.onOptionsItemSelected(item);

        }

        return true;
    }

    /**
     * 切换到新页面
     * @param new_page 新页面postion
     */
    public void change_page(int new_page)  {
        mFGSetting.setCurrentItem(new_page);
    }
}
