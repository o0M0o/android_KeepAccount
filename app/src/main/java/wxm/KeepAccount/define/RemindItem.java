package wxm.KeepAccount.define;

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

import cn.wxm.andriodutillib.DBHelper.IDBRow;
import wxm.KeepAccount.utility.ContextUtil;

/**
 * 提醒数据
 * Created by 123 on 2016/10/9.
 */
@DatabaseTable(tableName = "tbRemind")
public class RemindItem
        implements Parcelable, IDBRow<Integer> {
    // for type
    public final static String REMIND_BUDGET   = "预算预警";
    public final static String REMIND_PAY      = "支出预警";
    public final static String REMIND_INCOME   = "收入预警";

    // for reason
    public final static String RAT_AMOUNT_BELOW    = "金额低于预设值预警";
    public final static String RAT_AMOUNT_EXCEED   = "金额高于预设值预警";

    // for period
    public final static String PERIOD_WEEKLY    = "每周";
    public final static String PERIOD_MONTHLY   = "每月";
    public final static String PERIOD_YEARLY    = "每年";

    public final static String FIELD_USR        = "usr_id";
    public final static String FIELD_NAME       = "name";

    @DatabaseField(generatedId = true, columnName = "_id", dataType = DataType.INTEGER)
    private int _id;

    @DatabaseField(columnName = "usr_id", foreign = true, foreignColumnName = UsrItem.FIELD_ID,
            canBeNull = false)
    private UsrItem usr;

    @DatabaseField(columnName = "name", canBeNull = false, dataType = DataType.STRING)
    private String name;

    @DatabaseField(columnName = "type", canBeNull = false, dataType = DataType.STRING)
    private String type;

    @DatabaseField(columnName = "reason", canBeNull = false, dataType = DataType.STRING)
    private String reason;

    @DatabaseField(columnName = "period", canBeNull = false, dataType = DataType.STRING)
    private String period;

    @DatabaseField(columnName = "startDate", dataType = DataType.TIME_STAMP)
    private Timestamp startDate;

    @DatabaseField(columnName = "endDate", dataType = DataType.TIME_STAMP)
    private Timestamp endDate;

    @DatabaseField(columnName = "amount", dataType = DataType.BIG_DECIMAL)
    private BigDecimal amount;

    public RemindItem() {
        set_id(GlobalDef.INVALID_ID);
    }

    private RemindItem(Parcel in) {
        set_id(in.readInt());
        int uid = in.readInt();
        if(GlobalDef.INVALID_ID != uid)   {
            setUsr(ContextUtil.getUsrUtility().getData(uid));
        }

        setName(in.readString());
        setType(in.readString());
        setReason(in.readString());
        setPeriod(in.readString());

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            Date date = format.parse(in.readString());
            setStartDate(new Timestamp(0));
            getStartDate().setTime(date.getTime());

            date = format.parse(in.readString());
            setEndDate(new Timestamp(0));
            getEndDate().setTime(date.getTime());
        } catch (ParseException ex)     {
            setStartDate(new Timestamp(0));
            setEndDate(new Timestamp(0));
        }

        setAmount(new BigDecimal(in.readString()));
    }

    public static final Creator<RemindItem> CREATOR = new Creator<RemindItem>() {
        @Override
        public RemindItem createFromParcel(Parcel in) {
            return new RemindItem(in);
        }

        @Override
        public RemindItem[] newArray(int size) {
            return new RemindItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(get_id());
        dest.writeInt(null == getUsr() ? GlobalDef.INVALID_ID : getUsr().getId());
        dest.writeString(getName());
        dest.writeString(getType());
        dest.writeString(getReason());
        dest.writeString(getPeriod());
        dest.writeString(getStartDate().toString());
        dest.writeString(getEndDate().toString());
        dest.writeString(getAmount().toString());
    }

    public int get_id() {
        return _id;
    }

    protected void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public UsrItem getUsr() {
        return usr;
    }

    public void setUsr(UsrItem usr) {
        this.usr = usr;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
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
