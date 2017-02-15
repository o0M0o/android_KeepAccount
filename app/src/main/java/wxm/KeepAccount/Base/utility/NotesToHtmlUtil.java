package wxm.KeepAccount.Base.utility;

import android.content.res.Resources;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import wxm.KeepAccount.Base.data.INote;
import wxm.KeepAccount.R;

import static android.R.attr.format;

/**
 * notes数据转换至html字符串
 * Created by User on 2017/2/14.
 */
public class NotesToHtmlUtil {
    /**
     * html头字符串
     */
    private final static String HTML_HEAD =
            "<html>\n" +
            "   <head>\n" +
            "       <meta charset=\"utf8\" />\n" +
            "       <title>for notes show</title>\n" +
            "   </head>\n" +
            "\n" +
            "   <body>";

    /**
     * html尾字符串
     */
    private final static String HTML_TAIL =
            "   </body>\n" +
            "</html>";

    /**
     * table头字符串
     */
    private final static String TABLE_HEAD =
            "       <table border=\"1\">\n" +
            "           <tr>\n" +
            "               <th>日期</th>\n" +
            "               <th>类型</th>\n" +
            "               <th>原因</th>\n" +
            "               <th>金额</th>\n" +
            "           </tr>";

    /**
     * table尾字符串
     */
    private final static String TABLE_TAIL = "</table>";

    /**
     * 填充4空格字符串
     */
    private final static String STR_PAD = "    ";


    private final static String TABLE_COL_HEAD = "<td>";
    private final static String TABLE_COL_TAIL = "</td>";

    /**
     * 根据数据生成html字符串（供展示）
     * @param ls_data   数据
     * @return          html字符串
     */
    public static String NotesToHtmlStr(List<INote> ls_data) {
        StringBuilder sb = new StringBuilder();
        AppendLine(sb, HTML_HEAD);
        AppendLine(sb, TABLE_HEAD);
        AppendLine(sb, NotesToTableStr(ls_data));
        AppendLine(sb, STR_PAD + STR_PAD + TABLE_TAIL);
        AppendLine(sb, HTML_TAIL);

        return sb.toString();
    }

    /**
     * 根据数据生成table字符串（供展示）
     * @param ls_data   数据
     * @return          html字符串
     */
    private static String NotesToTableStr(List<INote> ls_data)  {
        Resources res = ContextUtil.getInstance().getResources();
        String nt_pay = res.getString(R.string.cn_pay);
        String nt_income = res.getString(R.string.cn_income);
        SimpleDateFormat sd_format = new SimpleDateFormat( "yyyy-MM-dd", Locale.CHINA);
        String two_pad = STR_PAD + STR_PAD;

        StringBuilder sb = new StringBuilder();
        for(INote id : ls_data) {
            AppendLine(sb, STR_PAD + "<tr>");
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
                                pad, TABLE_COL_HEAD, row, TABLE_COL_TAIL);
        AppendLine(sb, col_amount);
    }
}
