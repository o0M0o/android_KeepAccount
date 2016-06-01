package com.wxm.KeepAccount.BaseLib;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 123 on 2016/5/7.
 * 本类为app的数据类
 */
public class AppModel {
    private static AppModel ourInstance = new AppModel();
    private List<RecordItem> allRecords;
    private boolean dbChange;
    private DBManager dbm = new DBManager(ContextUtil.getInstance());

    // 当前登录用户
    public String cur_usr;

    public static AppModel getInstance() {
        return ourInstance;
    }

    private AppModel() {
        allRecords = null;
        dbChange = false;
    }

    /**
     * 从数据库加载所有记录
     */
    public void LoadAllRecords()    {
        allRecords = dbm.query();
        dbChange = false;
    }

    public RecordItem GetRecord(String sql_tg)  {
        if((null == allRecords) || dbChange)
            LoadAllRecords();

        int id = Integer.parseInt(sql_tg);
        RecordItem ret = null;
        if(null != allRecords) {
            for (RecordItem it : allRecords) {
                if(id == it._id)    {
                    ret = it;
                    break;
                }
            }
        }

        return ret;
    }

    /**
     * 获得所有的记录
     * @return 所有记录
     */
    public List<RecordItem> GetAllRecords()     {
        if((null == allRecords) || dbChange)
            LoadAllRecords();

        ArrayList<RecordItem> ret;
        if(null != allRecords)  {
            ret = new ArrayList<>(allRecords);
        }
        else    {
            ret = new ArrayList<>();
        }

        return ret;
    }

    /**
     * 根据日期条件(例如 : '2016-05-07')获得相关记录
     * @param day_str   日期条件
     * @return  满足日期条件的记录
     */
    public List<RecordItem> GetRecordsByDay(String day_str)   {
        String check_str = "XXXX-XX-XX";
        int check_len = check_str.length();
        if(check_len != day_str.length())
            return null;

        if((null == allRecords) || dbChange)
            LoadAllRecords();

        ArrayList<RecordItem> ret = new ArrayList<>();
        if(null != allRecords)  {
            for(RecordItem it : allRecords) {
                String h_k = it.record_ts.toString().substring(0, check_len);
                if(h_k.equals(day_str)) {
                    RecordItem nit = it;
                    ret.add(nit);
                }
            }
        }

        return ret;
    }

    /**
     * 添加记录到文件
     * @param lsi 待添加的记录集合
     * @return 添加成功返回true
     */
    public boolean AddRecords(List<RecordItem> lsi)    {
        dbm.add(lsi);
        dbChange = true;
        return true;
    }

    /**
     * 修改记录到文件
     * @param lsi 待修改的记录集合
     * @return  修改成功返回true
     */
    public boolean ModifyRecords(List<RecordItem> lsi)  {
        dbm.modify(lsi);
        dbChange = true;
        return true;
    }

    /**
     * 删除文件中的记录
     * @param lsi 待删除的记录集合
     * @return  操作成功返回true
     */
    public boolean DeleteRecords(List<String> lsi)  {
        dbm.deleteRecords(lsi);
        dbChange = true;
        return true;
    }

    /**
     * 检查是否用户'usr'已经存在
     * @param usr   待检查用户
     * @return  如果存在返回true,否则返回false
     */
    public boolean hasUsr(String usr)   {
        if(usr.isEmpty())   {
            return false;
        }

        return dbm.hasUsr(usr);
    }

    /**
     * 添加用户
     * @param usr   待添加用户名
     * @param pwd   待添加用户密码
     * @return  添加成功返回true, 否则返回false
     */
    public boolean addUsr(String usr, String pwd)   {
        return dbm.addUsr(usr, pwd);
    }

    /**
     * 检查用户
     * @param usr   待检查用户名
     * @param pwd   待检查用户密码
     * @return  如果符合返回true, 否则返回false
     */
    public boolean checkUsr(String usr, String pwd) {
        return dbm.checkUsr(usr, pwd);
    }
}
