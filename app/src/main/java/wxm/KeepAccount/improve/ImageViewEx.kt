package wxm.KeepAccount.improve

import android.net.Uri
import android.widget.ImageView
import java.io.File

/**
 * @author      WangXM
 * @version     create：2018/6/6
 */

/**
 * show image in [fn] in imageView
 */
fun ImageView.setImagePath(fn:String)   {
    this.setImageURI(Uri.fromFile(File(fn)))
}