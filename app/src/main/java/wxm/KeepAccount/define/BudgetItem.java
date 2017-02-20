package wxm.KeepAccount.define;

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
import java.util.Locale;

import cn.wxm.andriodutillib.DBHelper.IDBRow;
import cn.wxm.andriodutillib.util.UtilFun;

/**
 * 预算类
 * Created by 123 on 2016/9/1.
 */
@DatabaseTable(tableName = "tbBudget")
public class BudgetItem
        implements Parcelable, IDBRow<Integer> {
    private static final String TAG = "BudgetItem";

    public final static String FIELD_USR        = "usr_id";
    public final static String FIELD_NAME       = "name";
    public final static String FIELD_ID         = "_id";

    @DatabaseField(generatedId = true, columnName = "_id", dataType = DataType.INTEGER)
    private int _id;

    @DatabaseField(columnName = "name", canBeNull = false, dataType = DataType.STRING)
    private String name;

    @DatabaseField(columnName = "usr_id", foreign = true, foreignColumnName = UsrItem.FIELD_ID,
            canBeNull = false)
    private UsrItem usr;

    @DatabaseField(columnName = "amount", dataType = DataType.BIG_DECIMAL, canBeNull = false)
    private BigDecimal amount;

    @DatabaseField(columnName = "remainder_amount", dataType = DataType.BIG_DECIMAL,
            canBeNull = false)
    private BigDecimal remainder_amount;

    @DatabaseField(columnName = "note", dataType = DataType.STRING)
    private String note;

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

    public void setAmount(BigDecimal new_amount)
    {
        if(null != amount) {
            if (!amount.equals(new_amount)) {
                if(null != remainder_amount) {
                    remainder_amount = remainder_amount.add(new_amount.subtract(amount));
                } else  {
                    remainder_amount = new_amount;
                }

                amount = new_amount;
            }
        } else  {
            amount = new_amount;
        }
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Timestamp getTs() {
        return ts;
    }

    public void setTs(Timestamp ts) {
        this.ts = ts;
    }

    public UsrItem getUsr() {
        return usr;
    }

    public void setUsr(UsrItem usr) {
        this.usr = usr;
    }

    public BigDecimal getRemainderAmount() {
        return remainder_amount;
    }

    private void setRemainderAmount(BigDecimal remainder_amount) {
        this.remainder_amount = remainder_amount;
    }

    /**
     * 使用预算
     * @param use_val 使用预算金额,为总使用预算金额
     */
    public void useBudget(BigDecimal use_val)   {
        remainder_amount = amount.subtract(use_val);
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
        dest.writeString(getRemainderAmount().toString());
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
        setRemainderAmount(BigDecimal.ZERO);
        setTs(new Timestamp(System.currentTimeMillis()));
    }

    private BudgetItem(Parcel in)   {
        set_id(in.readInt());

        setUsr(new UsrItem());
        getUsr().setId(in.readInt());

        setName(in.readString());
        setNote(in.readString());
        setAmount(new BigDecimal(in.readString()));
        setRemainderAmount(new BigDecimal(in.readString()));

        try {
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

    @Override
    public int hashCode()   {
        return getName().hashCode() + getAmount().hashCode() + get_id();
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
