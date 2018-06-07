package wxm.KeepAccount.db

import com.j256.ormlite.dao.RuntimeExceptionDao
import org.greenrobot.eventbus.EventBus
import wxm.KeepAccount.item.INote
import wxm.KeepAccount.item.NoteImageItem
import wxm.KeepAccount.utility.AppUtil
import wxm.KeepAccount.utility.let1
import wxm.androidutil.db.DBUtilityBase
import wxm.androidutil.log.TagLog
import wxm.androidutil.util.doJudge
import java.io.File
import java.util.*

/**
 * @author      WangXM
 * @version     create：2018/6/5
 */
class NoteImageUtility : DBUtilityBase<NoteImageItem, Int>() {
    override fun getDBHelper(): RuntimeExceptionDao<NoteImageItem, Int> {
        return AppUtil.dbHelper.noteImageREDao
    }

    override fun onDataCreate(cd: MutableList<Int>?) {
        EventBus.getDefault().post(DBDataChangeEvent())
    }

    override fun onDataRemove(dd: MutableList<Int>?) {
        EventBus.getDefault().post(DBDataChangeEvent())
    }

    override fun onDataModify(md: MutableList<Int>?) {
        EventBus.getDefault().post(DBDataChangeEvent())
    }

    companion object {
        fun addImage(note: INote, igPath: String): Boolean {
            return AppUtil.noteImageUtility.createData(NoteImageItem().apply {
                foreignID = note.id
                imageType = note.noteType()
                imagePath = igPath
            }).doJudge(
                    {
                        TagLog.i("add noteId=${note.id}, igPath=$igPath")
                        true
                    },
                    { false }
            )
        }

        fun removeImage(note: INote, igPath: String): Boolean {
            if (!note.images.contains(igPath))
                return false

            val dbHelper = AppUtil.noteImageUtility.dbHelper
            val op = dbHelper.deleteBuilder()
            op.where().eq(NoteImageItem.FIELD_FOREIGN_ID, note.id)
                    .and().eq(NoteImageItem.FIELD_IMAGE_TYPE, note.noteType())
                    .and().eq(NoteImageItem.FIELD_IMAGE_PATH, igPath)
            op.delete()

            File(igPath).let1 {
                if (it.exists() && it.isFile) {
                    it.delete()
                }
            }
            TagLog.i("remove noteId=${note.id}, igPath=$igPath")
            return true
        }

        fun setNoteImages(note: INote): Boolean {
            val dbHelper = AppUtil.noteImageUtility.dbHelper
            val prepare = dbHelper.queryBuilder().where()
                    .eq(NoteImageItem.FIELD_FOREIGN_ID, note.id)
                    .and().eq(NoteImageItem.FIELD_IMAGE_TYPE, note.noteType())
                    .and().eq(NoteImageItem.FIELD_STATUS, NoteImageItem.STATUS_USE).prepare()

            val ret = LinkedList<String>()
            dbHelper.query(prepare)?.filterNotNull()?.forEach {
                ret.add(it.imagePath)
            }

            note.images = ret
            return true
        }

        fun clearNoteImages(note: INote) {
            note.images.forEach {
                removeImage(note, it)
            }

            note.images.clear()
        }
    }
}