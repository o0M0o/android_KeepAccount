package wxm.KeepAccount.ui.dialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView
import android.widget.SimpleAdapter
import android.widget.TextView
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.ui.data.edit.RecordInfo.ACRecordInfoEdit
import wxm.KeepAccount.ui.dialog.base.DlgResource
import wxm.KeepAccount.utility.ContextUtil
import wxm.KeepAccount.utility.EventHelper
import wxm.androidutil.dialog.DlgOKOrNOBase
import wxm.uilib.IconButton.IconButton
import java.util.*
import kotlin.collections.ArrayList

/**
 * select 'record type'
 * Created by WangXM on 2016/11/1.
 */
class DlgSelectRecordType : DlgOKOrNOBase() {
    //private val mGVMain: GridView by bindView(R.id.gv_record_info)
    private lateinit var mGVMain: GridView
    //private val mIBSort: IconButton by bindView(R.id.ib_sort)
    private lateinit var mIBSort: IconButton

    private var mLHMData: ArrayList<HashMap<String, String>> = ArrayList()
    private var mGAAdapter: GVTypeAdapter? = null
    private var mRootType: String? = null

    /**
     * current 'record type'
     */
    var curType: String? = null
        private set

    /**
     * set old 'record type'
     * @param rt    can be :
     * -- GlobalDef.STR_RECORD_PAY
     * -- GlobalDef.STR_RECORD_INCOME
     * @param ot    current record type
     */
    fun setOldType(rt: String, ot: String) {
        mRootType = rt
        curType = ot
    }

    override fun createDlgView(savedInstanceState: Bundle?): View {
        initDlgTitle(if (GlobalDef.STR_RECORD_PAY == mRootType) "选择支出类型" else "选择收入类型",
                "接受", "放弃")

        return View.inflate(activity, R.layout.dlg_select_record_info, null)
    }

    override fun initDlgView(savedInstanceState: Bundle?) {
        mGVMain = findDlgChildView(R.id.gv_record_info)!!
        mIBSort = findDlgChildView(R.id.ib_sort)!!

        mGAAdapter = GVTypeAdapter(activity, mLHMData,
                arrayOf(KEY_NAME, KEY_NOTE),
                intArrayOf(R.id.tv_type_name, R.id.tv_type_note))

        mGVMain.adapter = mGAAdapter
        mGVMain.onItemClickListener = OnItemClickListener { _, _, pos, _ ->
            run {
                val tvStr = mLHMData[pos][KEY_NAME]
                if (tvStr != curType) {
                    for (hm in mLHMData) {
                        if (hm[KEY_NAME] == tvStr) {
                            hm[KEY_SELECTED] = VAL_SELECTED
                        } else {
                            hm[KEY_SELECTED] = VAL_NOT_SELECTED
                        }
                    }

                    curType = tvStr
                    mGAAdapter!!.notifyDataSetChanged()
                }
            }
        }

        // for click
        EventHelper.setOnClickListener(dlgView,
                intArrayOf(R.id.ib_manage, R.id.ib_sort),
                View.OnClickListener { v ->
                    when (v.id) {
                        R.id.ib_manage -> {
                            val it = Intent(context, ACRecordInfoEdit::class.java)
                            it.putExtra(ACRecordInfoEdit.IT_PARA_RECORD_TYPE, mRootType)

                            startActivityForResult(it, 1)
                        }

                        R.id.ib_sort -> {
                            val curName = mIBSort.actName
                            val isUp = DlgResource.mSZSortByNameUp == curName

                            mIBSort.actName = if (isUp) DlgResource.mSZSortByNameDown else DlgResource.mSZSortByNameUp
                            mIBSort.setActIcon(if (isUp) R.drawable.ic_sort_down_1 else R.drawable.ic_sort_up_1)

                            loadData()
                        }
                    }
                })

        // for gridview show
        loadData()
    }

    private fun loadData() {
        val rd = ContextUtil.recordTypeUtility
        val alType = if (GlobalDef.STR_RECORD_PAY == mRootType)
            ArrayList(rd.allPayItem)
        else
            ArrayList(rd.allIncomeItem)

        val isUp = DlgResource.mSZSortByNameUp == mIBSort.actName
        alType.sortWith(Comparator { o1, o2 ->
            if (isUp)
                o1.type.compareTo(o2.type)
            else
                o2.type.compareTo(o1.type)
        })

        mLHMData.clear()
        for (ri in alType) {
            val hmd = HashMap<String, String>()
            hmd[KEY_NAME] = ri.type
            ri.note?.let { hmd[KEY_NOTE] = it }

            if (ri.type == curType) {
                hmd[KEY_SELECTED] = VAL_SELECTED
            } else {
                hmd[KEY_SELECTED] = VAL_NOT_SELECTED
            }

            mLHMData.add(hmd)
        }

        mGAAdapter!!.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        loadData()
    }


    /**
     * adapter for gridview
     */
    inner class GVTypeAdapter internal constructor(context: Context, data: List<Map<String, *>>,
                                                   from: Array<String>, to: IntArray) : SimpleAdapter(context, data, R.layout.gi_record_type, from, to) {
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
                    R.drawable.gi_shape_record_type_sel
                else
                    R.drawable.gi_shape_record_type_nosel
                v.setBackgroundResource(curCL)

                val cr = if (hm[KEY_SELECTED] == VAL_SELECTED) mCRWhite else mCRTextFit
                (v.findViewById<View>(R.id.tv_type_name) as TextView).setTextColor(cr)
                (v.findViewById<View>(R.id.tv_type_note) as TextView)
                        .setTextColor(if (cr == mCRTextFit) mCRTextHalfFit else cr)
            }

            return v
        }
    }

    companion object {
        private const val KEY_NAME = "key_name"
        private const val KEY_NOTE = "key_note"
        private const val KEY_SELECTED = "key_selected"

        private const val VAL_SELECTED = "val_selected"
        private const val VAL_NOT_SELECTED = "val_not_selected"

        private val mCRWhite: Int = ContextUtil.getColor(R.color.white)
        private val mCRTextFit: Int = ContextUtil.getColor(R.color.text_fit)
        private val mCRTextHalfFit: Int = ContextUtil.getColor(R.color.text_half_fit)
    }
}
