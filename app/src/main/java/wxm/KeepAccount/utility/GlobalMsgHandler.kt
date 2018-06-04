package wxm.KeepAccount.utility

import android.content.Intent
import android.os.Handler
import android.os.Message
import org.greenrobot.eventbus.EventBus
import wxm.KeepAccount.db.LoginHistoryUtility

import wxm.KeepAccount.define.EMsgType
import wxm.androidutil.util.UtilFun
import wxm.KeepAccount.item.UsrItem
import wxm.KeepAccount.ui.login.DoLoginEvent
import wxm.androidutil.log.TagLog


/**
 * global msg handler
 * Created by WangXM on 2016/6/30.
 */
class GlobalMsgHandler : Handler() {
    override fun handleMessage(msg: Message) {
        TagLog.i("receive msg : " + msg.toString())
        val et = EMsgType.getEMsgType(msg.what) ?: return

        when (et) {
            EMsgType.USR_ADD -> {
                val arr = UtilFun.cast<Array<Any>>(msg.obj)

                val data = UtilFun.cast<Intent>(arr[0])
                val h = UtilFun.cast<Handler>(arr[1])
                val usr = data.getStringExtra(UsrItem.FIELD_NAME)
                val pwd = data.getStringExtra(UsrItem.FIELD_PWD)

                if (AppUtil.usrUtility.hasUsr(usr)) {
                    replyMsg(h, EMsgType.USR_ADD.id,
                            arrayOf(false, data, "用户已经存在！"))
                } else {
                    val ret = null != AppUtil.usrUtility.addUsr(usr, pwd)
                    replyMsg(h, EMsgType.USR_ADD.id,
                            arrayOf(ret, data))
                }
            }

            EMsgType.USR_LOGIN -> {
                val arr = UtilFun.cast<Array<Any>>(msg.obj)

                val data = UtilFun.cast<Intent>(arr[0])
                val h = UtilFun.cast<Handler>(arr[1])

                val usr = data.getStringExtra(UsrItem.FIELD_NAME)
                val pwd = data.getStringExtra(UsrItem.FIELD_PWD)

                AppUtil.curUsr = AppUtil.usrUtility.checkGetUsr(usr, pwd)
                val ret = null != AppUtil.curUsr

                replyMsg(h, EMsgType.USR_LOGIN.id, ret)
            }

            EMsgType.USR_LOGOUT -> {
                AppUtil.curUsr = null
                LoginHistoryUtility.cleanHistory()
                EventBus.getDefault().post(DoLoginEvent(this.javaClass.simpleName))
            }

            else -> {
                TagLog.e("can not handle msg : " + msg.toString())
            }
        }
    }

    companion object {
        /**
         * reply msg
         * @param mh        receive handler
         * @param msg_type  origin msg type`arg1`）
         * @param msg_obj   object for reply`obj`
         */
        private fun replyMsg(mh: Handler?, msg_type: Int, msg_obj: Any) {
            val m = Message.obtain(mh, EMsgType.REPLAY.id)
            m.arg1 = msg_type
            m.obj = msg_obj
            m.sendToTarget()
        }
    }
}

