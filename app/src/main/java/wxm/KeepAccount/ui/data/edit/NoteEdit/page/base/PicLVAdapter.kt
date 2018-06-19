package wxm.KeepAccount.ui.data.edit.NoteEdit.page.base

import android.content.Context
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import org.greenrobot.eventbus.EventBus
import wxm.KeepAccount.R
import wxm.KeepAccount.event.PicPath
import wxm.androidutil.improve.let1
import wxm.androidutil.improve.setImagePath
import wxm.androidutil.ui.dialog.DlgAlert
import wxm.androidutil.ui.moreAdapter.MoreAdapter
import wxm.androidutil.ui.view.ViewHolder

/**
 * @author      WangXM
 * @version     create：2018/6/19
 */
class PicLVAdapter internal constructor(context: Context,
                                        data: List<Map<String, String>>,
                                        canModify: Boolean)
    : MoreAdapter(context, data, R.layout.li_pic) {
    private val mCanModify = canModify

    override fun loadView(pos: Int, vhHolder: ViewHolder) {
        @Suppress("UNCHECKED_CAST")
        val path = (getItem(pos) as Map<String, String>)[PIC_PATH]!!
        vhHolder.getView<ImageView>(R.id.iv_image)!!.let1 {
            it.setImagePath(path)
            it.setOnClickListener {_ ->
                EventBus.getDefault().post(PicPath(PicPath.PREVIEW_PIC, path))
            }
        }

        if (mCanModify) {
            vhHolder.getView<ImageButton>(R.id.ib_image_remove).setOnClickListener { _ ->
                DlgAlert.showAlert(mContext, R.string.dlg_info, R.string.dlg_data_deleted
                ) { b ->
                    b.setPositiveButton(R.string.cn_sure) { _, _ ->
                        EventBus.getDefault().post(PicPath(PicPath.REMOVE_PIC, path))
                    }
                }
            }

            vhHolder.getView<ImageButton>(R.id.ib_image_refresh).setOnClickListener { _ ->
                EventBus.getDefault().post(PicPath(PicPath.REFRESH_PIC, path))
            }
        } else {
            vhHolder.getView<LinearLayout>(R.id.ll_ib).visibility = View.GONE
        }
    }

    companion object {
        const val PIC_PATH = "pic_path"
    }
}