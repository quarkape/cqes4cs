package club.hue.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

// 转换时间格式

public class DateTransition {

    public static String TimeStamp2StringSimple() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    public static String DateMills2String(Long dateMills) {
        Date date = new Date(dateMills);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
}
