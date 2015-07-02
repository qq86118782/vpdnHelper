package util;

import java.lang.reflect.Method;

import android.telephony.SignalStrength;

public class LteHelper {
	SignalStrength ss;
	public LteHelper(SignalStrength ss2){
		ss=ss2;
	}
		
	public Object getLteSignalStrength(){
		try {
			Method[] methods = android.telephony.SignalStrength.class
					.getMethods();
			for (Method mthd : methods) {
				if (mthd.getName().equals("getLteSignalStrength")
					) {
					return mthd.invoke(ss);
				
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
	public Object getLteRsrp(){
		try {
			Method[] methods = android.telephony.SignalStrength.class
					.getMethods();
			for (Method mthd : methods) {
				if (mthd.getName().equals("getLteRsrp")
					) {
					return mthd.invoke(ss);
				
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
	public Object getLteRsrq(){
		try {
			Method[] methods = android.telephony.SignalStrength.class
					.getMethods();
			for (Method mthd : methods) {
				if (mthd.getName().equals("getLteRsrq")
					) {
					return mthd.invoke(ss);
				
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
	public Object getLteRssnr(){
		try {
			Method[] methods = android.telephony.SignalStrength.class
					.getMethods();
			for (Method mthd : methods) {
				if (mthd.getName().equals("getLteRssnr")
					) {
					return mthd.invoke(ss);
				
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
	public Object getLteCqi(){
		try {
			Method[] methods = android.telephony.SignalStrength.class
					.getMethods();
			for (Method mthd : methods) {
				if (mthd.getName().equals("getLteCqi")
					) {
					return mthd.invoke(ss);
				
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
}
