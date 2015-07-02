package util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateHelper {
	public static String parseString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		if (date != null)
			return sdf.format(date);
		else
			return null;
	}
	public static String parseSimpleString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (date != null)
			return sdf.format(date);
		else
			return null;
	}
	public static String parseStringCN(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
		if (date != null)
			return sdf.format(date);
		else
			return null;
	}
	public static String parseSimpleStringCN(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		if (date != null)
			return sdf.format(date);
		else
			return null;
	}
	public static String parseStringDetail(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (date != null)
			return sdf.format(date);
		else
			return null;
	}

	public static String parseStringDetail2(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		if (date != null)
			return sdf.format(date);
		else
			return null;
	}
	public static Date parseDateYMD(String s) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if(s==null)return null;
			else
			return sdf.parse(s);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			return null;
		}
	
	}
	public static Date parseDate(String s) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {

			return sdf.parse(s);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Date parseDateDetail(String s) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {

			return sdf.parse(s);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static Date parseTimestampToDate(Timestamp t){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义格式，不显示毫秒 
		String str = df.format(t); 
		return parseDateDetail(str);
		


	}

	public static void main(String[] args) {
		System.out.println(DateHelper.getPreMonthString(new Date()));
	}
	  public static String getCurrentYYYYMMDD(){   
		  
	        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);   
	        formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));   
	        Date currTime = new Date();   
	        String thisTime = new String(formatter.format(currTime));   
	        return thisTime;   
	}   
	    public static Date getCurrentYYYYMMDDHHMMSS(){   
	           
	        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);   
	        formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));   
	        Date currTime = new Date();   
	        String thisTime = new String(formatter.format(currTime));   
	        return DateHelper.parseDateDetail(thisTime);
	}   
	    
	    
	 public static  String getPreMonthString(Date a){
		 String as=a.toLocaleString();
		
		 String year=as.substring(0,4);
		 String month="";
		 switch(a.getMonth()){
		 case 0: month="12";  break;
		 case 1: month="01";break;
		 case 2:month="02";break;
		 case 3:month="03";break;
		 case 4:month="04";break;
		 case 5:month="05";break;
		 case 6:month="06";break;
		 case 7:month="07";break;
		 case 8:month="08";break;
		 case 9:month="09";break;
		 case 10:month="10";break;
		 case 11:month="11";break;
		 }
	
		 return year+""+month;
	 }
}
