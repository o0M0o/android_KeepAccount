package wxm.KeepAccount.ui.data.edit.RecordInfo

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.RelativeLayout
import android.widget.SimpleAdapter
import android.widget.TextView
import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.define.RecordTypeItem
import wxm.KeepAccount.ui.dialog.DlgRecordInfo
import wxm.KeepAccount.utility.ContextUtil
import wxm.androidutil.app.AppBase
import wxm.androidutil.ui.dialog.DlgOKOrNOBase
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.util.UtilFun
import java.util.*

/**
 * @author      WangXM
 * @version     create：2018/4/25
 */
class FrgRecordInfoEdit : FrgSupportBaseAdv(), View.OnClickListener {
    // data for view
     var mEditType: String? = null
        set(value) {
            field = UtilFun.cast_t<String>(value)
        }

    private var mCurType: String? = null
    private var mLHMData: ArrayList<HashMap<String, String>> = ArrayList()
    private var mGVAdapter: GVTypeAdapter? = null

    val curData: Any?
        get() {
            var ri: RecordTypeItem? = null
            if (!UtilFun.StringIsNullOrEmpty(mCurType)) {
                for (hm in mLHMData) {
                    if (hm[KEY_NAME] == mCurType) {
                        ri = ContextUtil.recordTypeUtility.getData(Integer.valueOf(hm[KEY_ID]))
                        break
                    }
                }
            }

            return ri
        }

    // ui component for view
    private val mTVNote: TextView by bindView(R.id.tv_note)
    private val mRLActAdd: RelativeLayout by bindView(R.id.rl_add)
    private val mRLActMinus: RelativeLayout by bindView(R.id.rl_minus)
    private val mRLActPencil: RelativeLayout by bindView(R.id.rl_pencil)
    private val mRLActAccept: RelativeLayout by bindView(R.id.rl_accept)
    private val mRLActReject: RelativeLayout by bindView(R.id.rl_reject)
    private val mGVHolder: GridView by bindView(R.id.gv_record_info)


    override fun isUseEventBus(): Boolean {
        return false
    }

    override fun getLayoutID(): Int {
        return R.layout.vw_edit_record_info
    }

    override fun initUI(savedInstanceState: Bundle?) {
        if(null == savedInstanceState) {
            mRLActAdd.setOnClickListener(this)
            mRLActMinus.setOnClickListener(this)
            mRLActAccept.setOnClickListener(this)
            mRLActReject.setOnClickListener(this)
            mRLActPencil.setOnClickListener(this)
            updateAct(SELECTED_NONE)
        }

        loadUI(savedInstanceState)
    }

    override fun loadUI(bundle: Bundle?) {
        if (mEditType != GlobalDef.STR_RECORD_PAY
                && mEditType != GlobalDef.STR_RECORD_INCOME)
            return

        // init gv
        mLHMData.clear()
        mGVAdapter = GVTypeAdapter(activity, mLHMData,
                arrayOf(KEY_NAME), intArrayOf(R.id.tv_type_name))
        mGVHolder.adapter = mGVAdapter
        mGVHolder.setOnItemClickListener { _, _, position, _ -> onGVItemClick(position) }
        loadInfo()
    }

