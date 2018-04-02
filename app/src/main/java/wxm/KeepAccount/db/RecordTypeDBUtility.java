package wxm.KeepAccount.db;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import wxm.androidutil.DBHelper.DBUtilityBase;
import wxm.KeepAccount.define.RecordTypeItem;
import wxm.KeepAccount.utility.ContextUtil;

/**
 * for record type data
 * Created by WangXM on 2016/12/2.
 */
public class RecordTypeDBUtility extends DBUtilityBase<RecordTypeItem, Integer> {

    public RecordTypeDBUtility() {
        super();
    }

    /**
     * 得到所有支付类类型数据
     *
     * @return 结果数据
     */
    public List<RecordTypeItem> getAllPayItem() {
        return getDBHelper()
                .queryForEq(RecordTypeItem.FIELD_ITEMTYPE, RecordTypeItem.DEF_PAY);
    }


    /**
     * 得到所有收入类类型数据
     *
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

    @Override
    protected void onDataModify(List<Integer> md) {
        EventBus.getDefault().post(new DBDataChangeEvent());
    }

    @Override
    protected void onDataCreate(List<Integer> cd) {
        EventBus.getDefault().post(new DBDataChangeEvent());
    }

    @Override
    protected void onDataRemove(List<Integer> dd) {
        EventBus.getDefault().post(new DBDataChangeEvent());
    }
}
