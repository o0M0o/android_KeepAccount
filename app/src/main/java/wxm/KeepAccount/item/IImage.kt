package wxm.KeepAccount.item

import java.util.*

/**
 * interface for image
 * @author      WangXM
 * @version     create：2018/7/4
 */
interface IImage {
    val holderId : Int

    val holderType: String

    var images: LinkedList<NoteImageItem>
}