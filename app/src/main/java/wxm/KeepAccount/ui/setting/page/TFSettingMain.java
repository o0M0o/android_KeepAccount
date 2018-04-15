package wxm.KeepAccount.ui.setting.page;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.setting.ACSetting;
import wxm.KeepAccount.utility.ContextUtil;
import wxm.KeepAccount.utility.ToolUtil;

/**
 * main page for setting
 * Created by WangXM on 2016/10/10.
 */
public class TFSettingMain extends TFSettingBase {
    @BindView(R.id.rl_remind)
    RelativeLayout mRLRemind;

    @BindView(R.id.rl_share_app)
    RelativeLayout mRLShareApp;

    @Override
    protected int getLayoutID() {
        return R.layout.page_setting_main;
    }

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void initUI(Bundle bundle)    {
        if(null == bundle) {
            mRLRemind.setVisibility(View.GONE);
            mRLShareApp.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.rl_check_version, R.id.rl_chart_color, R.id.rl_reformat_data})
    public void onIVClick(View v) {
        switch (v.getId()) {
            case R.id.rl_check_version: {
                ((ACSetting)getActivity())
                        .switchToPageByType(TFSettingCheckVersion.class.getName());
            }
            break;

            case R.id.rl_chart_color: {
                ((ACSetting)getActivity())
                        .switchToPageByType(TFSettingChartColor.class.getName());
            }
            break;

            case R.id.rl_reformat_data: {
                Dialog alertDialog = new AlertDialog.Builder(getContext()).
                        setTitle("清除所有数据!").
                        setMessage("此操作不能恢复，是否继续操作!").
                        setPositiveButton("是", (dialog, which) -> {
                            AlertDialog mADDlg = new AlertDialog.Builder(this.getActivity())
                                    .setTitle("提示")
                                    .setMessage("请等待数据清理完毕...").create();;
                            ToolUtil.runInBackground(this.getActivity(),
                                    ContextUtil::ClearDB,
                                    mADDlg::dismiss);
                        }).
                        setNegativeButton("否", (dialog, which) -> {}).
                        create();
                alertDialog.show();
            }
            break;
        }
    }

    @Override
    public void updateSetting() {
        if (mBSettingDirty) {
            mBSettingDirty = false;
        }
    }
}
