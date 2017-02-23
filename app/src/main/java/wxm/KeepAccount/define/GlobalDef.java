package wxm.KeepAccount.define;

/**
 * App全局定义
 * Created by 123 on 2016/5/6.
 */
public class GlobalDef {
    public static final String PACKAGE_NAME = "com.wxm.keepaccount";

    static public final String STR_RECORD_PAY       = "record_pay";
    static public final String STR_RECORD_INCOME    = "record_income";
    static public final String STR_RECORD_BUDGET    = "record_budget";

    static public final String STR_RECORD_DATE      = "record_date";
    static public final String STR_RECORD_TYPE      = "record_type";

    public static final String  STR_CREATE     = "create";
    public static final String  STR_MODIFY     = "modify";

    public static final String STR_PWD_PAD = "JkpYkhiayh@#$_)(";

    public static final String INTENT_LOAD_RECORD_ID     = "record_id";
    public static final String INTENT_LOAD_RECORD_TYPE   = "record_type";

    static public final int INTRET_RECORD_ADD       = 1000;
    static public final int INTRET_RECORD_MODIFY    = 1001;
    static public final int INTRET_DAILY_DETAIL     = 1002;
    static public final int INTRET_SURE             = 1003;
    static public final int INTRET_GIVEUP           = 1099;

    static public final int INTRET_USR_ADD       = 2000;
    static public final int INTRET_USR_LOGOUT    = 2001;

    static public final int INTRET_ERROR         = 9000;


    static public final int INVALID_ID          = -1;

    public final static String DEF_USR_NAME = "default";
    public final static String DEF_USR_PWD  = "123456";

    // for msg
    public static final int MSG_USR_ADDUSR  = 1001;
    public static final int MSG_USR_LOGIN   = 1002;
    public static final int MSG_USR_LOGOUT  = 1003;

    public static final int MSG_REPLY       = 9999;
}