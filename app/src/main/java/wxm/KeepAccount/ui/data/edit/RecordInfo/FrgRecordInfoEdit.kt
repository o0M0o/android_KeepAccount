package wxm.KeepAccount.ui.data.edit.RecordInfo

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.widget.GridView
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.KeepAccount.db.RecordTypeDBUtility
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.item.RecordTypeItem
import wxm.KeepAccount.ui.dialog.DlgRecordInfo
import wxm.androidutil.app.AppBase
import wxm.androidutil.improve.let1
import wxm.androidutil.ui.dialog.DlgAlert
import wxm.androidutil.ui.dialog.DlgOKOrNOBase
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.ui.moreAdapter.MoreAdapter
import wxm.androidutil.ui.view.ViewHolder
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

    val curData: Any?
        get() {
            var ri: RecordTypeItem? = null
            if (!UtilFun.StringIsNullOrEmpty(mCurType)) {
                mLHMData.find { it[KEY_NAME] == mCurType }?.let1 {
                    ri = RecordTypeDBUtility.instance.getData(Integer.valueOf(it[KEY_ID]))
                }
            }

            return ri
        }

    // ui component for view
    private val mRLActAdd: ImageView by bindView(R.id.iv_add)
    private val mRLActMinus: ImageView by bindView(R.id.iv_minus)
    private val mRLActPencil: ImageView by bindView(R.id.iv_edit)
    private val mRLActAccept: ImageView by bindView(R.id.iv_accept)
    private val mRLActReject: ImageView by bindView(R.id.iv_reject)
    private val mGVHolder: GridView by bindView(R.id.gv_record_info)

    override fun getLayoutID(): Int = R.layout.vw_edit_record_info

    override fun initUI(savedInstanceState: Bundle?) {
        if (null == savedInstanceState) {
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
        mGVHolder.setOnItemClickListener { _, _, position, _ -> onGVItemClick(position) }
        loadInfo()
    }

    override fun onClick(v: View) {
        val vid = v.id
        when (vid) {
            R.id.iv_edit, R.id.iv_add-> {
                DlgRecordInfo().let1 { dp ->
                    dp.setInitDate(if (R.id.iv_edit == vid) curData as RecordTypeItem else null)
                    dp.setRecordType(mEditType!!)
                    dp.addDialogListener(object : DlgOKOrNOBase.DialogResultListener {
                        override fun onDialogPositiveResult(dialog: DialogFragment) {
                            (dialog as DlgRecordInfo).curDate?.let1 {
                                if (R.id.iv_add == vid)
                                    RecordTypeDBUtility.instance.createData(it)
                                else
                                    RecordTypeDBUtility.instance.modifyData(it)
                                loadInfo()
                            }
                        }

                        override fun onDialogNegativeResult(dialog: DialogFragment) {}
                    })

                    dp.show(fragmentManager, "添加记录信息")
                }
            }

            R.id.iv_minus -> {
                updateAct(SELECTED_MINUS)
                mCurType = ""
                mLHMData.forEach {
                    it[KEY_SELECTED] = VAL_NOT_SELECTED
                }

                mRLActAccept.visibility = View.INVISIBLE
                (mGVHolder.adapter as GVTypeAdapter).notifyDataSetChanged()
            }

            R.id.iv_accept -> {
                //for gv
                LinkedList<Int>().apply {
                    mLHMData.filter { it[KEY_SELECTED] == VAL_SELECTED }
                            .map { add(Integer.valueOf(it[KEY_ID])) }
                }.let1 {lsId ->
                    if (lsId.isNotEmpty()) {
                        DlgAlert.showAlert(context!!, R.string.dlg_warn, "请确认是否删除数据!") {
                            it.setPositiveButton("确认") { _, _ ->
                                RecordTypeDBUtility.instance.removeDatas(lsId)

                                loadInfo()
                                updateAct(SELECTED_ACCEPT)
                            }

                            it.setNegativeButton("取消") { _, _ -> }
                        }
                    } else {
                        loadInfo()
                        updateAct(SELECTED_ACCEPT)
                    }
                }
            }

            R.id.iv_reject -> {
                // for ui
                updateAct(SELECTED_REJECT)

                // for gv
                mLHMData.map { it[KEY_SELECTED] = VAL_NOT_SELECTED }

                (mGVHolder.adapter as GVTypeAdapter).notifyDataSetChanged()
            }
        }
    }

    /**
     * load data to gird view
     */
    private fun loadInfo() {
        mLHMData.clear()
        if (mEditType == GlobalDef.STR_RECORD_PAY) {
            ArrayList(RecordTypeDBUtility.instance.allPayItem)
        } else {
            ArrayList(RecordTypeDBUtility.instance.allIncomeItem)
        }.let1 {
            it.sortWith(Comparator { o1, o2 ->
                o1.type.compareTo(o2.type)
            })

            mLHMData.addAll(it.map {
                HashMap<String, String>().apply {
                    put(KEY_NAME, it.type)
                    put(KEY_NOTE, it.note)
                    put(KEY_SELECTED, VAL_NOT_SELECTED)
                    put(KEY_ID, it._id.toString())
                }
            })
        }

        mGVHolder.adapter = GVTypeAdapter(activity!!, mLHMData,
                arrayOf(KEY_NAME, KEY_NOTE), intArrayOf(R.id.tv_type_name, R.id.tv_type_note))
    }

    /**
     * update actions
     * @param type  for type
     */
    private fun updateAct(type: Int) {
        when (type) {
            SELECTED_NONE -> {
                mRLActPencil.visibility = View.INVISIBLE
                mRLActAdd.setBackgroundColor(mCLNotSelected)

                mRLActAdd.visibility = View.VISIBLE
                mRLActAdd.setBackgroundColor(mCLNotSelected)

                mRLActMinus.visibility = View.VISIBLE
                mRLActMinus.setBackgroundColor(mCLNotSelected)

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

                mRLActMinus.isSelected = false
                mRLActMinus.setBackgroundColor(mCLNotSelected)
            }
        }
    }

    /**
     * after gridview item selected
     * @param position      current click position
     */
    private fun onGVItemClick(position: Int) {
        val hm = mLHMData[position]
        if (mRLActMinus.isSelected) {
            hm[KEY_SELECTED] = if (hm[KEY_SELECTED] == VAL_SELECTED)
                VAL_NOT_SELECTED
            else
                VAL_SELECTED

            mRLActAccept.visibility = if (0 == mLHMData.count { it[KEY_SELECTED] == VAL_SELECTED })
                View.INVISIBLE else View.VISIBLE

            (mGVHolder.adapter as GVTypeAdapter).notifyDataSetChanged()
        } else {
            val tvStr = hm[KEY_NAME]
            mLHMData.forEach {
                if (it[KEY_NAME] == tvStr) {
                    if (it[KEY_SELECTED] == VAL_NOT_SELECTED) {
                        it[KEY_SELECTED] = VAL_SELECTED

                        mRLActPencil.visibility = View.VISIBLE
                        mCurType = tvStr
                    } else {
                        it[KEY_SELECTED] = VAL_NOT_SELECTED

                        mRLActPencil.visibility = View.INVISIBLE
                        mCurType = ""
                    }
                } else {
                    it[KEY_SELECTED] = VAL_NOT_SELECTED
                }
            }

            (mGVHolder.adapter as GVTypeAdapter).notifyDataSetChanged()
        }
    }

    /**
     * adapter for gridview
     */
    inner class GVTypeAdapter
    internal constructor(context: Context, data: List<Map<String, *>>, from: Array<String?>, to: IntArray)
        : MoreAdapter(context, data, R.layout.gi_record_type, from, to) {
        override fun loadView(pos: Int, vhHolder: ViewHolder) {
            vhHolder.convertView.setBackgroundColor(if (mLHMData[pos][KEY_SELECTED] == VAL_SELECTED)
                mCLSelected else mCLNotSelected)
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