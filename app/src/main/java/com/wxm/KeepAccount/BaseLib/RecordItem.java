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
    public BigDecimal record_val;
    public Timestamp record_ts;

    public RecordItem()
    {
        record_ts = new Timestamp(0);
        record_val = BigDecimal.ZERO;
        record_type = "";
        record_info = "";
    }

    @Override
    public String toString()
    {
        String ret = String.format("type : %s, info : %s, val : %f, timestamp : %s",
                            record_type, record_info, record_val, record_ts.toString());
        return ret;
    }
}
