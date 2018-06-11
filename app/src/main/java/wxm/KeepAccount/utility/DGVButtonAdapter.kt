package wxm.KeepAccount.utility

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SimpleAdapter
import android.widget.TextView
import wxm.KeepAccount.R
import wxm.KeepAccount.define.EAction
import java.util.*

/**
 * drag grid view adapter for buttons in welcome activity
 * Created by WangXM on 2016/9/19.
 */
class DGVButtonAdapter(private val mCTContext: Context, data: List<Map<String, *>>)
    : SimpleAdapter(mCTContext, data, R.layout.gi_button,
        arrayOf(""), IntArray(0)) {
    /**
     * get current action
     * @return      action
     */
    val curAction: List<String>
        get() {
            return ArrayList<String>().apply {
                val ic = count
                @Suppress("UNCHECKED_CAST")
                for (i in 0 until ic) {
                    add(((getItem(i) as HashMap<String, Any>)[KEY_ACT_NAME] as String))
                }
            }
        }

    override fun getViewTypeCount(): Int {
        return count.let {
            if (it < 1) 1 else it
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getView(position: Int, view: View?, arg2: ViewGroup?): View? {
        val v = super.getView(position, view, arg2)
        if (null != v) {
            val hv = (getItem(position) as HashMap<*, *>)[KEY_ACT_NAME] as String

            v.findViewById<TextView>(R.id.tv_name).text = hv
            if (mCTContext is View.OnClickListener) {
                v.setOnClickListener(mCTContext as View.OnClickListener)
            }

            // for image
            EAction.getIcon(hv)!!.let {
                v.findViewById<ImageView>(R.id.iv_image)!!.apply {
                    setImageBitmap(it)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
            }
        }

        return v
    }

    companion object {
        const val KEY_ACT_NAME = "KEY_ACT_NAME"
    }
}
