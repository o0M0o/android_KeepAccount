package wxm.KeepAccount.ui.base.page

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import wxm.KeepAccount.improve.notExist
import wxm.KeepAccount.item.IImage
import wxm.KeepAccount.item.NoteImageItem
import wxm.KeepAccount.ui.base.TouchUI.TouchEditText
import wxm.androidutil.improve.let1
import wxm.androidutil.time.CalendarUtility
import java.util.*

/**
 * page utility
 * @author      WangXM
 * @version     create：2018/6/9
 */

/**
 * for input money
 */
class MoneyTextWatcher(private val mTEHolder: TouchEditText) : TextWatcher {
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

object PgUtil {
    /**
     * use [pns] as [nd] new image attr
     */
    fun refreshNoteImage(nd: IImage, pns: List<String>) {
        if (pns.isEmpty()) {
            nd.images.clear()
        } else {
            // add new image
            pns.filter {
                val path = it
                nd.images.notExist { path == it.imagePath }
            }.forEach {
                nd.images.add(NoteImageItem().apply {
                    foreignID = nd.holderId
                    imageType = nd.holderType
                    imagePath = it
                })
            }

            // remove image
            nd.images.removeAll(nd.images.filter {
                val path = it.imagePath
                pns.notExist { path == it }
            })
        }
    }

    /**
     * dlg for pick data
     * [ct] is context for dlg
     * [date] is dlg init date
     * [op] is invoked after picked
     */
    fun pickDateDlg(ct: Context, date: Calendar, op: (date: Calendar) -> Unit) {
        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val strDate = String.format(Locale.CHINA, "%04d-%02d-%02d",
                        year, month + 1, dayOfMonth)
                val tm = String.format(Locale.CHINA, "%s %02d:%02d",
                        strDate, hourOfDay, minute)
                op(CalendarUtility.YearMonthDayHourMinute.parse(tm))
            }.let1 {
                TimePickerDialog(ct, it,
                        date.get(Calendar.HOUR_OF_DAY),
                        date.get(Calendar.MINUTE), true).show()
            }
        }.let1 {
            DatePickerDialog(ct, it,
                    date.get(Calendar.YEAR), date.get(Calendar.MONTH),
                    date.get(Calendar.DAY_OF_MONTH)).show()
        }
    }
}
