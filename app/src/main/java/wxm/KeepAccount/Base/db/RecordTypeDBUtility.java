package wxm.KeepAccount.Base.db;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.List;

import cn.wxm.andriodutillib.DBHelper.DBUtilityBase;
import wxm.KeepAccount.Base.data.RecordTypeItem;
import wxm.KeepAccount.Base.utility.ContextUtil;

/**
 * for record type data
 * Created by ookoo on 2016/12/2.
 */
public class RecordTypeDBUtility extends DBUtilityBase<RecordTypeItem, Integer> {

    public RecordTypeDBUtility() {
        super();
    }

    /**
     * 得到所有支付类类型数据
     * @return 结果数据
     */
    public List<RecordTypeItem> getAllPayItem() {
        return getDBHelper()
                .queryForEq(RecordTypeItem.FIELD_ITEMTYPE, RecordTypeItem.DEF_PAY);
    }


    /**
     * 得到所有收入类类型数据
     * @return 结果数据
     */
    public List<RecordTypeItem> getAllIncomeItem() {
        return getDBHelper()
                .queryForEq(RecordTypeItem.FIELD_ITEMTYPE, RecordTypeItem.DEF_INCOME);
    }


    @Override
    protected RuntimeExceptionDao<RecordTypeItem, Integer> getDBHelper() {
        return ContextUtil.getDBHelper().getRTItemREDao();
    }
}
