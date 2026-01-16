/**  
 * @Title: StringUtil.java
 * @package com.foreamlib.util
 * @Description: TODO()
 * @author ChengZi 
 * 
 * @version V1.0  
 */

package com.drift.foreamlib.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @ClassName: StringUtil
 * @Description: TODO()
 * @author ChengZi
 * 
 */
public class StringUtil {
	public static String getDateString(String date) {
		if(date==null)
			return "";
		System.out.println("date--->" + date);
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(Long.parseLong(date)*1000).toString();
	}
	public static String getDuration(long millisecond) {	
		DecimalFormat df = new DecimalFormat("00");
		long second = millisecond / 1000;
		return df.format(second / 3600) + ":" + df.format(second / 60 % 60)
				+ ":" + df.format(second % 60);
	}
	public static boolean isNon(String str) {
		if (str == null || str.length() == 0) {
			return true;
		} else {
			return false;
		}
	}
	public static String getDuration2(long millisecond) {	
		DecimalFormat df = new DecimalFormat("00");
		long second = millisecond / 1000;
		return df.format(second / 3600) + ":" + df.format(second / 60 % 60)+ ":" + df.format(second % 60);
	}
	public static String getShortSizeString(long size){
		long value = size;
		String result = null;
		if(value<1024){
			result = size+"";
		}else if(value<1024L*1024L){
			float f= ((float)value)/(1024L);
			result = String.format(Locale.getDefault(), "%2.1fKB", f);
		}else if(value<1024L*1024L*1024L){
			float f= ((float)value)/(1024L*1024L);
			result = String.format(Locale.getDefault(), "%2.2fMB", f);
		}
		else{
			float f= ((float)value)/(1024L*1024L*1024L);
			result = String.format(Locale.getDefault(), "%2.2fGB", f);
		}
		return result;
	}

	public static boolean isForeamCamSSid(String SSID) {
		boolean isForeamSSID = false;
		if (// SSID.toLowerCase(Locale.getDefault()).contains("foream") == true
				SSID.toLowerCase(Locale.getDefault()).contains("ghost") == true || SSID.toLowerCase(Locale.getDefault()).contains("spider") == true
						|| SSID.toLowerCase(Locale.getDefault()).contains("finder") == true || SSID.toLowerCase(Locale.getDefault()).contains("stealth") == true
						|| SSID.toLowerCase(Locale.getDefault()).contains("gopro") == true || SSID.toLowerCase(Locale.getDefault()).contains("x-sports") == true
						|| SSID.toLowerCase(Locale.getDefault()).contains("x1") == true || SSID.toLowerCase(Locale.getDefault()).contains("compass") == true
						|| SSID.toLowerCase(Locale.getDefault()).contains("compassb") == true || SSID.toLowerCase(Locale.getDefault()).contains("x3") == true) {
			isForeamSSID = true;
		}
		return isForeamSSID;
	}
}
