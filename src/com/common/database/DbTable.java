package com.common.database;

import java.util.HashMap;
import java.util.Map;

public class DbTable {
	public enum collumsType{
		INTEGER,
		TEXT,
	}
	public final String TABLE_NAME;
	private Map<String,String> COLUMNS = new HashMap<String, String>();
	protected AbstractDataBase dbHelper = null;

	protected DbTable(String tableName){
		this.TABLE_NAME = tableName;
	}
	
	protected void addColumns(String key, collumsType value){
		COLUMNS.put(key, value.toString());
	}
	
	
	
	
	void setDbHelper(AbstractDataBase dbHelper){ 
		this.dbHelper = dbHelper;
	}
	Map<String, String> getColumns() {
		return new HashMap<String, String>(COLUMNS);
	}
}
