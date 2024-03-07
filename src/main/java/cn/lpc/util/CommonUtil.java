package cn.lpc.util;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 通用工具类
 */
public class CommonUtil {


    public static final String PATTEN_DEFAULT_YMD = "yyyy-MM-dd";

    /**
     * 验证字符串是否为空
     * @return
     */
    public static boolean isEmpty(String str) {
        if(str == null || "".equals(str)) {
            return true; //为空
        }else {
            return false; //不为空
        }
    }

    /**
     * 判断后缀是否是图片文件的后缀
     * @param suffix
     * @return
     */
    public static boolean isPhoto(String suffix){
        if("jpg".toUpperCase().equals(suffix.toUpperCase())){
            return true;
        }else if("png".toUpperCase().equals(suffix.toUpperCase())){
            return true;
        }else if("gif".toUpperCase().equals(suffix.toUpperCase())){
            return true;
        }else if("jpeg".toUpperCase().equals(suffix.toUpperCase())){
            return true;
        }else{
            return false;
        }
    }
    /**
     * 返回指定格式的日期字符串
     * @param date
     * @param formatter
     * @return
     */
    public static String getFormatterDate(Date date, String formatter){
        SimpleDateFormat sdf = new SimpleDateFormat(formatter);
        return sdf.format(date);
    }

    /**
     * 获取展示时间
     * @param date
     * @return
     */
    public static String getShowDate(Date date) {
        // 当前时间
        Date now = new Date();
        SimpleDateFormat sf = new SimpleDateFormat(PATTEN_DEFAULT_YMD);
        // 获取今天的日期
        String nowDay = sf.format(now);
        // 获取昨天的日期
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(now);
        calendar.add(calendar.DATE,-1);
        SimpleDateFormat sdf = new SimpleDateFormat(PATTEN_DEFAULT_YMD);
        String yesterday = sdf.format(calendar.getTime());
        //对比的时间
        String day = sf.format(date);
        if(nowDay.equals(day)) {
            return "今天";
        } else if (yesterday.equals(day)) {
            return "昨天";
        }
        return day;
    }

    /**
     * 文件大小转换
     * @param size
     * @return
     */
    public static String convertFileSize(Long size) {
        Double KB = new BigDecimal(size * 1.00 / 1024).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        Double MB = new BigDecimal( (size * 1.00) / (1024 * 1024)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        Double GB = new BigDecimal( (size * 1.00) / (1024 * 1024 * 1024)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        if((size / 1024) == 0) {
            return size + "B";
        } else if ((size / (1024 * 1024)) == 0) {
            return KB + "KB";
        } else if ((size / (1024 * 1024 * 1024)) == 0) {
            return MB + "MB";
        } else {
            return GB + "GB";
        }
    }


}