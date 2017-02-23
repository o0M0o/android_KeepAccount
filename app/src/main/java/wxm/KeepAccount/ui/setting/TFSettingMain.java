package wxm.KeepAccount.ui.setting;


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
import wxm.KeepAccount.utility.ContextUtil;
import wxm.KeepAccount.R;

/**
 * 设置主页面
 * Created by 123 on 2016/10/10.
 */
public class TFSettingMain extends TFSettingBase {

    @BindView(R.id.rl_remind)
    RelativeLayout  mRLRemind;

    @BindView(R.id.rl_share_app)
    RelativeLayout  mRLShareApp;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.page_setting_main, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != view) {
            mRLRemind.setVisibility(View.GONE);
            mRLShareApp.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.rl_check_version, R.id.rl_chart_color, R.id.rl_reformat_data})
    public void onIVClick(View v) {
        switch (v.getId())  {
            case R.id.rl_check_version :    {
                toPageByIdx(FrgSetting.PAGE_IDX_CHECK_VERSION);
            }
            break;

            case R.id.rl_chart_color :    {
                toPageByIdx(FrgSetting.PAGE_IDX_CHART_COLOR);
            }
            break;

            case R.id.rl_reformat_data :    {
                Dialog alertDialog = new AlertDialog.Builder(getContext()).
                        setTitle("清除所有数据!").
                        setMessage("此操作不能恢复，是否继续操作!").
                        setPositiveButton("是", (dialog, which) -> ContextUtil.ClearDB()).
                        setNegativeButton("否", (dialog, which) -> {
                        }).
                        create();
                alertDialog.show();
            }
            break;
        }
    }

    @Override
    public void updateSetting() {
        if(mBSettingDirty)  {
            mBSettingDirty = false;
        }
    }
}