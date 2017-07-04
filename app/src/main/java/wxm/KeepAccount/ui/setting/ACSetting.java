package wxm.KeepAccount.ui.setting;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import wxm.androidutil.ExActivity.BaseAppCompatActivity;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.GlobalDef;

/**
 * for app setting
 */
public class ACSetting extends BaseAppCompatActivity {
    private final FrgSetting mFGSetting = new FrgSetting();

    @Override
    protected void leaveActivity() {
        if (FrgSetting.PAGE_IDX_MAIN != mFGSetting.getCurrentItem()) {
            mFGSetting.change_page(FrgSetting.PAGE_IDX_MAIN);
        } else {
            int ret_data = GlobalDef.INTRET_GIVEUP;
            Intent data = new Intent();
            setResult(ret_data, data);
            finish();
        }
    }

    @Override
    protected void initFrgHolder() {
        LOG_TAG = "ACSetting";
        mFGHolder = mFGSetting;
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
                if (FrgSetting.PAGE_IDX_MAIN != mFGSetting.getCurrentItem()) {
                    final TFSettingBase tb = mFGSetting.getCurrentPage();
                    if (tb.isSettingDirty()) {
                        Dialog alertDialog = new AlertDialog.Builder(this).
                                setTitle("配置已经更改").
                                setMessage("是否保存更改的配置?").
                                setPositiveButton("是", (dialog, which) -> {
                                    tb.updateSetting();
                                    mFGSetting.change_page(FrgSetting.PAGE_IDX_MAIN);
                                }).
                                setNegativeButton("否", (dialog, which) -> mFGSetting.change_page(FrgSetting.PAGE_IDX_MAIN)).
                                create();
                        alertDialog.show();
                    } else {
                        mFGSetting.change_page(FrgSetting.PAGE_IDX_MAIN);
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
                if (FrgSetting.PAGE_IDX_MAIN != mFGSetting.getCurrentItem()) {
                    mFGSetting.change_page(FrgSetting.PAGE_IDX_MAIN);
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

    /**
     * 切换到新页面
     *
     * @param new_page 新页面postion
     */
    public void change_page(int new_page) {
        mFGSetting.setCurrentItem(new_page);
    }
}
