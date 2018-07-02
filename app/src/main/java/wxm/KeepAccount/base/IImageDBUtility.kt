package wxm.KeepAccount.base

import wxm.KeepAccount.db.NoteImageUtility
import wxm.KeepAccount.item.IImage
import wxm.androidutil.db.DBUtilityBase
import wxm.androidutil.db.IDBRow
import wxm.androidutil.improve.let1

/**
 * @author      WangXM
 * @version     create：2018/7/16
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class IImageDBUtility<D, T> : DBUtilityBase<D, T>()
        where D : IDBRow<T>, D : IImage
{
    override fun createData(nr: D): Boolean {
        createItemChore(nr)
        return super.createData(nr)
    }

    override fun createDatas(nr: List<D>): Int {
        nr.forEach {
            createItemChore(it)
        }

        return super.createDatas(nr)
    }

    override fun getData(id: T): D? {
        val ret = super.getData(id)
        if(null != ret) {
            getItemChore(ret)
        }

        return ret
    }

    override fun getAllData(): List<D> {
        val ret = super.getAllData()
        ret?.filterNotNull()?.forEach {
            getItemChore(it)
        }

        return ret
    }

    override fun modifyData(np: D): Boolean {
        modifyItemChore(np)
        return super.modifyData(np)
    }

    override fun modifyData(ls_np: MutableList<D>): Int {
        ls_np.forEach {
            modifyItemChore(it)
        }

        return super.modifyData(ls_np)
    }

    override fun removeData(id: T): Boolean {
        getData(id)?.let1 {
            removeItemChore(it)
        }

        return super.removeData(id)
    }

    override fun removeDatas(ls_id: List<T>): Int {
        ls_id.forEach {
            getData(it)?.let1 {
                removeItemChore(it)
            }
        }

        return super.removeDatas(ls_id)
    }

    /// chores begin
    protected fun createItemChore(nr: D)   {
        NoteImageUtility.saveNoteImage(nr)
    }

    protected fun getItemChore(nr: D)   {
        NoteImageUtility.loadNoteImages(nr)
    }

    protected fun modifyItemChore(nr: D)   {
        NoteImageUtility.saveNoteImage(nr)
    }

    protected fun removeItemChore(nr: D)   {
        NoteImageUtility.clearNoteImages(nr)
    }
    /// chores end
}