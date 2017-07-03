package wxm.KeepAccount.ui.data.edit.base;

/**
 * TF基类接口
 * Created by 123 on 2016/10/30.
 */
public interface IPreviewAndEditBase {
    /**
     * 设置运行数据
     *
     * @param type   可以为以下参数 --
     *               * “支出”(GlobalDef.STR_RECORD_PAY)
     *               * “收入”(GlobalDef.STR_RECORD_INCOME)
     *               * “预算”(GlobalDef.STR_RECORD_BUDGET)
     * @param action 可以为以下参数 --
     *               * “更新”(GlobalDef.STR_MODIFY)
     *               * “新建”(GlobalDef.STR_CREATE)
     * @param obj    若是“更新”，则此参数为待更新数据
     */
    void setCurData(String type, String action, Object obj);

    /**
     * 接受数据时调用
     * @return 成功返回true
     */
    boolean onAccept();


    /**
     * 切换至预览页
     */
    void toPreviewPage();


    /**
     * 切换至编辑页
     * @return 切换成功返回true
     */
    boolean toEditPage();


    /**
     * 当前页是否是编辑页
     *
     * @return 若是编辑页，返回true
     */
    boolean isEditPage();

    /**
     * 当前页是否是预览页
     *
     * @return 若是预览页，返回true
     */
    boolean isPreviewPage();
}
