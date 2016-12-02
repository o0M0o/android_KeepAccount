package wxm.KeepAccount.Base.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import cn.wxm.andriodutillib.DBHelper.IDBRow;

/**
 * 记录类型数据
 * Created by 123 on 2016/8/8.
 */
@DatabaseTable(tableName = "tbRecordType")
public class RecordTypeItem
        implements Parcelable, IDBRow<Integer> {
    public final static String FIELD_ITEMTYPE = "itemType";

    public final static String DEF_INCOME   = "income";
    public final static String DEF_PAY      = "pay";

    @DatabaseField(generatedId = true, columnName = "_id", dataType = DataType.INTEGER)
    private int _id;

    @DatabaseField(columnName = "itemType", canBeNull = false, dataType = DataType.STRING)
    private String itemType;

    @DatabaseField(columnName = "type", canBeNull = false, dataType = DataType.STRING)
    private String type;

    @DatabaseField(columnName = "note", dataType = DataType.STRING)
    private String note;


    public RecordTypeItem() {
        set_id(-1);
        setItemType("");
        setType("");
        setNote("");
    }


    public static final Creator<RecordTypeItem> CREATOR = new Creator<RecordTypeItem>() {
        @Override
        public RecordTypeItem createFromParcel(Parcel in) {
            return new RecordTypeItem(in);
        }

        @Override
        public RecordTypeItem[] newArray(int size) {
            return new RecordTypeItem[size];
        }
    };

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    /**
     * 记录类型大类({@link #DEF_INCOME} or {@link #DEF_PAY})
     */
    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    /**
     * 记录分类
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * 辅助注释信息
     */
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(get_id());
        out.writeString(getItemType());
        out.writeString(getType());
        out.writeString(getNote());
    }

    private RecordTypeItem(Parcel in)   {
        set_id(in.readInt());
        setItemType(in.readString());
        setType(in.readString());
        setNote(in.readString());
    }

    @Override
    public Integer getID() {
        return get_id();
    }

    @Override
    public void setID(Integer integer) {
        set_id(integer);
    }
}
