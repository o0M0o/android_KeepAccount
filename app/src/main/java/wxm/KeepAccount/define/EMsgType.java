package wxm.KeepAccount.define;

/**
 * msg type
 * Created by WangXM on 2018/2/19.
 */
public enum EMsgType {
    USR_ADD("add usr", 1001),
    USR_LOGIN("usr login", 1002),
    USR_LOGOUT("usr logout", 1003),

    REPLAY("replay", 9999);

    private String szType;
    private int iId;

    EMsgType(String type, int id)    {
        szType = type;
        iId = id;
    }

    /**
     * get type name
     * @return  type name
     */
    public String getType()   {
        return szType;
    }

    /**
     * get type id
     * @return  type id
     */
    public int getId()  {
        return iId;
    }

    /**
     * get EMsgType from id
     * @param id    id for msg type
     * @return      EMsgType or null
     */
    public static EMsgType getEMsgType(int id) {
        for(EMsgType et : EMsgType.values())    {
            if(et.getId() == id)
                return et;
        }

        return null;
    }
}
