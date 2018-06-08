package wxm.KeepAccount.db

import com.j256.ormlite.dao.RuntimeExceptionDao
import org.greenrobot.eventbus.EventBus
import wxm.KeepAccount.item.INote
import wxm.KeepAccount.item.NoteImageItem
import wxm.KeepAccount.utility.AppUtil
import wxm.KeepAccount.utility.getFileName
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
            val pn = note.images.find { it.imagePath == igPath }
            if(null != pn) {
                if(pn.status == NoteImageItem.STATUS_NOT_USE)   {
                    pn.status = NoteImageItem.STATUS_USE
                    AppUtil.noteImageUtility.modifyData(pn)
                }
                return true
            }

            return AppUtil.noteImageUtility.createData(NoteImageItem().apply {
                foreignID = note.id
                imageType = note.noteType()
                imagePath = igPath
            }).doJudge(
                    {
                        TagLog.i("add noteId=${note.id}, igPath=${getFileName(igPath)}")
                        setNoteImages(note)
                        true
                    },
                    { false }
            )
        }

        fun removeImage(note: INote, igPath: String): Boolean {
            val pn = note.images.find { it.imagePath == igPath } ?: return false

            File(pn.imagePath).let1 {
                if (it.exists() && it.isFile) {
                    it.delete()
                }
            }
            AppUtil.noteImageUtility.dbHelper.delete(pn)


            TagLog.i("remove imageId=${pn.id}, igPath=${getFileName(pn.imagePath)}")
            return true
        }

        fun setNoteImages(note: INote): Boolean {
            val dbHelper = AppUtil.noteImageUtility.dbHelper
            val prepare = dbHelper.queryBuilder().where()
                    .eq(NoteImageItem.FIELD_FOREIGN_ID, note.id)
                    .and().eq(NoteImageItem.FIELD_IMAGE_TYPE, note.noteType())
                    .and().eq(NoteImageItem.FIELD_STATUS, NoteImageItem.STATUS_USE).prepare()

            dbHelper.query(prepare)?.filterNotNull()?.forEach {
                note.images.add(it)
            }

            return true
        }

        fun clearNoteImages(note: INote) {
            if(note.images.isNotEmpty()) {
                note.images.forEach {
                    removeImage(note, it.imagePath)
                }
                note.images.clear()
            }
        }
    }
}