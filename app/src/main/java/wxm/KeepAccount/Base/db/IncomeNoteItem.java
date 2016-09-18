package wxm.KeepAccount.Base.db;

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

import wxm.KeepAccount.Base.data.AppGobalDef;


/**
 * 支出记录数据
 * Created by 123 on 2016/5/3.
 */
@DatabaseTable(tableName = "tbIncomeNote")
public class IncomeNoteItem implements Parcelable {
    public final static String FIELD_TS         = "ts";
    public final static String FIELD_USR        = "usr_id";

    @DatabaseField(generatedId = true, columnName = "_id", dataType = DataType.INTEGER)
    private int _id;

    @DatabaseField(columnName = "usr_id", foreign = true, foreignColumnName = UsrItem.FIELD_ID,
            canBeNull = false)
    private UsrItem usr;

    @DatabaseField(columnName = "budget_id", foreign = true, foreignColumnName = BudgetItem.FIELD_ID,
            canBeNull = true)
    private BudgetItem budget;

    @DatabaseField(columnName = "info", canBeNull = false, dataType = DataType.STRING)
    private String info;

    @DatabaseField(columnName = "note", dataType = DataType.STRING)
    private String note;

    @DatabaseField(columnName = "val", dataType = DataType.BIG_DECIMAL)
    private BigDecimal val;

    @DatabaseField(columnName = "ts", dataType = DataType.TIME_STAMP)
    private Timestamp ts;

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
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

    public void setTs(Timestamp tsval) {
        this.ts = tsval;
    }

    public UsrItem getUsr() {
        return usr;
    }

    public void setUsr(UsrItem usr) {
        this.usr = usr;
    }


    public BudgetItem getBudget() {
        return budget;
    }

    public void setBudget(BudgetItem budget) {
        this.budget = budget;
    }


    public IncomeNoteItem()
    {
        setTs(new Timestamp(0));
        setVal(BigDecimal.ZERO);
        setInfo("");
        setNote("");
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

        int usrid = AppGobalDef.INVALID_ID;
        UsrItem ui = getUsr();
        if(null != ui)
            usrid = ui.getId();
        out.writeInt(usrid);

        int budgetid = AppGobalDef.INVALID_ID;
        BudgetItem bi = getBudget();
        if(null != bi)
            budgetid = bi.get_id();
        out.writeInt(budgetid);

        out.writeString(getInfo());
        out.writeString(getNote());
        out.writeString(getVal().toString());
        out.writeString(getTs().toString());
    }

    public static final Parcelable.Creator<IncomeNoteItem> CREATOR
            = new Parcelable.Creator<IncomeNoteItem>() {
        public IncomeNoteItem createFromParcel(Parcel in) {
            return new IncomeNoteItem(in);
        }

        public IncomeNoteItem[] newArray(int size) {
            return new IncomeNoteItem[size];
        }
    };

    private IncomeNoteItem(Parcel in)   {
        setId(in.readInt());

        setUsr(new UsrItem());
        getUsr().setId(in.readInt());

        setBudget(new BudgetItem());
        getBudget().set_id(in.readInt());

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
}

