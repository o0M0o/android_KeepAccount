package com.wxm.KeepAccount.BaseLib;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by 123 on 2016/5/3.
 */
public class RecordItem {
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
        String ret = String.format("type : %s, info : %s, val : %f, timestamp : %s\nnote : %s",
                            record_type, record_info, record_val,
                            record_ts.toString(), record_note);
        return ret;
    }
}
