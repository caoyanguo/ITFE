package com.cfcc.itfe.facade.time;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.mule.util.DateUtils;

import com.cfcc.deptone.common.util.DateUtil;

public class TimeFacade {

	/**
	 * 返回当前系统时间 格式 "YYYYMMDD"
	 * 
	 * @return
	 */
	public static String getCurrentStringTime() {
		Date date = new Date();
		return DateUtil.date2String2(date);
	}
	
	/**
	 * 
	 * 返回当前系统时间的前一天 格式 "YYYYMMDD"
	 * 
	 * @return
	 */
	public static String getCurrentStringTimebefor(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		return DateUtil.date2String2(calendar.getTime());
	}

	/**
	 * 返回当前系统时间 格式 "YYYY-MM-DD"
	 * 
	 * @return
	 */
	public static String getCurrent2StringTime() {

		return getCurrentDateTime().toString();
	}

	/**
	 * 返回当前系统时间 格式 java.sql.date
	 * 
	 * @return
	 */
	public static java.sql.Date getCurrentDateTime() {
		Date date = new Date();
		return new java.sql.Date(date.getTime());
	}

	/**
	 * 返回当前系统时间 格式 java.sql.date
	 * 
	 * 
	 */
	public static java.util.Date getCurrentDate() {
		return parseDate(getCurrentStringTime());
	}

	public static String formatDate(Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	
	/**
	 * 按照指定格式返回当前系统时间  
	 * 
	 * @return
	 */
	public static String getCurrentStringTime(String pattern) {
		return formatDate(new Date(),pattern);
	}
	
	/**
	 * 
	 * 
	 * 
	 */
	public static java.util.Date parseDate(String dateString, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		if (StringUtils.isBlank(dateString))
			return null;
		try {
			sdf.setLenient(false);
			return sdf.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 
	 * 
	 * 
	 */
	public static java.util.Date parseDate(String dateString) {
		return parseDate(dateString, "yyyyMMdd");
	}

	public static void main(String[] argv) {
		Calendar calendar = Calendar.getInstance();
		
		
		Timestamp  a= new java.sql.Timestamp(calendar.getTimeInMillis());
		
		
	
	}
	
	
	/**
	 * 取得两个时间之间的各月份的最后一天
	 * @param sdate
	 * @param edate
	 * @return
	 */
	public static String[] getMontharr(String sdate,String edate){
		String sdatesub=sdate.substring(0, 6);
		String edatesub=edate.substring(0, 6);
		int i=Integer.parseInt(sdatesub);
		String strarr[] = new String[Integer.parseInt(edatesub)-Integer.parseInt(sdatesub)+1];
		int j=0;
		while(i<Integer.parseInt(edatesub)){
			strarr[j]=getEndDateOfMonth(String.valueOf(i)+"01");
			i+=1;
			j+=1;
		}
		strarr[j]=edate;
		return strarr;
	}
	
	/**
	* 获取一个月的最后一天
	*
	* @param dat
	* @return
	*/
	public static String getEndDateOfMonth(String dat) {// yyyyMMdd
		String str = dat.substring(0, 6);
		String month = dat.substring(4, 6);
		int mon = Integer.parseInt(month);
		if (mon == 1 || mon == 3 || mon == 5 || mon == 7 || mon == 8
				|| mon == 10 || mon == 12) {
			str += "31";
		} else if (mon == 4 || mon == 6 || mon == 9 || mon == 11) {
			str += "30";
		} else {
			if (isLeapYear(dat)) {
				str += "29";
			} else {
				str += "28";
			}
		}
		return str;
	}
	
	/**
	* 判断是否润年
	*
	* @param ddate
	* @return
	*/
	public static boolean isLeapYear(String ddate) {

		/**
		* 详细设计： 1.被400整除是闰年，否则： 2.不能被4整除则不是闰年 3.能被4整除同时不能被100整除则是闰年
		* 3.能被4整除同时能被100整除则不是闰年
		*/
		Date d = strToDate(ddate);
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(d);
		int year = gc.get(Calendar.YEAR);
		
		if ((year % 400) == 0)
			return true;
		else if ((year % 4) == 0) {
			if ((year % 100) == 0)
				return false;
			else
				return true;
		}else
			return false;
	}
	
	
	/**
	* 将短时间格式字符串转换为时间 yyyyMMdd
	*
	* @param strDate
	* @return
	*/
	public static Date strToDate(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

}
