package wxm.KeepAccount.ui.data.show.note.base

/**
 * @author      WangXM
 * @version     create：2018/4/29
 */
enum class EOperation constructor(var mID: Int) {
    DELETE(1),
    EDIT(2);

    val isEdit: Boolean
        get() = mID == EDIT.mID

    val isDelete: Boolean
        get() = mID == DELETE.mID
}