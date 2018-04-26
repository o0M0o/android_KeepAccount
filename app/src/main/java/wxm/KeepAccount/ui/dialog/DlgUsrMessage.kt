package wxm.KeepAccount.ui.dialog

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Message
import android.support.design.widget.TextInputEditText
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View

import org.json.JSONException
import org.json.JSONObject

import java.io.IOException

import wxm.KeepAccount.utility.ToolUtil
import wxm.androidutil.Dialog.DlgOKOrNOBase
import wxm.androidutil.util.PackageUtil
import wxm.androidutil.util.SIMCardUtil
import wxm.androidutil.util.UtilFun
import wxm.androidutil.util.WRMsgHandler
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.utility.ContextUtil

import android.Manifest.permission.READ_PHONE_STATE
import android.Manifest.permission.READ_SMS
import android.os.Bundle

/**
 * submit usr msg
 * Created by WangXM on 2017/1/9.
 */
class DlgUsrMessage : DlgOKOrNOBase() {
    private val mSZUrlPost: String = ContextUtil.getString(R.string.url_post_send_message)
    private val mSZColUsr: String = ContextUtil.getString(R.string.col_usr)
    private val mSZColMsg: String = ContextUtil.getString(R.string.col_message)
    private val mSZColAppName: String = ContextUtil.getString(R.string.col_app_name)
    private val mSZColValAppName: String = ContextUtil.getString(R.string.col_val_app_name)

    private val mSZUsrMessage: String = ContextUtil.getString(R.string.cn_usr_message)
    private val mSZAccept: String = ContextUtil.getString(R.string.cn_accept)
    private val mSZGiveUp: String = ContextUtil.getString(R.string.cn_giveup)

    private lateinit var mETUsrMessage: TextInputEditText

    private var mProgressStatus = 0
    private lateinit var mHDProgress: LocalMsgHandler
    private lateinit var mPDDlg: ProgressDialog

    override fun createDlgView(savedInstanceState: Bundle?): View {
        initDlgTitle(mSZUsrMessage, mSZAccept, mSZGiveUp)
        return View.inflate(activity, R.layout.dlg_send_message, null)
    }

    override fun initDlgView(savedInstanceState: Bundle?) {
        mETUsrMessage = findDlgChildView(R.id.et_usr_message)!!

        mHDProgress = LocalMsgHandler(this)
        mPDDlg = ProgressDialog(context)
    }

    override fun checkBeforeOK(): Boolean {
        val msg = mETUsrMessage.text.toString()
        if (UtilFun.StringIsNullOrEmpty(msg)) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("消息不能为空")
                    .setTitle("警告")
            val dlg = builder.create()
            dlg.show()
            return false
        }

        var usr: String? = null
        val ct = ContextUtil.instance
        if (null != ct) {
            if (ContextCompat.checkSelfPermission(ct, READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(ct, READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                val si = SIMCardUtil(context)
                usr = si.nativePhoneNumber
            }
        }

        return sendMsgByHttpPost(if (UtilFun.StringIsNullOrEmpty(usr)) "null" else usr!!, msg)
    }

    /**
     * use http send msg
     * @param usr       msg owner
     * @param msg       msg
     * @return          true if success
     */
    private fun sendMsgByHttpPost(usr: String, msg: String): Boolean {
        mProgressStatus = 0

        mPDDlg.max = 100
        // 设置对话框的标题
        mPDDlg.setTitle("发送消息")
        // 设置对话框 显示的内容
        mPDDlg.setMessage("发送进度")
        // 设置对话框不能用“取消”按钮关闭
        mPDDlg.setCancelable(true)
        mPDDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "取消") { _, _ -> }

        // 设置对话框的进度条风格
        mPDDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        mPDDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        // 设置对话框的进度条是否显示进度
        mPDDlg.isIndeterminate = false

        mPDDlg.incrementProgressBy(-mPDDlg.progress)
        mPDDlg.show()

        ToolUtil.runInBackground(this.activity,
                Runnable {
                    val client = OkHttpClient()
                    try {
                        // set param
                        val param = JSONObject()
                        param.put(mSZColUsr, usr)
                        param.put(mSZColMsg, msg)
                        param.put(mSZColAppName,
                                mSZColValAppName + "-"
                                        + PackageUtil.getVerName(context, GlobalDef.PACKAGE_NAME))

                        val body = RequestBody.create(JSON, param.toString())

                        mProgressStatus = 50
                        val m = Message()
                        m.what = MSG_PROGRESS_UPDATE
                        mHDProgress.sendMessage(m)

                        val request = Request.Builder()
                                .url(mSZUrlPost).post(body).build()
                        client.newCall(request).execute()

                        mProgressStatus = 100
                        val m1 = Message()
                        m1.what = MSG_PROGRESS_UPDATE
                        mHDProgress.sendMessage(m1)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                },
                Runnable {  mPDDlg.dismiss() })

        return true
    }

    /**
     * safe message hanlder
     */
    private class LocalMsgHandler internal constructor(ac: DlgUsrMessage) : WRMsgHandler<DlgUsrMessage>(ac) {
        init {
            TAG = "LocalMsgHandler"
        }

        override fun processMsg(m: Message, home: DlgUsrMessage) {
            when (m.what) {
                MSG_PROGRESS_UPDATE -> {
                    home.mPDDlg.progress = home.mProgressStatus
                }

                else -> Log.e(TAG, String.format("msg(%s) can not process", m.toString()))
            }
        }
    }

    companion object {
        // for progress dialog when send http post
        private const val PROGRESS_DIALOG = 0x112
        private const val MSG_PROGRESS_UPDATE = 0x111
        // for http post
        private val JSON = MediaType.parse("application/json; charset=utf-8")
    }
}
