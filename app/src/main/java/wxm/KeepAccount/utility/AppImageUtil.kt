package wxm.KeepAccount.utility

import android.net.Uri
import android.provider.MediaStore
import wxm.KeepAccount.define.GlobalDef
import wxm.androidutil.util.FileUtil
import wxm.androidutil.util.doJudge
import wxm.androidutil.util.forObj
import java.io.*
import java.nio.file.Files
import java.nio.file.StandardCopyOption
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
fun createPath(dir:String, fn:String): String   {
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
    if(-1 != fnPos)    {
        val newFN = AppUtil.imagePath + "/" + UUID.randomUUID().toString() + realPath.substring(fnPos)
        fileCopy(File(realPath), File(newFN))

        return newFN
    }

    return ""
}

fun defaultUsrIcon(): String {
    //return AppUtil.imagePath + "/" + defaultUsrIconName()
    return createPath("//android_asset", defaultUsrIconName())
}

fun defaultUsrIconName(): String {
    return "usr_default_icon.png"
}