package wxm.KeepAccount.db

import com.j256.ormlite.dao.RuntimeExceptionDao
import org.greenrobot.eventbus.EventBus
import wxm.KeepAccount.improve.notExist
import wxm.KeepAccount.item.IImage
import wxm.KeepAccount.item.NoteImageItem
import wxm.KeepAccount.utility.AppUtil
import wxm.KeepAccount.utility.getFileName
import wxm.androidutil.db.DBUtilityBase
import wxm.androidutil.improve.doJudge
import wxm.androidutil.improve.let1
import wxm.androidutil.log.TagLog
import java.io.File
import java.util.*

/**
 * utility for note's image
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
        val instance = NoteImageUtility()

        /**
         * load [note] image
         */
        fun loadNoteImages(note: IImage): Boolean {
            note.images = getNoteImages(note)
            return true
        }

        /**
         * clear [note] image
         */
        fun clearNoteImages(note: IImage) {
            note.images.forEach {
                removeImage(note, it.imagePath)
            }
            note.images.clear()
        }

        /**
         * save image for [ig]
         */
        fun saveNoteImage(ig: IImage) {
            val oldImages = getNoteImages(ig)
            oldImages.filter {
                val path = it.imagePath
                ig.images.notExist{ it.imagePath == path }
            }.forEach {
                File(it.imagePath).let1 {
                    if (it.exists() && it.isFile) {
                        it.delete()
                    }
                }
                instance.dbHelper.delete(it)
            }

            ig.images.forEach {
                val path = it.imagePath
                if(oldImages.notExist { it.imagePath == path }) {
                    instance.createData(it)
                }
            }
        }

        /**
         * remove image [igPath] from [note]
         */
        private fun removeImage(note: IImage, igPath: String): Boolean {
            val pn = note.images.find { it.imagePath == igPath } ?: return false

            File(pn.imagePath).let1 {
                if (it.exists() && it.isFile) {
                    it.delete()
                }
            }
            instance.dbHelper.delete(pn)


            TagLog.i("remove imageId=${pn.id}, igPath=${getFileName(pn.imagePath)}")
            return true
        }

        /**
         * get [nd] image
         */
        private fun getNoteImages(nd: IImage): LinkedList<NoteImageItem> {
            val dbHelper = instance.dbHelper
            val smt = dbHelper.queryBuilder().where()
                    .eq(NoteImageItem.FIELD_FOREIGN_ID, nd.holderId)
                    .and().eq(NoteImageItem.FIELD_IMAGE_TYPE, nd.holderType)
                    .and().eq(NoteImageItem.FIELD_STATUS, NoteImageItem.STATUS_USE).prepare()

            return LinkedList<NoteImageItem>().apply {
                dbHelper.query(smt)?.filterNotNull()?.forEach {
                    add(it)
                }
            }
        }
    }
}