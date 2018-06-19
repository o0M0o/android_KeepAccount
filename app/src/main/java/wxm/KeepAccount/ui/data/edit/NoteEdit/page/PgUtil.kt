package wxm.KeepAccount.ui.data.edit.NoteEdit.page

import android.text.Editable
import android.text.TextWatcher
import wxm.KeepAccount.db.NoteImageUtility
import wxm.KeepAccount.item.INote
import wxm.KeepAccount.ui.base.TouchUI.TouchEditText
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.androidutil.improve.let1

/**
 * @author      WangXM
 * @version     create：2018/6/9
 */

/**
 * for input money
 */
class MoneyTextWatcher(private val mTEHolder : TouchEditText) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        s?.let1 { ed ->
            ed.toString().indexOf(".").let1 {
                if (it >= 0) {
                    if ((ed.length - (it + 1)) > 2) {
                        mTEHolder.error = "小数点后超过两位数!"
                        mTEHolder.setText(s.subSequence(0, it + 3))
                    }
                }
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}

/**
 * use [pn] as [nd] new image attr
 * if [createFlg] is true is new note else is modified data
 */
fun refreshNoteImage(nd: INote, pn:String, createFlg:Boolean)    {
    if (createFlg) {
        if(pn.isNotEmpty())   {
            NoteImageUtility.addImage(nd, pn)
        }
    } else {
        if(pn.isEmpty())  {
            NoteImageUtility.clearNoteImages(nd)
        } else  {
            nd.images.find { it.imagePath == pn }.let1 {
                if(null == it)  {
                    NoteImageUtility.clearNoteImages(nd)
                    NoteImageUtility.addImage(nd, pn)
                }
            }
        }
    }

    // update app global data
    NoteDataHelper.findNote(nd.id, nd.noteType())!!.let1 {
        NoteImageUtility.updateNoteImages(it)
    }
}

/**
 * use [pns] as [nd] new image attr
 * if [createFlg] is true is new note else is modified data
 */
fun refreshNoteImage(nd: INote, pns:List<String>, createFlg:Boolean)    {
    if (createFlg) {
        pns.forEach {
            if (it.isNotEmpty()) {
                NoteImageUtility.addImage(nd, it)
            }
        }
    } else {
        if(pns.isEmpty())  {
            NoteImageUtility.clearNoteImages(nd)
        } else  {
            pns.forEach { NoteImageUtility.addImage(nd, it) }
        }
    }

    // update app global data
    NoteDataHelper.findNote(nd.id, nd.noteType())!!.let1 {
        NoteImageUtility.updateNoteImages(it)
    }
}
