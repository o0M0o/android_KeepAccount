package wxm.KeepAccount.Base.db;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.utility.ToolUtil;

/**
 * 预算类
 * Created by 123 on 2016/9/1.
 */
@DatabaseTable(tableName = "tbBudget")
public class BudgetItem implements Parcelable {
    private static final String TAG = "BudgetItem";
    private static final String NULL_ID = "NULL";

    public final static String FIELD_USR    = "usr";

    @DatabaseField(generatedId = true, columnName = "_id", dataType = DataType.INTEGER)
    private int _id;

    @DatabaseField(columnName = "name", canBeNull = false, dataType = DataType.STRING)
    private String name;

    @DatabaseField(columnName = "usr", foreign = true, foreignColumnName = UsrItem.FIELD_ID,
            canBeNull = false)
    private UsrItem usr;

    @DatabaseField(columnName = "amount", dataType = DataType.BIG_DECIMAL)
    private BigDecimal amount;

    @DatabaseField(columnName = "note", canBeNull = false, dataType = DataType.STRING)
    private String note;

    @DatabaseField(columnName = "start_date", dataType = DataType.DATE)
    private Date startDate;

    @DatabaseField(columnName = "end_date", dataType = DataType.DATE)
    private Date endDate;

    @DatabaseField(columnName = "ts", dataType = DataType.TIME_STAMP)
    private Timestamp ts;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Timestamp getTs() {
        return ts;
    }

    public void setTs(Timestamp ts) {
        this.ts = ts;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(get_id());

        int usrid = -1;
        UsrItem ui = getUsr();
        if(null != ui)
            usrid = ui.getId();
        dest.writeInt(usrid);

        dest.writeString(getName());
        dest.writeString(getNote());
        dest.writeString(getAmount().toString());
        dest.writeString(null == getStartDate() ?
                NULL_ID
                : ToolUtil.DateToSerializetr(getStartDate()));
        dest.writeString(null == getEndDate() ?
                NULL_ID
                : ToolUtil.DateToSerializetr(getEndDate()));
        dest.writeString(getTs().toString());
    }

    public static final Parcelable.Creator<BudgetItem> CREATOR
            = new Parcelable.Creator<BudgetItem>() {
        public BudgetItem createFromParcel(Parcel in) {
            return new BudgetItem(in);
        }

        public BudgetItem[] newArray(int size) {
            return new BudgetItem[size];
        }
    };

    public BudgetItem() {
        set_id(-1);
        setAmount(BigDecimal.ZERO);
        setTs(new Timestamp(System.currentTimeMillis()));
    }

    private BudgetItem(Parcel in)   {
        set_id(in.readInt());

        setUsr(new UsrItem());
        getUsr().setId(in.readInt());

        setName(in.readString());
        setNote(in.readString());
        setAmount(new BigDecimal(in.readString()));

        try {
            String sdt = in.readString();
            if(!sdt.equals(NULL_ID))    {
                setStartDate(ToolUtil.SerializeStrToDate(sdt));
            }

            String edt = in.readString();
            if(!edt.equals(NULL_ID))    {
                setEndDate(ToolUtil.SerializeStrToDate(edt));
            }

            setTs(new Timestamp(0));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            java.util.Date date = format.parse(in.readString());
            getTs().setTime(date.getTime());
        }
        catch (ParseException ex)
        {
            Log.e(TAG, "get budgetItem from parcel fall!, ex = " + UtilFun.ExceptionToString(ex));
        }
    }

    public UsrItem getUsr() {
        return usr;
    }

    public void setUsr(UsrItem usr) {
        this.usr = usr;
    }
}
