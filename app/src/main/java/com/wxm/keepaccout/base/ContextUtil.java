package com.wxm.keepaccout.base;

import android.app.Application;

/**
 * Created by 123 on 2016/5/7.
 * 获取全局context
 */
public class ContextUtil extends Application {
    private static ContextUtil instance;

    public static ContextUtil getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        instance = this;
    }
}