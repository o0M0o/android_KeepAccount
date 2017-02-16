package wxm.KeepAccount.Base.utility;

import android.content.res.Resources;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import wxm.KeepAccount.Base.data.INote;
import wxm.KeepAccount.R;

/**
 * notes数据转换至html字符串
 * Created by User on 2017/2/14.
 */
public class NotesToHtmlUtil {
    private static final String ENCODING            = "UTF-8";
    private static final String DAY_REPORT_MODE_FN  = "report_day_md.html";
    private static final String TAG_COLUMNS         = "###columns###";
    private static final String TAG_ROWS            = "###rows###";
    private static final String TAG_CAPTION         = "###caption###";

    private static final String TABLE_COLUMNS =
            "<th>日期</th>\n<th>类型</th>\n<th>原因</th>\n<th>金额</th>";

    /**
     * 填充(4空格字符串)
     */
    private final static String STR_PAD = "    ";

    /**
     * table cell开始和结束标签
     */
    private final static String TB_CELL_HEAD = "<td>";
    private final static String TB_CELL_TAIL = "</td>";

    /**
     * 根据数据生成html字符串（供展示）
     * @param caption   表名
     * @param ls_data   数据
     * @return          html字符串
     */
    public static String NotesToHtmlStr(String caption, List<INote> ls_data) {
        String sz_org = ToolUtil.getFromAssets(DAY_REPORT_MODE_FN, ENCODING);
        return sz_org.replace(TAG_CAPTION, caption).replace(TAG_COLUMNS, TABLE_COLUMNS)
                    .replace(TAG_ROWS, NotesToRowStr(ls_data));
    }

    /**
     * 根据数据生成table字符串（供展示）
     * @param ls_data   数据
     * @return          html字符串
     */
    private static String NotesToRowStr(List<INote> ls_data) {
        Resources res = ContextUtil.getInstance().getResources();
        String nt_pay = res.getString(R.string.cn_pay);
        String nt_income = res.getString(R.string.cn_income);
        SimpleDateFormat sd_format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String two_pad = STR_PAD + STR_PAD;

        StringBuilder sb = new StringBuilder();
        for (INote id : ls_data) {
            String sz_tr = id.isPayNote() ?  STR_PAD + "<tr bgcolor=\"f8aba6\";>"
                                : STR_PAD + "<tr bgcolor=\"84bf96\";>";

            AppendLine(sb, sz_tr);
            AppendTableRow(sb, two_pad, sd_format.format(id.getTs().getTime()));
            AppendTableRow(sb, two_pad, id.isPayNote() ? nt_pay : nt_income);
            AppendTableRow(sb, two_pad, id.getInfo());
            AppendTableRow(sb, two_pad,
                    String.format(Locale.CHINA, "%.02f", id.getVal().floatValue()));
            AppendLine(sb, STR_PAD + "</tr>");
        }

        return sb.toString();
    }

    /**
     * 字符串构造器添加一行
     * @param sb    字符串构造器
     * @param ln    行内容
     */
    private static void AppendLine(StringBuilder sb, String ln)     {
        sb.append(ln).append("\n");
    }

    /**
     * 字符串构造器添加一行表格
     * @param sb        字符串构造器
     * @param pad       表格行前填充内容
     * @param row       表格行
     */
    private static void AppendTableRow(StringBuilder sb, String pad, String row)    {
        String col_amount = String.format(Locale.CHINA, "%s%s%s%s",
                                pad, TB_CELL_HEAD, row, TB_CELL_TAIL);
        AppendLine(sb, col_amount);
    }
}