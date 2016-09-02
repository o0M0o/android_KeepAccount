package wxm.KeepAccount.Base.data;

import wxm.KeepAccount.Base.db.RecordTypeItem;

import java.util.List;

/**
 * recordtype数据辅助类
 * Created by 123 on 2016/8/9.
 */
public class RecordTypeItemUtility {
    public RecordTypeItemUtility()  {
    }


    /**
     * 得到所有支付类类型数据
     * @return 结果数据
     */
    public List<RecordTypeItem> getAllPayItem() {
        return AppModel.getDBHelper().getRTItemREDao()
                .queryForEq(RecordTypeItem.FIELD_ITEMTYPE, RecordTypeItem.DEF_PAY);
    }


    /**
     * 得到所有收入类类型数据
     * @return 结果数据
     */
    public List<RecordTypeItem> getAllIncomeItem() {
        return AppModel.getDBHelper().getRTItemREDao()
                .queryForEq(RecordTypeItem.FIELD_ITEMTYPE, RecordTypeItem.DEF_INCOME);
    }
}
