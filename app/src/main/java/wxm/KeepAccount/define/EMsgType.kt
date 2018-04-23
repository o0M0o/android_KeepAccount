package wxm.KeepAccount.define

/**
 * msg type
 * Created by WangXM on 2018/2/19.
 */
enum class EMsgType(val type: String, val id: Int) {
    USR_ADD("add usr", 1001),
    USR_LOGIN("usr login", 1002),
    USR_LOGOUT("usr logout", 1003),

    REPLAY("replay", 9999);

    companion object {
        /**
         * get EMsgType from id
         * @param id    id for msg type
         * @return      EMsgType or null
         */
        fun getEMsgType(id: Int): EMsgType? {
            for (et in EMsgType.values()) {
                if (et.id == id)
                    return et
            }

            return null
        }
    }
}
