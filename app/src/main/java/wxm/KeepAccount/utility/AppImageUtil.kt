package wxm.KeepAccount.utility

import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import wxm.KeepAccount.define.GlobalDef
import wxm.androidutil.improve.forObj
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * @author      WangXM
 * @version     create：2018/5/29
 */

private fun getRealPathFromURI(contentURI: Uri): String {
    return AppUtil.self.contentResolver
            .query(contentURI, null, null, null, null)
            .forObj(
                    {
                        it.use {
                            it.moveToFirst()
                            val idx = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                            it.getString(idx)

                        }
                    },
                    { contentURI.path }
            )
}

/**
 * return full path use [dir] as directory path and [fn] as file name
 */
fun createPath(dir: String, fn: String): String {
    return "$dir${GlobalDef.FILE_PATH_SEPARATOR}$fn"
}

@Throws(IOException::class)
private fun fileCopy(src: File, dst: File) {
    val inStream = FileInputStream(src)
    val outStream = FileOutputStream(dst)
    val inChannel = inStream.channel
    val outChannel = outStream.channel
    inChannel.transferTo(0, inChannel.size(), outChannel)
    inStream.close()
    outStream.close()
}

/**
 * save image from [imageUri] to app image directory
 * return image file name
 */
fun saveImage(imageUri: Uri): String {
    val realPath = getRealPathFromURI(imageUri)
    val fnPos = realPath.lastIndexOf(".")
    if (-1 != fnPos) {
        val newFN = AppUtil.imagePath + "/" + UUID.randomUUID().toString() + realPath.substring(fnPos)
        fileCopy(File(realPath), File(newFN))

        return newFN
    }

    return ""
}

/**
 * get default usr icon
 */
fun defaultUsrIcon(): String = createPath(AppUtil.imagePath, "usr_default_icon.png")

/**
 * save [bm] to [fn] as [cf] type
 */
fun saveBitmapToJPGFile(bm: Bitmap, fn: String,
                        cf: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG): Boolean {
    return FileOutputStream(File(fn)).let {
        try {
            it.use { f ->
                bm.compress(cf, 100, f)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
}

fun getFileName(pn:String): String  {
    return pn.lastIndexOf(GlobalDef.FILE_PATH_SEPARATOR).let {
        if(-1 != it)    pn.substring(it + 1)
        else pn
    }
}