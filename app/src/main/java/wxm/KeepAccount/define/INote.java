package wxm.KeepAccount.define;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by 123 on 2016/10/21.
 * interface for note
 */
public interface INote {
    boolean isPayNote();

    boolean isIncomeNote();

    PayNoteItem toPayNote();

    IncomeNoteItem toIncomeNote();

    int getId();

    void setId(int _id);

    String getInfo();

    void setInfo(String info);

    String getNote();

    void setNote(String note);

    BigDecimal getVal();

    void setVal(BigDecimal val);

    String getValToStr();

    Timestamp getTs();

    void setTs(Timestamp tsval);

    UsrItem getUsr();

    void setUsr(UsrItem usr);

    BudgetItem getBudget();

    void setBudget(BudgetItem bi);
}
