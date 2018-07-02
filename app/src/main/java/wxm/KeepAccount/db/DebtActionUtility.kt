package wxm.KeepAccount.db

import com.j256.ormlite.dao.RuntimeExceptionDao
import org.greenrobot.eventbus.EventBus
import wxm.KeepAccount.improve.exist
import wxm.KeepAccount.improve.notExist
import wxm.KeepAccount.item.DebtActionItem
import wxm.KeepAccount.item.DebtNoteItem
import wxm.KeepAccount.utility.AppUtil
import wxm.androidutil.db.DBUtilityBase
import wxm.androidutil.improve.let1
import java.util.*

/**
 * utility for note's image
 * @author      WangXM
 * @version     create：2018/6/5
 */
class DebtActionUtility : DBUtilityBase<DebtActionItem, Int>() {
    override fun getDBHelper(): RuntimeExceptionDao<DebtActionItem, Int> = AppUtil.dbHelper.debtActionREDao

    override fun onDataCreate(cd: MutableList<Int>?) {
        EventBus.getDefault().post(DBDataChangeEvent())
    }

    override fun onDataRemove(dd: MutableList<Int>?) {
        EventBus.getDefault().post(DBDataChangeEvent())
    }

    override fun onDataModify(md: MutableList<Int>?) {
        EventBus.getDefault().post(DBDataChangeEvent())
    }

    override fun createData(nr: DebtActionItem): Boolean {
        NoteImageUtility.saveNoteImage(nr)
        return super.createData(nr)
    }

    override fun createDatas(nr: MutableList<DebtActionItem>): Int {
        nr.forEach {
            NoteImageUtility.saveNoteImage(it)
        }

        return super.createDatas(nr)
    }

    override fun getData(id: Int): DebtActionItem? {
        val ret = super.getData(id)
        if(null != ret) {
            NoteImageUtility.loadNoteImages(ret)
        }

        return ret
    }

    override fun getAllData(): MutableList<DebtActionItem> {
        val ret = super.getAllData()
        ret?.filterNotNull()?.forEach {
            NoteImageUtility.loadNoteImages(it)
        }

        return ret
    }

    override fun modifyData(np: DebtActionItem): Boolean {
        NoteImageUtility.saveNoteImage(np)
        return super.modifyData(np)
    }

    override fun modifyData(ls_np: MutableList<DebtActionItem>): Int {
        ls_np.forEach {
            NoteImageUtility.saveNoteImage(it)
        }

        return super.modifyData(ls_np)
    }

    override fun removeData(id: Int): Boolean {
        getData(id)?.let1 {
            NoteImageUtility.clearNoteImages(it)
        }

        return super.removeData(id)
    }

    override fun removeDatas(ls_id: List<Int>): Int {
        ls_id.forEach {
            getData(it)?.let1 {
                NoteImageUtility.clearNoteImages(it)
            }
        }

        return super.removeDatas(ls_id)
    }

    companion object {
        val instance = DebtActionUtility()

        /**
         * get debt actions for [nr]
         */
        fun getActions(nr: DebtNoteItem): List<DebtActionItem>  {
            val smt = instance.dbHelper.queryBuilder().where()
                    .eq(DebtActionItem.FIELD_DEBT_ID, nr.id).prepare()

            return LinkedList<DebtActionItem>().apply {
                instance.dbHelper.query(smt)?.filterNotNull()?.forEach {
                    add(it)
                }
            }
        }

        /**
         * modify debt actions for [nr]
         */
        fun modifyActions(nr: DebtNoteItem) {
            val oldActs = getActions(nr)
            oldActs.filter {
                val oldId = it.id
                nr.actions.notExist { oldId == it.id }
            }.forEach {
                instance.removeData(it.id)
            }

            nr.actions.forEach {
                val newId = it.id
                if(oldActs.exist { newId == it.id })    {
                    instance.modifyData(it)
                } else  {
                    instance.createData(it)
                }
            }
        }

        /**
         * remove actions for [nr]
         */
        fun removeActions(nr: DebtNoteItem) {
            instance.removeDatas(getActions(nr).map { it.id }.toList())
            instance.removeDatas(nr.actions.map { it.id }.toList())
        }
    }
}