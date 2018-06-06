package wxm.KeepAccount.ui.preview

import android.os.Bundle
import wxm.androidutil.ui.activity.ACSwitcherActivity

/**
 * A login screen that offers login via email/password.
 */
class ACImagePreview : ACSwitcherActivity<FrgImagePreview>() {
    override fun setupFragment(savedInstanceState: Bundle?) {
        addFragment(FrgImagePreview())
    }

    companion object {
        const val IMAGE_FILE_PATH = "image_file_path"
    }
}


