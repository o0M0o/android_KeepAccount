package wxm.KeepAccount.ui.dialog

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.SimpleAdapter
import android.widget.TextView

import java.util.ArrayList
import java.util.Collections
import java.util.HashMap

import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnItemClick
import kotterknife.bindView
import wxm.androidutil.Dialog.DlgOKOrNOBase
import wxm.androidutil.util.UtilFun
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.ui.data.edit.RecordInfo.ACRecordInfoEdit
import wxm.KeepAccount.ui.dialog.base.DlgResource
import wxm.KeepAccount.utility.ContextUtil
import wxm.uilib.IconButton.IconButton

/**
 * select 'record type'
 * Created by WangXM on 2016/11/1.
 */
class DlgSelectRecordType : DlgOKOrNOBase() {
    private val mGVMain: GridView by bindView(R.id.gv_record_info)
    private val mIBSort: IconButton by bindView(R.id.ib_sort)

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

    override fun InitDlgView(): View? {
        if (UtilFun.StringIsNullOrEmpty(mRootType) || GlobalDef.STR_RECORD_PAY != mRootType && GlobalDef.STR_RECORD_INCOME != mRootType)
            return null

        // init data
        mGAAdapter = GVTypeAdapter(activity, mLHMData,
                arrayOf(KEY_NAME, KEY_NOTE),
                intArrayOf(R.id.tv_type_name, R.id.tv_type_note))
        InitDlgTitle(if (GlobalDef.STR_RECORD_PAY == mRootType) "选择支出类型" else "选择收入类型",
                "接受", "放弃")

        // for UI component
        val vw = View.inflate(activity, R.layout.dlg_select_record_info, null)
        ButterKnife.bind(this, vw)

        mGVMain.adapter = mGAAdapter

        // for gridview show
        loadData()
        return vw
    }

    @OnItemClick(R.id.gv_record_info)
    fun onGVItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val tv_str = mLHMData[position][KEY_NAME]
        if (tv_str != curType) {
            for (hm in mLHMData) {
                if (hm[KEY_NAME] == tv_str) {
                    hm[KEY_SELECTED] = VAL_SELECTED
                } else {
                    hm[KEY_SELECTED] = VAL_NOT_SELECTED
                }
            }

            curType = tv_str
            mGAAdapter!!.notifyDataSetChanged()
        }
    }

    /**
     * addition action
     * @param v     action view
     */
    @OnClick(R.id.ib_sort, R.id.ib_manage)
    fun onActionClick(v: View) {
        val vid = v.id
        when (vid) {
            R.id.ib_manage -> {
                val it = Intent(context, ACRecordInfoEdit::class.java)
                it.putExtra(ACRecordInfoEdit.IT_PARA_RECORDTYPE, mRootType)

                startActivityForResult(it, 1)
            }

            R.id.ib_sort -> {
                val cur_name = mIBSort.actName
                val is_up = DlgResource.mSZSortByNameUp == cur_name

                mIBSort.actName = if (is_up) DlgResource.mSZSortByNameDown else DlgResource.mSZSortByNameUp
                mIBSort.setActIcon(if (is_up) R.drawable.ic_sort_down_1 else R.drawable.ic_sort_up_1)

                loadData()
            }
        }
    }


    private fun loadData() {
        val rd = ContextUtil.recordTypeUtility
        val alType = if (GlobalDef.STR_RECORD_PAY == mRootType)
            rd.allPayItem
        else
            rd.allIncomeItem

        val is_up = DlgResource.mSZSortByNameUp == mIBSort.actName
        Collections.sort(alType) { o1, o2 ->
            if (is_up)
                o1.type!!.compareTo(o2.type!!)
            else
                o2.type!!.compareTo(o1.type!!)
        }

        mLHMData.clear()
        for (ri in alType) {
            val hmd = HashMap<String, String>()
            ri.type?.let { hmd[KEY_NAME] = it }
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

        override fun getView(position: Int, view: View, arg2: ViewGroup): View? {
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
        private val KEY_NAME = "key_name"
        private val KEY_NOTE = "key_note"
        private val KEY_SELECTED = "key_selected"

        private val VAL_SELECTED = "val_selected"
        private val VAL_NOT_SELECTED = "val_not_selected"

        private var mCRWhite: Int = 0
        private var mCRTextFit: Int = 0
        private var mCRTextHalfFit: Int = 0

        init {
            val ct = ContextUtil.instance
            if (null != ct) {
                val res = ct.resources
                val te = ct.theme

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mCRWhite = res.getColor(R.color.white, te)
                    mCRTextFit = res.getColor(R.color.text_fit, te)
                    mCRTextHalfFit = res.getColor(R.color.text_half_fit, te)
                } else {
                    mCRWhite = res.getColor(R.color.white)
                    mCRTextFit = res.getColor(R.color.text_fit)
                    mCRTextHalfFit = res.getColor(R.color.text_half_fit)
                }
            }
        }
    }
}
