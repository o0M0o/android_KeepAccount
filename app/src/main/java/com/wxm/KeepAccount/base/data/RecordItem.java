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
@DatabaseTable(tableName = "tb_KeepAccount")
public class RecordItem implements Parcelable {
    @DatabaseField(generatedId = true, columnName = "_id", dataType = DataType.INTEGER)
    private int _id;

    @DatabaseField(columnName = "record_type", dataType = DataType.STRING)
    private String record_type;

    @DatabaseField(columnName = "record_info", dataType = DataType.STRING)
    private String record_info;

    @DatabaseField(columnName = "record_note", dataType = DataType.STRING)
    private String record_note;

    @DatabaseField(columnName = "record_val", dataType = DataType.BIG_DECIMAL)
    private BigDecimal record_val;

    @DatabaseField(columnName = "record_ts", dataType = DataType.TIME_STAMP)
    private Timestamp record_ts;

    public RecordItem()
    {
        setRecord_ts(new Timestamp(0));
        setRecord_val(BigDecimal.ZERO);
        setRecord_type("");
        setRecord_info("");
        setRecord_note("");
    }

    @Override
    public String toString()
    {
        return String.format(Locale.CHINA
                            ,"type : %s, info : %s, val : %f, timestamp : %s\nnote : %s"
                            , getRecord_type(), getRecord_info(), getRecord_val(),
                            getRecord_ts().toString(), getRecord_note());
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(get_id());
        out.writeString(getRecord_type());
        out.writeString(getRecord_info());
        out.writeString(getRecord_note());
        out.writeString(getRecord_val().toString());
        out.writeString(getRecord_ts().toString());

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
        set_id(in.readInt());
        setRecord_type(in.readString());
        setRecord_info(in.readString());
        setRecord_note(in.readString());
        setRecord_val(new BigDecimal(in.readString()));

        try {
            setRecord_ts(new Timestamp(0));

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            Date date = format.parse(in.readString());
            getRecord_ts().setTime(date.getTime());
        }
        catch (ParseException ex)
        {
            setRecord_ts(new Timestamp(0));
        }
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getRecord_type() {
        return record_type;
    }

    public void setRecord_type(String record_type) {
        this.record_type = record_type;
    }

    public String getRecord_info() {
        return record_info;
    }

    public void setRecord_info(String record_info) {
        this.record_info = record_info;
    }

    public String getRecord_note() {
        return record_note;
    }

    public void setRecord_note(String record_note) {
        this.record_note = record_note;
    }

    public BigDecimal getRecord_val() {
        return record_val;
    }

    public void setRecord_val(BigDecimal record_val) {
        this.record_val = record_val;
    }

    public Timestamp getRecord_ts() {
        return record_ts;
    }

    public void setRecord_ts(Timestamp record_ts) {
        this.record_ts = record_ts;
    }
}
