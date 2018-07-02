package wxm.KeepAccount.db

import com.j256.ormlite.dao.RuntimeExceptionDao
import org.greenrobot.eventbus.EventBus
import wxm.KeepAccount.item.DebtNoteItem
import wxm.KeepAccount.utility.AppUtil
import wxm.androidutil.db.DBUtilityBase
import wxm.androidutil.improve.let1

/**
 * utility for note's image
 * @author      WangXM
 * @version     create：2018/6/5
 */
class DebtUtility : DBUtilityBase<DebtNoteItem, Int>() {
    override fun getDBHelper(): RuntimeExceptionDao<DebtNoteItem, Int> = AppUtil.dbHelper.debtREDao

    override fun onDataCreate(cd: MutableList<Int>?) {
        EventBus.getDefault().post(DBDataChangeEvent())
    }

    override fun onDataRemove(dd: MutableList<Int>?) {
        EventBus.getDefault().post(DBDataChangeEvent())
    }

    override fun onDataModify(md: MutableList<Int>?) {
        EventBus.getDefault().post(DBDataChangeEvent())
    }

    override fun createData(nr: DebtNoteItem): Boolean {
        createItemChore(nr)
        return super.createData(nr)
    }

    override fun createDatas(nr: MutableList<DebtNoteItem>): Int {
        nr.forEach {
            createItemChore(it)
        }

        return super.createDatas(nr)
    }

    override fun getData(id: Int): DebtNoteItem? {
        val ret = super.getData(id)
        if(null != ret) {
            getItemChore(ret)
        }

        return ret
    }

    override fun getAllData(): MutableList<DebtNoteItem> {
        val ret = super.getAllData()
        ret?.filterNotNull()?.forEach {
            getItemChore(it)
        }

        return ret
    }

    override fun modifyData(np: DebtNoteItem): Boolean {
        modifyItemChore(np)
        return super.modifyData(np)
    }

    override fun modifyData(ls_np: MutableList<DebtNoteItem>): Int {
        ls_np.forEach {
            modifyItemChore(it)
        }

        return super.modifyData(ls_np)
    }

    override fun removeData(id: Int): Boolean {
        getData(id)?.let1 {
            removeItemChore(it)
        }

        return super.removeData(id)
    }

    override fun removeDatas(ls_id: MutableList<Int>): Int {
        ls_id.forEach {
            getData(it)?.let1 {
                removeItemChore(it)
            }
        }

        return super.removeDatas(ls_id)
    }

    /// chores begin
    private fun createItemChore(nr: DebtNoteItem)   {
        NoteImageUtility.saveNoteImage(nr)
        DebtActionUtility.instance.createDatas(nr.actions)
    }

    private fun getItemChore(nr: DebtNoteItem)   {
        NoteImageUtility.loadNoteImages(nr)
        DebtActionUtility.getActions(nr)
    }

    private fun modifyItemChore(nr: DebtNoteItem)   {
        NoteImageUtility.saveNoteImage(nr)
        DebtActionUtility.modifyActions(nr)
    }

    private fun removeItemChore(nr: DebtNoteItem)   {
        NoteImageUtility.clearNoteImages(nr)
        DebtActionUtility.removeActions(nr)
    }
    /// chores end

    companion object {
        val instance = DebtUtility()
    }
}