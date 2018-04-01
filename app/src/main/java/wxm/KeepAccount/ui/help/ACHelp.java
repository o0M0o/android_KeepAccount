package wxm.KeepAccount.ui.help;

import android.os.Bundle;

import wxm.KeepAccount.ui.base.SwitcherActivity.ACSwitcherActivity;

/**
 * UI for help
 */
public class ACHelp extends ACSwitcherActivity<FrgHelp> {
    public static final String STR_HELP_TYPE = "HELP_TYPE";

    public static final String STR_HELP_MAIN = "help_main";
    public static final String STR_HELP_START = "help_start";

    @Override
    protected void initUi(Bundle savedInstanceState)    {
        super.initUi(savedInstanceState);
        LOG_TAG = "ACHelp";
        addFragment(new FrgHelp());
    }
}
