package com.wxm.KeepAccount.base.utility;

/**
 * 处理app内部消息接口类
 * Created by 123 on 2016/5/6.
 */
public class AppManager {
    private static final String TAG = "AppManager";

    private static final AppManager ourInstance = new AppManager();
    public static AppManager getInstance() {
        return ourInstance;
    }

    private AppManager() {
    }

}
