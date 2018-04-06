package wxm.KeepAccount.ui.help;

import android.os.Bundle;

import wxm.androidutil.Switcher.ACSwitcherActivity;


/**
 * UI for help
 */
public class ACHelp extends ACSwitcherActivity<FrgHelp> {
    public static final String STR_HELP_TYPE = "HELP_TYPE";

    public static final String STR_HELP_MAIN = "help_main";
    public static final String STR_HELP_START = "help_start";

    @Override
    protected void setupFragment(Bundle bundle) {
        addFragment(new FrgHelp());
    }
}
