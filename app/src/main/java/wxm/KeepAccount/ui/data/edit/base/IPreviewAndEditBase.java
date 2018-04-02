package wxm.KeepAccount.ui.data.edit.base;

/**
 * interface for preview & edit record
 * Created by WangXM on 2016/10/30.
 */
public interface IPreviewAndEditBase {
    /**
     * set current record
     * @param type   can be --
     *               * “pay”(GlobalDef.STR_RECORD_PAY)
     *               * “income”(GlobalDef.STR_RECORD_INCOME)
     *               * “budget”(GlobalDef.STR_RECORD_BUDGET)
     * @param action can be --
     *               * “modify”(GlobalDef.STR_MODIFY)
     *               * “create”(GlobalDef.STR_CREATE)
     * @param obj    if action is 'modify' then is record
     */
    void setCurData(String type, String action, Object obj);

    /**
     * invoke when accept record
     * @return  true if success
     */
    boolean onAccept();


    /**
     * switch to preview page
     */
    void toPreviewPage();


    /**
     * switch to edit page
     */
    void toEditPage();


    /**
     * check whether current page is edit
     * @return      true if current page is edit
     */
    boolean isEditPage();

    /**
     * check whether current page is preview
     * @return      true if current page is preview
     */
    boolean isPreviewPage();
}
