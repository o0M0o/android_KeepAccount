package com.wxm.KeepAccount.Base.data;

import com.wxm.KeepAccount.Base.db.RecordTypeItem;

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

    /**
     * 添加一个新数据
     * @param ri    待添加数据
     * @return  如果添加成功返回true, 否则返回false
     */
    public boolean addItem(RecordTypeItem ri)   {
        return 1 == AppModel.getDBHelper().getRTItemREDao()
                .create(ri);
    }


    /**
     *   更新数据
     * @param ri    待修改数据
     * @return  如果修改成功返回true, 否则返回false
     */
    public boolean modifyItem(RecordTypeItem ri)    {
        return 1== AppModel.getDBHelper().getRTItemREDao()
                    .update(ri);
    }
}
