package util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SqlLiteHelper {
	
	SQLiteDatabase db;
	public SqlLiteHelper(Context ctx){
		db =ctx.openOrCreateDatabase("vpdn_db.db", ctx.MODE_PRIVATE, null);
		db.execSQL("create table if not exists imsi_info(imsi text, time text)");
	}
	
	public boolean addNewImsi(String imsi){
		
		String sql="INSERT INTO imsi_info(imsi, time)VALUES('"+imsi+"','"+DateHelper.parseStringDetail(DateHelper.getCurrentYYYYMMDDHHMMSS())+"')";
		db.execSQL(sql);
		return true;
	}
	
	public void deleteAll(){
		db.execSQL("delete from imsi_info");
	}
	
	/**
	 * 4066666,2014-11-23-33/4066666,2014-11-23-33/4066666,2014-11-23-33
	 * @return
	 */
	public String getAllRecords(){
		Cursor c = db.query("imsi_info", null, null, null, null, null, null);
		String returned="";
		c.moveToFirst();
		while(!c.isAfterLast()){
		String name = c.getString(0);
		String time=c.getString(1);
		returned+=name+","+time+"/";
		    c.moveToNext();
		}
		return returned;
	
	}


}
