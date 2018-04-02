package wxm.KeepAccount.db;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.greenrobot.eventbus.EventBus;

import java.sql.SQLException;
import java.util.List;

import wxm.androidutil.DBHelper.DBUtilityBase;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.define.RemindItem;
import wxm.KeepAccount.define.UsrItem;
import wxm.KeepAccount.utility.ContextUtil;

/**
 * 提醒数据辅助类
 * Created by WangXM on 2016/10/9.
 */
public class RemindDBUtility extends DBUtilityBase<RemindItem, Integer> {
    public RemindDBUtility() {
        super();
    }

    @Override
    protected RuntimeExceptionDao<RemindItem, Integer> getDBHelper() {
        return ContextUtil.getDBHelper().getRemindREDao();
    }

    /**
     * 返回当前用户查找RemindItem数据
     *
     * @return 返回数据, 无数据返回{@code NULL}
     */
    public List<RemindItem> getAllRemind() {
        UsrItem cur_usr = ContextUtil.getCurUsr();
        if (null == cur_usr)
            return null;

        return getDBHelper().queryForEq(RemindItem.FIELD_USR, cur_usr.getId());
    }

    /**
     * 检查新提醒名字是否重名
     *
     * @param name 待检查新名字
     * @return 如果重名返回true, 否则返回false
     */
    public boolean CheckRemindName(String name) {
        UsrItem cur_usr = ContextUtil.getCurUsr();
        if (null == cur_usr)
            return true;

        boolean r;
        try {
            List<RemindItem> ret = getDBHelper().queryBuilder()
                    .where().eq(RemindItem.FIELD_USR, cur_usr.getId())
                    .and().eq(RemindItem.FIELD_NAME, name).query();
            r = 0 < ret.size();
        } catch (SQLException e) {
            e.printStackTrace();
            r = true;
        }

        return r;
    }

    /**
     * 添加/更新提醒数据
     *
     * @param ri 提醒数据（可以是新数据也可以是更新后的现有数据）
     * @return 操作成功返回true, 否则返回false
     */
    public boolean AddOrUpdateRemind(RemindItem ri) {
        if (null == ri.getUsr()) {
            UsrItem cur_usr = ContextUtil.getCurUsr();
            if (null == cur_usr)
                return false;

            ri.setUsr(cur_usr);
        }

        boolean r;
        if (GlobalDef.INVALID_ID == ri.get_id())
            r = 1 == getDBHelper().create(ri);
        else
            r = 1 == getDBHelper().update(ri);

        return r;
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
