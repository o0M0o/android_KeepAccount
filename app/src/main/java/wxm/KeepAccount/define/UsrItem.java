package wxm.KeepAccount.define;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import wxm.androidutil.DBHelper.IDBRow;

/**
 * usr class
 * Created by WangXM on 2016/8/5.
 */
@DatabaseTable(tableName = "tbUsr")
public class UsrItem
        implements IDBRow<Integer> {
    public final static String FIELD_ID = "_id";
    public final static String FIELD_NAME = "name";
    public final static String FIELD_PWD = "pwd";

    @DatabaseField(generatedId = true, columnName = "_id", dataType = DataType.INTEGER)
    private int _id;

    @DatabaseField(columnName = "name", unique = true, dataType = DataType.STRING)
    private String name;

    @DatabaseField(columnName = "pwd", dataType = DataType.STRING)
    private String pwd;

    public UsrItem() {
        setName("");
        setPwd("");
        setId(-1);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    @Override
    public Integer getID() {
        return getId();
    }

    @Override
    public void setID(Integer integer) {
        setId(integer);
    }
}
