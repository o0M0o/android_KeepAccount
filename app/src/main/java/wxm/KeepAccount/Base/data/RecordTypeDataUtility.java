package wxm.KeepAccount.Base.data;

import java.util.List;

import wxm.KeepAccount.Base.db.RecordTypeItem;

/**
 * recordtype数据辅助类
 * Created by 123 on 2016/8/9.
 */
public class RecordTypeDataUtility extends  DataUtilityBase {
    public RecordTypeDataUtility()  {
        super();
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
        boolean br = 1 == AppModel.getDBHelper().getRTItemREDao().create(ri);
        if(br)
            onDataCreate();

        return br;
    }


    /**
     * 更新数据
     * @param ri    待修改数据
     * @return  如果修改成功返回true, 否则返回false
     */
    public boolean modifyItem(RecordTypeItem ri)    {
        boolean br = 1== AppModel.getDBHelper().getRTItemREDao().update(ri);
        if(br)
            onDataModify();

        return br;
    }

    /**
     * 根据ID删除数据
     * @param ri_id  待删除数据
     * @return  如果删除成功返回ture, 否则返回false
     */
    public boolean removeItemById(int ri_id)    {
        boolean br = 1== AppModel.getDBHelper().getRTItemREDao().deleteById(ri_id);
        if(br)
            onDataDelete();

        return br;
    }
}
