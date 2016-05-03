package com.wxm.keepaccount;

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
}
