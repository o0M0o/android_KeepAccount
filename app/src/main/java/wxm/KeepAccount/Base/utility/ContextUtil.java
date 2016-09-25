package wxm.KeepAccount.Base.utility;

import android.app.Application;
import wxm.KeepAccount.Base.handler.GlobalMsgHandler;

/**
 * Created by 123 on 2016/5/7.
 * 获取全局context
 */
public class ContextUtil extends Application {
    private GlobalMsgHandler    mMHHandler;

    private static ContextUtil instance;
    public static ContextUtil getInstance() {
        return instance;
    }

    public static GlobalMsgHandler getMsgHandler()  {
        if(null != instance)
            return instance.mMHHandler;

        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        instance = this;
        mMHHandler = new GlobalMsgHandler();
    }
}
