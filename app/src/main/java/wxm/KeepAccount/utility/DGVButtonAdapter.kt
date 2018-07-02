package wxm.KeepAccount.utility

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import wxm.KeepAccount.R
import wxm.KeepAccount.define.EAction
import wxm.androidutil.ui.moreAdapter.MoreAdapter
import wxm.androidutil.ui.view.ViewHolder
import java.util.*

/**
 * drag grid view adapter for buttons in welcome activity
 * Created by WangXM on 2016/9/19.
 */
class DGVButtonAdapter(private val mCTContext: Context, data: List<Map<String, *>>)
    : MoreAdapter(mCTContext, data, R.layout.gi_button, arrayOf(""), IntArray(0)) {
    override fun loadView(pos: Int, vhHolder: ViewHolder) {
        val hv = (getItem(pos) as HashMap<*, *>)[KEY_ACT_NAME] as String

        vhHolder.getView<TextView>(R.id.tv_name)!!.text = hv
        if (mCTContext is View.OnClickListener) {
            vhHolder.convertView.setOnClickListener(mCTContext as View.OnClickListener)
        }

        // for image
        EAction.getIcon(hv)!!.let {
            vhHolder.getView<ImageView>(R.id.iv_image)!!.apply {
                setImageBitmap(it)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }
    }


    companion object {
        const val KEY_ACT_NAME = "KEY_ACT_NAME"
    }
}
