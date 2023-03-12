package club.hue.utils;

// 检测XSS，对字符进行转义

public class CheckXSSUtil {

    public static String checkXSS(String str) {
        str.replace("<", "&lt;");
        str.replace(">", "&gt;");
        str.replace("&", "&amp;");
        str.replace("\"", "&quot;");
        str.replace("'", "&#x27");
        str.replace("/", "&#x2F;");
        return str;
    }
}
