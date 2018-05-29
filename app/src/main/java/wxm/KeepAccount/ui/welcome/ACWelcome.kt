package wxm.KeepAccount.ui.welcome


import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import wxm.KeepAccount.define.GlobalDef
import wxm.androidutil.ui.activity.ACSwitcherActivity
import com.theartofdev.edmodo.cropper.CropImage
import android.R.attr.data
import android.app.Activity
import org.greenrobot.eventbus.EventBus
import wxm.KeepAccount.R
import wxm.KeepAccount.event.UsrImage
import wxm.androidutil.ui.dialog.DlgAlert


/**
 * first page after login
 */
class ACWelcome : ACSwitcherActivity<FrgWelcome>()  {
    override fun setupFragment(savedInstanceState: Bundle?) {
        addFragment(FrgWelcome())
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig)
        hotFragment.reInitUI()
    }

    override fun leaveActivity() {
        if ((hotFragment as FrgWelcome).leaveFrg()) {
            setResult(GlobalDef.INTRET_USR_LOGOUT, Intent())
            finish()
        }
    }

    /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                EventBus.getDefault().post(UsrImage(ImageUri = result.uri))
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                DlgAlert.showAlert(this, R.string.dlg_erro, result.error.toString())
            }
        }
    }
    */
}
