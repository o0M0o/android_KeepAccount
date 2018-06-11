package wxm.KeepAccount.ui.preview


import android.os.Bundle
import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.androidutil.improve.let1
import wxm.KeepAccount.improve.setImagePath
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.uilib.zoomImage.zoomImageView

/**
 * for login
 * Created by WangXM on 2016/11/29.
 */
class FrgImagePreview : FrgSupportBaseAdv() {
    private val mZIVImage: zoomImageView  by bindView(R.id.ziv_scale)

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
