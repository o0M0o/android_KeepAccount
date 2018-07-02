package wxm.KeepAccount.ui.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import wxm.KeepAccount.ui.base.ACBase.ACBase

/**
 * UI for help
 */
class ACNoteSearch : ACBase<FrgNoteSearch>() {
    companion object {
        @Suppress("MemberVisibilityCanBePrivate")
        const val REQUEST_NOTE_SEARCH = 1

        fun start(ct: Context, frg: Fragment) {
            frg.startActivityForResult(Intent(ct, ACNoteSearch::class.java),
                    REQUEST_NOTE_SEARCH)
        }

        fun start(ac: Activity) {
            ac.startActivityForResult(Intent(ac, ACNoteSearch::class.java),
                    REQUEST_NOTE_SEARCH)
        }
    }
}
