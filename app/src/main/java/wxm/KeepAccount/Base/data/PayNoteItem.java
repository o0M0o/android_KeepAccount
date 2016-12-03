package wxm.KeepAccount.Base.data;

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
import wxm.KeepAccount.Base.define.GlobalDef;
import wxm.KeepAccount.Base.utility.ContextUtil;

/**
 * 支出记录数据
 * Created by 123 on 2016/5/3.
 */
@DatabaseTable(tableName = "tbPayNote")
public class PayNoteItem
        implements Parcelable, INote, IDBRow<Integer> {
    public final static String FIELD_TS         = "ts";
    public final static String FIELD_USR        = "usr_id";
    public final static String FIELD_BUDGET     = "budget_id";

    @DatabaseField(generatedId = true, columnName = "_id", dataType = DataType.INTEGER)
    private int _id;

    @DatabaseField(columnName = "usr_id", foreign = true, foreignColumnName = UsrItem.FIELD_ID,
            canBeNull = false)
    private UsrItem usr;

    @DatabaseField(columnName = "budget_id", foreign = true, foreignColumnName = BudgetItem.FIELD_ID)
    private BudgetItem budget;

    @DatabaseField(columnName = "info", canBeNull = false, dataType = DataType.STRING)
    private String info;

    @DatabaseField(columnName = "note", dataType = DataType.STRING)
    private String note;

    @DatabaseField(columnName = "val", dataType = DataType.BIG_DECIMAL)
    private BigDecimal val;

    @DatabaseField(columnName = "ts", dataType = DataType.TIME_STAMP)
    private Timestamp ts;

    @Override
    public boolean isPayNote() {
        return true;
    }

    @Override
    public boolean isIncomeNote() {
        return false;
    }

    @Override
    public PayNoteItem toPayNote() {
        return this;
    }

    @Override
    public IncomeNoteItem toIncomeNote() {
        return null;
    }

    @Override
    public int getId() {
        return _id;
    }

    @Override
    public void setId(int _id) {
        this._id = _id;
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String getNote() {
        return note;
    }

    @Override
    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public BigDecimal getVal() {
        return val;
    }

    @Override
    public void setVal(BigDecimal val) {
        this.val = val;
    }

    @Override
    public Timestamp getTs() {
        return ts;
    }

    @Override
    public void setTs(Timestamp tsval) {
        this.ts = tsval;
    }

    @Override
    public UsrItem getUsr() {
        return usr;
    }

    @Override
    public void setUsr(UsrItem usr) {
        this.usr = usr;
    }


    public PayNoteItem()
    {
        setTs(new Timestamp(0));
        setVal(BigDecimal.ZERO);
        setInfo("");
        setNote("");

        setId(GlobalDef.INVALID_ID);
    }

    @Override
    public String toString()
    {
        return String.format(Locale.CHINA
                ,"info : %s, val : %f, timestamp : %s\nnote : %s"
                ,getInfo() ,getVal() ,getTs().toString() ,getNote());
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(getId());

        out.writeInt(null == getUsr() ? GlobalDef.INVALID_ID : getUsr().getId());
        out.writeInt(null == getBudget() ? GlobalDef.INVALID_ID : getBudget().get_id());

        out.writeString(getInfo());
        out.writeString(getNote());
        out.writeString(getVal().toString());
        out.writeString(getTs().toString());
    }

    public static final Parcelable.Creator<PayNoteItem> CREATOR
            = new Parcelable.Creator<PayNoteItem>() {
        public PayNoteItem createFromParcel(Parcel in) {
            return new PayNoteItem(in);
        }

        public PayNoteItem[] newArray(int size) {
            return new PayNoteItem[size];
        }
    };

    private PayNoteItem(Parcel in)   {
        setId(in.readInt());

        int uid = in.readInt();
        if(GlobalDef.INVALID_ID != uid)   {
            setUsr(ContextUtil.getUsrUtility().getData(uid));
        }

        int bid = in.readInt();
        if(GlobalDef.INVALID_ID != bid)   {
            setBudget(ContextUtil.getBudgetUtility().getData(bid));
        }

        setInfo(in.readString());
        setNote(in.readString());
        setVal(new BigDecimal(in.readString()));

        try {
            setTs(new Timestamp(0));

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            Date date = format.parse(in.readString());
            getTs().setTime(date.getTime());
        }
        catch (ParseException ex)
        {
            setTs(new Timestamp(0));
        }
    }

    @Override
    public BudgetItem getBudget() {
        return budget;
    }

    @Override
    public void setBudget(BudgetItem bi) {
        this.budget = bi;
    }

    @Override
    public Integer getID() {
        return getId();
    }

    @Override
    public void setID(Integer integer) {
        setId(integer);
    }
}