    override fun onClick(v: View) {
        val vid = v.id
        when (vid) {
            R.id.rl_pencil, R.id.rl_add -> {
                val dp = DlgRecordInfo()
                dp.setInitDate(if (R.id.rl_pencil == vid) curData as RecordTypeItem else null)
                dp.setRecordType(mEditType!!)
                dp.addDialogListener(object : DlgOKOrNOBase.DialogResultListener {
                    override fun onDialogPositiveResult(dialog: DialogFragment) {
                        val curDp = UtilFun.cast_t<DlgRecordInfo>(dialog)
                        val ri = curDp.curDate
                        if (null != ri) {
                            if (R.id.rl_add == vid)
                                ContextUtil.recordTypeUtility.createData(ri)
                            else
                                ContextUtil.recordTypeUtility.modifyData(ri)
                            loadInfo()
                        }
                    }

                    override fun onDialogNegativeResult(dialog: DialogFragment) {}
                })

                dp.show(fragmentManager, "添加记录信息")
            }

            R.id.rl_minus -> {
                updateAct(SELECTED_MINUS)
                mCurType = ""
                for (hm in mLHMData) {
                    hm[KEY_SELECTED] = VAL_NOT_SELECTED
                }

                mRLActAccept.visibility = View.INVISIBLE
                mTVNote.text = if (mRLActMinus.isSelected) "请选择待删除项" else ""
                mGVAdapter!!.notifyDataSetChanged()
            }

            R.id.rl_accept -> {
                //for gv
                val lsId = LinkedList<Int>()
                for (hm in mLHMData) {
                    if (hm[KEY_SELECTED] == VAL_SELECTED) {
                        lsId.add(Integer.valueOf(hm[KEY_ID]))
                    }
                }

                if (0 < lsId.size) {
                    val dlg = AlertDialog.Builder(activity)
                            .setTitle("警告")
                            .setMessage("请确认是否删除数据!")
                            .setPositiveButton("确认",
                                    { _, _ ->
                                        for (id in lsId) {
                                            ContextUtil.recordTypeUtility.removeData(id)
                                        }

                                        mTVNote.text = ""
                                        loadInfo()
                                        updateAct(SELECTED_ACCEPT)
                                    })
                            .setNegativeButton("取消", { _, _ -> })
                            .create()

                    dlg.show()
                } else {
                    mTVNote.text = ""
                    loadInfo()
                    updateAct(SELECTED_ACCEPT)
                }
            }

            R.id.rl_reject -> {
                // for ui
                updateAct(SELECTED_REJECT)

                // for gv
                for (hm in mLHMData) {
                    hm[KEY_SELECTED] = VAL_NOT_SELECTED
                }
                mTVNote.text = ""
                mGVAdapter!!.notifyDataSetChanged()
            }
        }
    }

    /**
     * load data to gird view
     */
    private fun loadInfo() {
        mLHMData.clear()
        val alType = if (mEditType == GlobalDef.STR_RECORD_PAY) {
            ArrayList(ContextUtil.recordTypeUtility.allPayItem)
        } else {
            ArrayList(ContextUtil.recordTypeUtility.allIncomeItem)
        }

        alType.sortWith(Comparator { o1, o2 -> o1.type.compareTo(o2.type) })

        for (ri in alType) {
            val hmd = HashMap<String, String>()
            hmd[KEY_NAME] = ri.type
            ri.note?.let { hmd[KEY_NOTE] = it }
            hmd[KEY_SELECTED] = VAL_NOT_SELECTED
            hmd[KEY_ID] = ri._id.toString()

            mLHMData.add(hmd)
        }

        mGVAdapter!!.notifyDataSetChanged()
    }

    /**
     * update actions
     * @param type  for type
     */
    private fun updateAct(type: Int) {
        when (type) {
            SELECTED_NONE -> {
                mRLActPencil.visibility = View.INVISIBLE
                mRLActAdd.visibility = View.VISIBLE
                mRLActMinus.visibility = View.VISIBLE
                mRLActAccept.visibility = View.INVISIBLE
                mRLActReject.visibility = View.INVISIBLE
            }

            SELECTED_MINUS -> {
                mRLActPencil.visibility = View.INVISIBLE
                if (mRLActMinus.isSelected) {
                    mRLActAdd.visibility = View.VISIBLE
                    mRLActAccept.visibility = View.INVISIBLE
                    mRLActReject.visibility = View.INVISIBLE

                    mRLActMinus.setBackgroundColor(mCLNotSelected)
                    mRLActMinus.isSelected = false
                } else {
                    mRLActAdd.visibility = View.INVISIBLE
                    mRLActAccept.visibility = View.VISIBLE
                    mRLActReject.visibility = View.VISIBLE

                    mRLActMinus.setBackgroundColor(mCLSelected)
                    mRLActMinus.isSelected = true
                }
            }

            SELECTED_ACCEPT -> {
                mRLActPencil.visibility = View.INVISIBLE
                mRLActAdd.visibility = View.VISIBLE
                mRLActAccept.visibility = View.INVISIBLE
                mRLActReject.visibility = View.INVISIBLE
                mRLActMinus.setBackgroundColor(mCLNotSelected)
            }

            SELECTED_REJECT -> {
                mRLActPencil.visibility = View.INVISIBLE
                mRLActAdd.visibility = View.VISIBLE
                mRLActAccept.visibility = View.INVISIBLE
                mRLActReject.visibility = View.INVISIBLE
                mRLActMinus.setBackgroundColor(mCLNotSelected)
            }
        }
    }

