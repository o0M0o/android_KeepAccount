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

import wxm.androidutil.DBHelper.IDBRow;


/**
 * income record
 * Created by 123 on 2016/5/3.
 */
@DatabaseTable(tableName = "tbIncomeNote")
public class IncomeNoteItem
        implements INote, IDBRow<Integer> {
    public final static String FIELD_TS = "ts";
    public final static String FIELD_USR = "usr_id";
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
    private String valStr;
    private String tsStr;

    public IncomeNoteItem() {
        setTs(new Timestamp(0));
        setVal(BigDecimal.ZERO);
        setInfo("");
        setNote("");
    }

    @Override
    public boolean isPayNote() {
        return false;
    }

    @Override
    public boolean isIncomeNote() {
        return true;
    }

    @Override
    public PayNoteItem toPayNote() {
        return null;
    }

    @Override
    public IncomeNoteItem toIncomeNote() {
        return this;
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
    public String getValToStr() {
        return valStr;
    }

    @Override
    public BigDecimal getVal() {
        return val;
    }

    @Override
    public void setVal(BigDecimal val) {
        this.val = val;
        valStr = String.format(Locale.CHINA, "%.02f", val);
    }

    @Override
    public Timestamp getTs() {
        return ts;
    }

    @Override
    public void setTs(Timestamp tsval) {
        this.ts = tsval;
        tsStr = ts.toString();
    }

    @Override
    public String getTsToStr()    {
        return tsStr;
    }

    @Override
    public UsrItem getUsr() {
        return usr;
    }

    @Override
    public void setUsr(UsrItem usr) {
        this.usr = usr;
    }

    @Override
    public BudgetItem getBudget() {
        return null;
    }

    @Override
    public void setBudget(BudgetItem budget) {
        throw new AssertionError("NOT SUPPORT");
    }

    @Override
    public String toString() {
        return String.format(Locale.CHINA
                , "info : %s, val : %f, timestamp : %s\nnote : %s"
                , getInfo(), getVal(), getTs().toString(), getNote());
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

