package wxm.KeepAccount.event

/**
 * @author      WangXM
 * @version     create：2018/5/28
 */
data class PicPath(val action:Int, val picPath: String)  {
    companion object {
        const val PREVIEW_PIC = 1
        const val REFRESH_PIC = 2
        const val REMOVE_PIC  = 3
    }
}