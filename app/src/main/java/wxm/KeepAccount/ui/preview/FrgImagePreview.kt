package wxm.KeepAccount.ui.preview


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import kotterknife.bindView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.db.LoginHistoryUtility
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.event.DoLogin
import wxm.KeepAccount.ui.base.ZoomImageView.ZoomImageView
import wxm.KeepAccount.ui.usr.ACAddUsr
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.ui.welcome.ACWelcome
import wxm.KeepAccount.utility.AppUtil
import wxm.KeepAccount.utility.ToolUtil
import wxm.KeepAccount.utility.let1
import wxm.KeepAccount.utility.setImagePath
import wxm.androidutil.log.TagLog
import wxm.androidutil.time.toTimestamp
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * for login
 * Created by WangXM on 2016/11/29.
 */
class FrgImagePreview : FrgSupportBaseAdv() {
    private val mZIVImage: ZoomImageView  by bindView(R.id.ziv_scale)

    override fun getLayoutID(): Int = R.layout.frg_image_perview

    override fun initUI(bundle: Bundle?) {
        activity!!.intent!!.let1 {
            it.getStringExtra(ACImagePreview.IMAGE_FILE_PATH)?.let1 {
                mZIVImage.setImagePath(it)
            }
        }
    }

    /// PRIVATE BEGIN
    /// PRIVATE END
}
