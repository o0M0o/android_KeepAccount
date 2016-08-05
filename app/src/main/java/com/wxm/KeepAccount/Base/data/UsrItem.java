package com.wxm.KeepAccount.Base.data;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 用户数据
 * Created by 123 on 2016/8/5.
 */
@DatabaseTable(tableName = "tb_Usr")
public class UsrItem {
    @DatabaseField(id = true, columnName = "usr_name", dataType = DataType.STRING)
    private String usr_name;

    @DatabaseField(columnName = "usr_pwd", dataType = DataType.STRING)
    private String usr_pwd;

    public UsrItem()    {
        setUsr_name("");
        setUsr_pwd("");
    }

    public String getUsr_name() {
        return usr_name;
    }

    public void setUsr_name(String usr_name) {
        this.usr_name = usr_name;
    }

    public String getUsr_pwd() {
        return usr_pwd;
    }

    public void setUsr_pwd(String usr_pwd) {
        this.usr_pwd = usr_pwd;
    }
}
