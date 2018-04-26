package wxm.KeepAccount.ui.help

import android.os.Bundle

import wxm.androidutil.Switcher.ACSwitcherActivity


/**
 * UI for help
 */
class ACHelp : ACSwitcherActivity<FrgHelp>() {

    override fun setupFragment(bundle: Bundle?) {
        addFragment(FrgHelp())
    }

    companion object {
        const val STR_HELP_TYPE = "HELP_TYPE"

        const val STR_HELP_MAIN = "help_main"
        const val STR_HELP_START = "help_start"
    }
}