    /**
     * after gridview item selected
     * @param position      current click position
     */
    private fun onGVItemClick(position: Int) {
        if (mRLActMinus.isSelected) {
            val hm = mLHMData[position]
            val oldSel = hm[KEY_SELECTED]
            hm[KEY_SELECTED] = if (oldSel == VAL_SELECTED)
                VAL_NOT_SELECTED
            else
                VAL_SELECTED

            val iSel = mLHMData.count { it[KEY_SELECTED] == VAL_SELECTED }
            mRLActAccept.visibility = if (0 == iSel) View.INVISIBLE else View.VISIBLE
            mTVNote.text = String.format(Locale.CHINA, "已选择%d项待删除", iSel)
            mGVAdapter!!.notifyDataSetChanged()
        } else {
            val tvStr = mLHMData[position][KEY_NAME]
            for (hm in mLHMData) {
                if (hm[KEY_NAME] == tvStr) {
                    if (hm[KEY_SELECTED] == VAL_NOT_SELECTED) {
                        hm[KEY_SELECTED] = VAL_SELECTED
                        mTVNote.text = if (UtilFun.StringIsNullOrEmpty(hm[KEY_NOTE]))
                            ""
                        else
                            hm[KEY_NOTE]

                        mRLActPencil.visibility = View.VISIBLE
                        mCurType = tvStr
                    } else {
                        hm[KEY_SELECTED] = VAL_NOT_SELECTED
                        mTVNote.text = ""

                        mRLActPencil.visibility = View.INVISIBLE
                        mCurType = ""
                    }
                } else {
                    hm[KEY_SELECTED] = VAL_NOT_SELECTED
                }
            }

            mGVAdapter!!.notifyDataSetChanged()
        }
    }

    /**
     * adapter for gridview
     */
    inner class GVTypeAdapter
            internal constructor(context: Context, data: List<Map<String, *>>, from: Array<String>, to: IntArray)
            : SimpleAdapter(context, data, R.layout.gi_record_type, from, to) {

        override fun getViewTypeCount(): Int {
            val orgCount = count
            return if (orgCount < 1) 1 else orgCount
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

        override fun getView(position: Int, view: View?, arg2: ViewGroup?): View? {
            val v = super.getView(position, view, arg2)
            if (null != v) {
                val hm = mLHMData[position]
                val curCL = if (hm[KEY_SELECTED] == VAL_SELECTED)
                    mCLSelected
                else
                    mCLNotSelected
                v.setBackgroundColor(curCL)
            }

            return v
        }
    }

    companion object {
        private const val SELECTED_NONE = 1
        private const val SELECTED_MINUS = 2
        private const val SELECTED_ACCEPT = 3
        private const val SELECTED_REJECT = 4
        private const val KEY_NAME = "key_name"
        private const val KEY_NOTE = "key_note"
        private const val KEY_SELECTED = "key_selected"
        private const val KEY_ID = "key_id"
        private const val VAL_SELECTED = "val_selected"
        private const val VAL_NOT_SELECTED = "val_not_selected"

        private val mCLSelected: Int = AppBase.getColor(R.color.peachpuff)
        private val mCLNotSelected: Int = AppBase.getColor(R.color.white)
    }
}