package com.common.database;


import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class AbstractDataBase extends SQLiteOpenHelper{

	private DbTable[] tableInfos = new DbTable[]{};

	protected AbstractDataBase(Context context, String name, int version) {
		super(context, name, null, version);
	}

	protected void setTable( DbTable[] tableInfos){
		this.tableInfos = tableInfos;  
		for(DbTable table:tableInfos){
			table.setDbHelper(this);
		}
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		for(DbTable tableInfo:tableInfos){
			String sql="CREATE TABLE "+tableInfo.TABLE_NAME+" (";
			
			sql+= "_ID INTEGER PRIMARY KEY";
			Map<String, String> columns = tableInfo.getColumns();
			for(String key:columns.keySet()){
				String value = columns.get(key);
				sql+= (","+key + " "+value);
			}
			sql+=" );";
			db.execSQL(sql);	
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for(DbTable tableInfo:tableInfos){
	        db.execSQL("DROP TABLE IF EXISTS " + tableInfo.TABLE_NAME);  
		}
        onCreate(db);
	}		
}
