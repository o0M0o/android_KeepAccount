package com.wxm.KeepAccount.Base.data;

import android.os.Parcel;
import android.os.Parcelable;

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
public class RecordItem implements Parcelable {
    public int _id;
    public String record_type;
    public String record_info;
    public String record_note;
    public BigDecimal record_val;
    public Timestamp record_ts;

    public RecordItem()
    {
        record_ts = new Timestamp(0);
        record_val = BigDecimal.ZERO;
        record_type = "";
        record_info = "";
        record_note = "";
    }

    @Override
    public String toString()
    {
        return String.format("type : %s, info : %s, val : %f, timestamp : %s\nnote : %s",
                            record_type, record_info, record_val,
                            record_ts.toString(), record_note);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(_id);
        out.writeString(record_type);
        out.writeString(record_info);
        out.writeString(record_note);
        out.writeString(record_val.toString());
        out.writeString(record_ts.toString());

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
        _id = in.readInt();
        record_type = in.readString();
        record_info = in.readString();
        record_note = in.readString();
        record_val = new BigDecimal(in.readString());

        try {
            record_ts = new Timestamp(0);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            Date date = format.parse(in.readString());
            record_ts.setTime(date.getTime());
        }
        catch (ParseException ex)
        {
            record_ts = new Timestamp(0);
        }
    }
}
