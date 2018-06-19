package wxm.KeepAccount.ui.help

import android.os.Bundle
import wxm.KeepAccount.ui.base.ACBase.ACBase

/**
 * UI for help
 */
class ACHelp : ACBase<FrgHelp>() {

    override fun setupFragment(bundle: Bundle?) {
        addFragment(FrgHelp())
    }

    companion object {
        const val STR_HELP_TYPE = "HELP_TYPE"

        const val STR_HELP_MAIN = "help_main"
        const val STR_HELP_START = "help_start"
    }
}
