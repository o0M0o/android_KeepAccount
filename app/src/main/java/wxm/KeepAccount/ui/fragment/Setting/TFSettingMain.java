package wxm.KeepAccount.ui.fragment.Setting;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.utility.ContextUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.fragment.utility.FrgSetting;

/**
 * 设置主页面
 * Created by 123 on 2016/10/10.
 */
public class TFSettingMain extends TFSettingBase {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.page_setting_main, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != view) {
            RelativeLayout rl = UtilFun.cast(view.findViewById(R.id.rl_check_version));
            assert null != rl;
            rl.setOnClickListener(v -> {
                //Toast.makeText(getContext(), "check version", Toast.LENGTH_SHORT).show();
                toPageByIdx(FrgSetting.PAGE_IDX_CHECK_VERSION);
            });

            rl = UtilFun.cast(view.findViewById(R.id.rl_chart_color));
            assert null != rl;
            rl.setOnClickListener(v -> {
                //Toast.makeText(getContext(), "chart color", Toast.LENGTH_SHORT).show();
                toPageByIdx(FrgSetting.PAGE_IDX_CHART_COLOR);
            });

            rl = UtilFun.cast(view.findViewById(R.id.rl_reformat_data));
            assert null != rl;
            rl.setOnClickListener(v -> {
                Dialog alertDialog = new AlertDialog.Builder(getContext()).
                        setTitle("清除所有数据!").
                        setMessage("此操作不能恢复，是否继续操作!").
                        setPositiveButton("是", (dialog, which) -> ContextUtil.ClearDB()).
                        setNegativeButton("否", (dialog, which) -> {
                        }).
                        create();
                alertDialog.show();
            });

            rl = UtilFun.cast(view.findViewById(R.id.rl_remind));
            assert null != rl;
            setLayoutVisible(rl, View.INVISIBLE);

            rl = UtilFun.cast(view.findViewById(R.id.rl_share_app));
            assert null != rl;
            setLayoutVisible(rl, View.INVISIBLE);
        }
    }

    @Override
    public void updateSetting() {
        if(mBSettingDirty)  {
            mBSettingDirty = false;
        }
    }


    /**
     * 设置layout可见性
     * 仅调整可见性，其它设置保持不变
     * @param visible  若为 :
     *                  1. {@code View.INVISIBLE}, 不可见
     *                  2. {@code View.VISIBLE}, 可见
     */
    private void setLayoutVisible(RelativeLayout rl, int visible)    {
        int w = RelativeLayout.LayoutParams.MATCH_PARENT;
        int h = 0;
        if(View.INVISIBLE != visible)
            h = RelativeLayout.LayoutParams.WRAP_CONTENT;

        ViewGroup.LayoutParams param = rl.getLayoutParams();
        param.width = w;
        param.height = h;
        rl.setLayoutParams(param);
    }
}
