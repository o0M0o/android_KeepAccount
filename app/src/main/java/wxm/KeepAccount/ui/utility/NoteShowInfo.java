package wxm.KeepAccount.ui.utility;

import java.math.BigDecimal;

/**
 * 数据显示内容定义类
 * Created by 123 on 2016/10/24.
 */
public class NoteShowInfo {
    private int     mPayCount;
    private int     mIncomeCount;
    private BigDecimal  mPayAmount;
    private BigDecimal  mIncomeAmount;

    public NoteShowInfo()   {
        mPayAmount = BigDecimal.ZERO;
        mIncomeAmount = BigDecimal.ZERO;

        mPayCount = 0;
        mIncomeCount = 0;
    }

    public int getPayCount() {
        return mPayCount;
    }

    public void setPayCount(int mPayCount) {
        this.mPayCount = mPayCount;
    }

    public int getIncomeCount() {
        return mIncomeCount;
    }

    public void setIncomeCount(int mIncomeCount) {
        this.mIncomeCount = mIncomeCount;
    }

    public BigDecimal getPayAmount() {
        return mPayAmount;
    }

    public void setPayAmount(BigDecimal mPayAmount) {
        this.mPayAmount = mPayAmount;
    }

    public BigDecimal getIncomeAmount() {
        return mIncomeAmount;
    }

    public void setIncomeAmount(BigDecimal mIncomeAmount) {
        this.mIncomeAmount = mIncomeAmount;
    }

    public BigDecimal getBalance()  {
        return mIncomeAmount.subtract(mPayAmount);
    }
}
