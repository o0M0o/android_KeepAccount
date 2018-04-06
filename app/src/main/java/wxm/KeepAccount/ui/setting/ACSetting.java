package wxm.KeepAccount.ui.setting;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import wxm.KeepAccount.ui.setting.page.TFSettingBase;
import wxm.KeepAccount.ui.setting.page.TFSettingChartColor;
import wxm.KeepAccount.ui.setting.page.TFSettingCheckVersion;
import wxm.KeepAccount.ui.setting.page.TFSettingMain;
import wxm.KeepAccount.ui.setting.page.TFSettingRemind;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.GlobalDef;
import wxm.androidutil.Switcher.ACSwitcherActivity;

/**
 * for app setting
 */
public class ACSetting extends ACSwitcherActivity<TFSettingBase> {
    private TFSettingMain           mTFMain         = new TFSettingMain();
    private TFSettingChartColor     mTFChartColor   = new TFSettingChartColor();
    private TFSettingCheckVersion   mTFCheckVer     = new TFSettingCheckVersion();
    private TFSettingRemind         mTFRemind       = new TFSettingRemind();

    @Override
    protected void leaveActivity() {
        if (mTFMain !=  getHotFragment()) {
            switchToFragment(mTFMain);
        } else {
            int ret_data = GlobalDef.INTRET_GIVEUP;
            Intent data = new Intent();
            setResult(ret_data, data);
            finish();
        }
    }

    @Override
    protected void setupFragment(Bundle bundle) {
        addFragment(mTFMain);
        addFragment(mTFChartColor);
        addFragment(mTFCheckVer);
        addFragment(mTFRemind);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mu_save_giveup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_save: {
                if (mTFMain != getHotFragment()) {
                    final TFSettingBase tb = getHotFragment();
                    final ACSwitcherActivity<TFSettingBase> h = this;
                    if (tb.isSettingDirty()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("配置已经更改");
                        builder.setMessage("是否保存更改的配置?");
                        builder.setPositiveButton("是", (DialogInterface dialog, int which) -> {
                            tb.updateSetting();
                            h.switchToFragment(mTFMain);
                        });
                        builder.setNegativeButton("否", (dialog, which) -> h.switchToFragment(mTFMain));
                        Dialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        switchToFragment(mTFMain);
                    }
                } else {
                    int ret_data = GlobalDef.INTRET_SURE;
                    Intent data = new Intent();
                    setResult(ret_data, data);
                    finish();
                }
            }
            break;

            case R.id.mi_giveup: {
                if (mTFMain != getHotFragment()) {
                    switchToFragment(mTFMain);
                } else {
                    int ret_data = GlobalDef.INTRET_GIVEUP;
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

    public void switchToPageByType(String pageType)    {
        if(pageType.equals(TFSettingChartColor.class.getName()))    {
            switchToFragment(mTFChartColor);
        } else if(pageType.equals(TFSettingCheckVersion.class.getName()))   {
            switchToFragment(mTFCheckVer);
        }
    }
}
