package wxm.KeepAccount.ui.help;

import wxm.androidutil.ExActivity.BaseAppCompatActivity;

/**
 * 本activity用于展示应用帮助信息
 * 为优化代码架构，activity启动时根据intent参数加载不同帮助信息
 */
public class ACHelp extends BaseAppCompatActivity {
    public static final String STR_HELP_TYPE = "HELP_TYPE";

    public static final String STR_HELP_MAIN = "help_main";
    public static final String STR_HELP_START = "help_start";
    public static final String STR_HELP_DAILYDETAIL = "help_dailydetail";
    public static final String STR_HELP_RECORD = "help_record";
    public static final String STR_HELP_BUDGET = "budget";

    @Override
    protected void leaveActivity() {
        finish();
    }

    @Override
    protected void initFrgHolder() {
        LOG_TAG = "ACHelp";
        mFGHolder = new FrgHelp();
    }
}
