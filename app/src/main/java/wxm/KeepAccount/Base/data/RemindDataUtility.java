package wxm.KeepAccount.Base.data;

import java.sql.SQLException;
import java.util.List;

import wxm.KeepAccount.Base.db.RemindItem;
import wxm.KeepAccount.Base.db.UsrItem;

/**
 * 提醒数据辅助类
 * Created by 123 on 2016/10/9.
 */
public class RemindDataUtility {
    public RemindDataUtility()
    {}

    /**
     * 返回当前用户查找RemindItem数据
     * @return 返回数据,无数据返回{@code NULL}
     */
    public List<RemindItem> GetAllRemind()  {
        UsrItem cur_usr = AppModel.getInstance().getCurUsr();
        if(null == cur_usr)
            return null;

        return AppModel.getDBHelper().getRemindREDao()
                .queryForEq(RemindItem.FIELD_USR, cur_usr.getId());
    }

    /**
     * 检查新提醒名字是否重名
     * @param name 待检查新名字
     * @return  如果重名返回true, 否则返回false
     */
    public boolean CheckRemindName(String name)    {
        UsrItem cur_usr = AppModel.getInstance().getCurUsr();
        if(null == cur_usr)
            return true;

        boolean r;
        try {
            List<RemindItem> ret = AppModel.getDBHelper().getRemindREDao().queryBuilder()
                                    .where().eq(RemindItem.FIELD_USR, cur_usr.getId())
                                        .and().eq(RemindItem.FIELD_NAME, name).query();
            r = 0 < ret.size();
        } catch (SQLException e) {
            e.printStackTrace();
            r = true;
        }

        return  r;
    }

    /**
     * 添加/更新提醒数据
     * @param ri  提醒数据（可以是新数据也可以是更新后的现有数据）
     * @return 操作成功返回true, 否则返回false
     */
    public boolean AddOrUpdateRemind(RemindItem ri)     {
        if(null == ri.getUsr()) {
            UsrItem cur_usr = AppModel.getInstance().getCurUsr();
            if(null == cur_usr)
                return false;

            ri.setUsr(cur_usr);
        }

        boolean r;
        if(AppGobalDef.INVALID_ID == ri.get_id())
            r = 1 == AppModel.getDBHelper().getRemindREDao().create(ri);
        else
            r = 1 == AppModel.getDBHelper().getRemindREDao().update(ri);

        return r;
    }
}
