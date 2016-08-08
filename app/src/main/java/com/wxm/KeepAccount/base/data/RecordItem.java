package com.wxm.KeepAccount.Base.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 数据类
 * Created by 123 on 2016/5/3.
 */
@DatabaseTable(tableName = "tbRecord")
public class RecordItem implements Parcelable {
    public final static String FIELD_TS     = "ts";
    public final static String FIELD_TYPE   = "type";
    public final static String FIELD_USR    = "usr";


    @DatabaseField(generatedId = true, columnName = "_id", dataType = DataType.INTEGER)
    private int _id;

    @DatabaseField(columnName = "usr", foreign = true, foreignColumnName = UsrItem.FIELD_ID,
                    canBeNull = false)
    private UsrItem usr;

    @DatabaseField(columnName = "type", canBeNull = false, dataType = DataType.STRING)
    private String type;

    @DatabaseField(columnName = "info", canBeNull = false, dataType = DataType.STRING)
    private String info;

    @DatabaseField(columnName = "note", dataType = DataType.STRING)
    private String note;

    @DatabaseField(columnName = "val", dataType = DataType.BIG_DECIMAL)
    private BigDecimal val;

    @DatabaseField(columnName = "ts", dataType = DataType.TIME_STAMP)
    private Timestamp ts;

    public RecordItem()
    {
        setRecord_ts(new Timestamp(0));
        setVal(BigDecimal.ZERO);
        setType("");
        setInfo("");
        setNote("");
    }

    @Override
    public String toString()
    {
        return String.format(Locale.CHINA
                            ,"type : %s, info : %s, val : %f, timestamp : %s\nnote : %s"
                            , getType(), getInfo(), getVal(),
                            getTs().toString(), getNote());
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(getId());
        out.writeString(getType());
        out.writeString(getInfo());
        out.writeString(getNote());
        out.writeString(getVal().toString());
        out.writeString(getTs().toString());

    }

    public static final Parcelable.Creator<RecordItem> CREATOR
            = new Parcelable.Creator<RecordItem>() {
        public RecordItem createFromParcel(Parcel in) {
            return new RecordItem(in);
        }

        public RecordItem[] newArray(int size) {
            return new RecordItem[size];
        }
    };

    private RecordItem(Parcel in)   {
        setId(in.readInt());
        setType(in.readString());
        setInfo(in.readString());
        setNote(in.readString());
        setVal(new BigDecimal(in.readString()));

        try {
            setRecord_ts(new Timestamp(0));

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            Date date = format.parse(in.readString());
            getTs().setTime(date.getTime());
        }
        catch (ParseException ex)
        {
            setRecord_ts(new Timestamp(0));
        }
    }

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public BigDecimal getVal() {
        return val;
    }

    public void setVal(BigDecimal val) {
        this.val = val;
    }

    public Timestamp getTs() {
        return ts;
    }

    public void setRecord_ts(Timestamp tsval) {
        this.ts = tsval;
    }

    public UsrItem getUsr() {
        return usr;
    }

    public void setUsr(UsrItem usr) {
        this.usr = usr;
    }
}
