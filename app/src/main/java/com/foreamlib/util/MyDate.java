package com.foreamlib.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MyDate {
    /**
     * 准备第一个模板，从字符串中提取出日期数字
     */
    private static String pat1 = "yyyy-MM-dd HH:mm:ss";
    /**
     * 准备第二个模板，将提取后的日期数字变为指定的格式
     */
    private static String pat2 = "yyyy年MM月dd日 HH:mm:ss";
    private static String pat3 = "yyyyMMddHHmmss";
    /**
     * 实例化模板对象
     */
    private static SimpleDateFormat sdf1 = new SimpleDateFormat(pat1);
    private static SimpleDateFormat sdf2 = new SimpleDateFormat(pat2);
    private static SimpleDateFormat sdf3 = new SimpleDateFormat(pat3);
    private static String mYear;
    private static String mMonth;
    private static String mDay;
    private static String mWay;
    private static String[] mFourWay;

    public static String[] getNextFourDate() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        mFourWay = new String[4];
        if ("1".equals(mWay)) {
            mWay = "日";
            mFourWay[0] = "一";
            mFourWay[1] = "二";
            mFourWay[2] = "三";
            mFourWay[3] = "四";

        } else if ("2".equals(mWay)) {
            mWay = "一";
            mFourWay[0] = "二";
            mFourWay[1] = "三";
            mFourWay[2] = "四";
            mFourWay[3] = "五";
        } else if ("3".equals(mWay)) {
            mWay = "二";
            mFourWay[0] = "三";
            mFourWay[1] = "四";
            mFourWay[2] = "五";
            mFourWay[3] = "六";
        } else if ("4".equals(mWay)) {
            mWay = "三";
            mFourWay[0] = "四";
            mFourWay[1] = "五";
            mFourWay[2] = "六";
            mFourWay[3] = "日";
        } else if ("5".equals(mWay)) {
            mWay = "四";
            mFourWay[0] = "五";
            mFourWay[1] = "六";
            mFourWay[2] = "日";
            mFourWay[3] = "一";
        } else if ("6".equals(mWay)) {
            mWay = "五";
            mFourWay[0] = "六";
            mFourWay[1] = "日";
            mFourWay[2] = "一";
            mFourWay[3] = "二";
        } else if ("7".equals(mWay)) {
            mWay = "六";
            mFourWay[0] = "日";
            mFourWay[1] = "一";
            mFourWay[2] = "二";
            mFourWay[3] = "三";
        }
        return mFourWay;
    }

    public static String getFileName() {
        String time = getCurTime();
        return time + ((int) (Math.random() * 900) + 100);
    }

    public static String getDate() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            mWay = "日";
        } else if ("2".equals(mWay)) {
            mWay = "一";
        } else if ("3".equals(mWay)) {
            mWay = "二";
        } else if ("4".equals(mWay)) {
            mWay = "三";
        } else if ("5".equals(mWay)) {
            mWay = "四";
        } else if ("6".equals(mWay)) {
            mWay = "五";
        } else if ("7".equals(mWay)) {
            mWay = "六";
        }
        return mYear + "年" + mMonth + "月" + mDay + "日" + " 星期" + mWay;
    }

    public static void main(String[] args) {
        Date dates = Dates();
        String string = sdf1.format(dates);
        System.out.println(string);
        String time = "2013-01-29 19:38:21";
        System.out.println(getTime(time));
    }

    public static String getOrderTime() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        return str;

    }

    public static String getSecondTime() {

        SimpleDateFormat formatter = new SimpleDateFormat("MMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        return str;

    }

    public static String getRegTime() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        str = str.replace(" ", "T");
        return str;

    }

    public static String getFormatTime() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        return str;

    }

    public static String getCurTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        return str;

    }

    public static String getShortFormatTime() {

        SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        return str;

    }

    public static String getMonthAndDay() {

        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        return str;

    }

    public static Long farmatTime(String string) {
        Date date = null;
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = Date(sf.parse(string));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null)
            return Long.valueOf(0);
        return date.getTime();
    }

    public static Date Date(Date date) {
        Date datetimeDate;
        datetimeDate = new Date(date.getTime());
        return datetimeDate;
    }

    public static Date Dates() {
        Date datetimeDate;
        Long dates = 1361515285070L;
        datetimeDate = new Date(dates);
        return datetimeDate;
    }

    public static Long getLongNowTime() {
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        return curDate.getTime();
    }

    public static String getTime(String commitDate) {
        if (commitDate == null || commitDate.trim().equals("")) {
            return null;
        }

        try {
            Date date = sdf1.parse((commitDate));
            if (date.getTime() > new Date().getTime()) {
                return getLaterTime(commitDate);
            } else {
                return getBeforeTime(commitDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getRealTime(String time) {
        // time=sdf3.format(time);
        // 2014-10-06 12:19:25
        // time.replaceAll(":", "").replaceAll("-", "").replaceAll(" ", "");
        String month = time.substring(5, 7);
        String day = time.substring(8, 10);
        String hour = time.substring(11, 13);
        String minute = time.substring(14, 16);
        String curtime = getCurTime();
        String curDay = curtime.substring(6, 8);
        if (Integer.valueOf(curDay) != Integer.valueOf(day))
            return month + "月" + day + "日";
        return hour + "时" + minute + "分";
    }

    /**
     * 获取两个日期之间的间隔天数
     *
     * @return
     */
    public static int getGapCount(String startDateStr) {
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = sdf1.parse(startDateStr);
            endDate = sdf1.parse(getFormatTime());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }

    /**
     * 格式化现在之后的日期格式
     *
     * @param commitDate 2103-01-09 00:00
     * @return
     */
    public static String getLaterTime(String commitDate) {

        // TODO Auto-generated method stub
        // 在主页面中设置当天时间
        if (commitDate == null)
            return "";
        if (commitDate.length() == 0)
            return "";
        commitDate = formatTime(commitDate);
        Date nowTime = new Date();
        String currDate = sdf1.format(nowTime);
        Date date = null;
        try {
            // 将给定的字符串中的日期提取出来
            date = sdf1.parse(commitDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int nowDate = Integer.valueOf(currDate.substring(8, 10));
        int commit = Integer.valueOf(commitDate.substring(8, 10));
        String monthDay = sdf2.format(date).substring(5, 12);
        String yearMonthDay = sdf2.format(date).substring(0, 12);
        int month = Integer.valueOf(monthDay.substring(0, 2));
        int day = Integer.valueOf(monthDay.substring(3, 5));
        if (month < 10 && day < 10) {
            monthDay = monthDay.substring(1, 3) + monthDay.substring(4);
        } else if (month < 10) {
            monthDay = monthDay.substring(1);
        } else if (day < 10) {
            monthDay = monthDay.substring(0, 3) + monthDay.substring(4);
        }
        int yearMonth = Integer.valueOf(yearMonthDay.substring(5, 7));
        int yearDay = Integer.valueOf(yearMonthDay.substring(8, 10));
        if (yearMonth < 10 && yearDay < 10) {
            yearMonthDay = yearMonthDay.substring(0, 5) + yearMonthDay.substring(6, 8) + yearMonthDay.substring(9);
        } else if (yearMonth < 10) {
            yearMonthDay = yearMonthDay.substring(0, 5) + yearMonthDay.substring(6);
        } else if (yearDay < 10) {
            yearMonthDay = yearMonthDay.substring(0, 8) + yearMonthDay.substring(9);
        }
        String str = " 00:00:00";
        float currDay = farmatTime(currDate.substring(0, 10) + str);
        float commitDay = farmatTime(commitDate.substring(0, 10) + str);
        int currYear = Integer.valueOf(currDate.substring(0, 4));
        int commitYear = Integer.valueOf(commitDate.substring(0, 4));
        int flag = (int) (farmatTime(commitDate) / 1000 - farmatTime(currDate) / 1000);
        String des = null;
        String hourMin = commitDate.substring(11, 16);
        int temp = flag;
        if (temp < 60) {
            // System.out.println("A");
            if (commitDay > currDay) {
                des = "明天" + hourMin;
            } else {
                des = "刚刚";
            }
        } else if (temp < 60 * 60) {
            // System.out.println("B");
            if (commitDay > currDay) {
                des = "明天" + hourMin;
            } else {
                des = temp / 60 + "分钟后";
            }
        } else if (temp < 60 * 60 * 24) {
            // System.out.println("C");
            int hour = temp / (60 * 60);
            if (commitDay > currDay) {
                des = "明天" + hourMin;
            } else {
                if (hour < 6) {
                    des = hour + "小时后";
                } else {
                    des = hourMin;
                }
            }
        } else if (temp < (60 * 60 * 24 * 2)) {
            // System.out.println("D");
            if (commit - nowDate == 1) {
                des = "明天" + hourMin;
            } else {
                des = "后天" + hourMin;
            }
        } else if (temp < 60 * 60 * 60 * 3) {
            // System.out.println("E");
            if (commit - nowDate == 2) {
                des = "后天" + hourMin;
            } else {
                if (commitYear > currYear) {
                    des = yearMonthDay;
                } else {
                    des = monthDay;
                }
            }
        } else {
            // System.out.println("F");
            if (commitYear > currYear) {
                des = yearMonthDay;
            } else {
                des = monthDay;
            }
        }
        if (des == null) {
            des = commitDate;
        }
        return des;
    }

    /**
     * 格式化今天以前的日期
     *
     * @param commitDate 2103-01-09 00:00
     * @return
     */
    public static String getBeforeTime(String commitDate) {
        // TODO Auto-generated method stub
        // 在主页面中设置当天时间
        if (commitDate == null)
            return "";
        if (commitDate.length() == 0)
            return "";
        // if(commitDate.length() != pat1.length())
        // commitDate = formatTime(commitDate);
        Date nowTime = new Date();
        String currDate = sdf1.format(nowTime);
        Date date = null;
        try {
            // 将给定的字符串中的日期提取出来
            date = sdf1.parse(commitDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int nowDate = Integer.valueOf(currDate.substring(8, 10));
        int commit = Integer.valueOf(commitDate.substring(8, 10));
        String monthDay = sdf2.format(date).substring(5, 12);
        String yearMonthDay = sdf2.format(date).substring(0, 12);
        int month = Integer.valueOf(monthDay.substring(0, 2));
        int day = Integer.valueOf(monthDay.substring(3, 5));
        if (month < 10 && day < 10) {
            monthDay = monthDay.substring(1, 3) + monthDay.substring(4);
        } else if (month < 10) {
            monthDay = monthDay.substring(1);
        } else if (day < 10) {
            monthDay = monthDay.substring(0, 3) + monthDay.substring(4);
        }
        int yearMonth = Integer.valueOf(yearMonthDay.substring(5, 7));
        int yearDay = Integer.valueOf(yearMonthDay.substring(8, 10));
        if (yearMonth < 10 && yearDay < 10) {
            yearMonthDay = yearMonthDay.substring(0, 5) + yearMonthDay.substring(6, 8) + yearMonthDay.substring(9);
        } else if (yearMonth < 10) {
            yearMonthDay = yearMonthDay.substring(0, 5) + yearMonthDay.substring(6);
        } else if (yearDay < 10) {
            yearMonthDay = yearMonthDay.substring(0, 8) + yearMonthDay.substring(9);
        }
        String str = " 00:00:00";
        float currDay = farmatTime(currDate.substring(0, 10) + str);
        float commitDay = farmatTime(commitDate.substring(0, 10) + str);
        int currYear = Integer.valueOf(currDate.substring(0, 4));
        int commitYear = Integer.valueOf(commitDate.substring(0, 4));
        int flag = (int) (farmatTime(currDate) / 1000 - farmatTime(commitDate) / 1000);
        String des = null;
        String hourMin = commitDate.substring(11, 16);
        int temp = flag;
        // Log.e("MyDate","getBeforeTime temp="+temp+" commitDay="+commitDay+" currDay="+currDay+" hourMin="+hourMin);
        if (temp < 60) {
            // System.out.println("A");
            if (commitDay < currDay) {
                des = "昨天" + hourMin;
            } else {
                des = "刚刚";
            }
        } else if (temp < 60 * 60) {
            // System.out.println("B");
            if (commitDay < currDay) {
                des = "昨天" + hourMin;
            } else {
                des = temp / 60 + "分钟前";
            }
        } else if (temp < 60 * 60 * 24) {
            // System.out.println("C");
            int hour = temp / (60 * 60);
            if (commitDay < currDay) {
                des = "昨天" + hourMin;
            } else {
                if (hour < 6) {
                    des = hour + "小时前";
                } else {
                    des = hourMin;
                }
            }
        } else if (temp < (60 * 60 * 24 * 2)) {
            // System.out.println("D");
            if (nowDate - commit == 1) {
                des = "昨天" + hourMin;
            } else {
                des = "前天" + hourMin;
            }
        } else if (temp < 60 * 60 * 60 * 3) {
            // System.out.println("E");
            if (nowDate - commit == 2) {
                des = "前天" + hourMin;
            } else {
                if (commitYear < currYear) {
                    des = yearMonthDay;
                } else {
                    des = monthDay;
                }
            }
        } else {
            // System.out.println("F");
            if (commitYear < currYear) {
                des = yearMonthDay;
            } else {
                des = monthDay;
            }
        }
        if (des == null) {

            des = commitDate;
        }
        return des;
    }

    public static Date Date() {
        Date datetimeDate;
        Long dates = 1361514787384L;
        datetimeDate = new Date(dates);
        return datetimeDate;
    }

    /**
     * 规范时间
     *
     * @param comTime 2103-1-9 00:00
     * @return 2103-01-09 00:00
     */
    public static String formatTime(String comTime) {

        String newTime = null;
        String[] str = comTime.split(" ");
        str[1] = str[1] + ":00";
        String[] days = str[0].split("-");
        if (Integer.parseInt(days[1]) < 10) {
            days[1] = "0" + days[1];
        }
        if (Integer.parseInt(days[2]) < 10 && days[2].length() == 1) {
            days[2] = "0" + days[2];
        }
        newTime = days[0] + "-" + days[1] + "-" + days[2] + " " + str[1];
        return newTime;
    }

    public static String formatTime2(String comTime) {

        // for 201312122934 to 2013-12-12 12:29:34
        String newTime = null;
        String[] str = comTime.split("-");
        String time = str[0];
        String first = time.substring(0, 1);
        String haveb = "";
        String leftdata = "";

        if (time.contains("B")) {
            haveb = "B-";
            time = time.substring(1);
            if (str.length > 1)
                leftdata = comTime.substring(15);
        } else {
            if (str.length > 1)
                leftdata = comTime.substring(14);
        }
        String year = time.substring(0, 4);
        String month = time.substring(4, 6);
        String day = time.substring(6, 8);
        String hour = time.substring(8, 10);
        String minute = time.substring(10, 12);
        String second = time.substring(12, 14);
        newTime = haveb + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second + leftdata;
        return newTime;
    }

    public static int getCurDateDiff(String oldtime) {
        if (oldtime == null || oldtime.length() == 0)
            return 0;
        Long now = getLongNowTime();
        Long old = farmatTime(oldtime);
        Long dif = now - old;
        return dif.intValue() / 1000;
    }

    public static String formatSecond(int second) {

        int h = second / (60 * 60);
        int m = (second - h * (60 * 60)) / 60;
        int s = second - h * (60 * 60) - m * 60;

        String data = "";
        if (h != 0)
            data = h + "小时";
        if (m != 0)
            data = data + m + "分";
        if (s != 0)
            data = data + s + "秒";
        if (data.length() == 0)
            data = "0秒";
        return data;
    }

    public static int getTimeDelta(Date date1, Date date2) {
        long timeDelta = (date1.getTime() - date2.getTime()) / 1000;// 单位是秒
        int secondsDelta = timeDelta > 0 ? (int) timeDelta : (int) Math.abs(timeDelta);
        return secondsDelta;
    }

    /***
     * 两个日期相差多少秒
     *
     * @param dateStr1 :yyyy-MM-dd HH:mm:ss
     * @param dateStr2 :yyyy-MM-dd HH:mm:ss
     */
    public static int getTimeDelta(String dateStr1, String dateStr2) {
        Date date1 = parseDateByPattern(dateStr1, pat1);
        Date date2 = parseDateByPattern(dateStr2, pat1);
        return getTimeDelta(date1, date2);
    }

    public static Date parseDateByPattern(String dateStr, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 得到秒数
     *
     * @param beforTime
     * @param seconds
     * @return
     */
    public static long addSecondsTime(String beforTime, int seconds) {
        String afterTime = null;
        Date date = parseDateByPattern(beforTime, pat1);
        long ms = (date.getTime() + seconds * 1000) / 1000;
        // afterTime = sdf1.format(new Date(ms));
        return ms;
    }

    /**
     * 得到相差秒数
     *
     * @param beforTime
     * @param seconds
     * @return
     */
    public static long getSecondsDelta(long seconds, String time) {
        String afterTime = null;
        Date date = parseDateByPattern(time, pat1);
        long ms = seconds - date.getTime() / 1000;
        return ms;
    }

    /**
     * 把秒转化为日期 00:00:00
     *
     * @param second
     * @return
     */
    public static String convertSencondToDate(long millisUntilFinished)

    {
        millisUntilFinished = millisUntilFinished * 1000;
        long hour = millisUntilFinished / (60 * 60 * 1000);
        long minute = (millisUntilFinished - hour * 60 * 60 * 1000) / (60 * 1000);
        long second = (millisUntilFinished - hour * 60 * 60 * 1000 - minute * 60 * 1000) / 1000;
        if (second >= 60) {
            second = second % 60;
            minute += second / 60;
        }
        if (minute >= 60) {
            minute = minute % 60;
            hour += minute / 60;
        }
        String sh = "";
        String sm = "";
        String ss = "";
        if (hour < 10) {
            sh = "0" + String.valueOf(hour);
        } else {
            sh = String.valueOf(hour);
        }
        if (minute < 10) {
            sm = "0" + String.valueOf(minute);
        } else {
            sm = String.valueOf(minute);
        }
        if (second < 10) {
            ss = "0" + String.valueOf(second);
        } else {
            ss = String.valueOf(second);
        }
        return sh + ":" + sm + ":" + ss;
    }
}
