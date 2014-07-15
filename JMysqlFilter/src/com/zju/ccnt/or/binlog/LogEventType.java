package com.zju.ccnt.or.binlog;

import java.util.HashMap;
import java.util.Map;

public enum LogEventType {
		  UNKNOWN_EVENT(0),
		  START_EVENT_V3(1), 
		  QUERY_EVENT(2), 
		  STOP_EVENT(3), 
		  ROTATE_EVENT(4), 
		  INTVAR_EVENT(5), 
		  LOAD_EVENT(6),
		  SLAVE_EVENT(7), 
		  CREATE_FILE_EVENT(8), 
		  APPEND_BLOCK_EVENT(9), 
		  EXEC_LOAD_EVENT(10), 
		  DELETE_FILE_EVENT(11), 
		  NEW_LOAD_EVENT(12), 
		  RAND_EVENT(13),
		  USER_VAR_EVENT(14), 
		  FORMAT_DESCRIPTION_EVENT(15), 
		  XID_EVENT(16), 
		  BEGIN_LOAD_QUERY_EVENT(17), 
		  EXECUTE_LOAD_QUERY_EVENT(18), 
		  TABLE_MAP_EVENT (19), 
		  PRE_GA_WRITE_ROWS_EVENT (20), 
		  PRE_GA_UPDATE_ROWS_EVENT(21), 
		  PRE_GA_DELETE_ROWS_EVENT (22), 
		  WRITE_ROWS_EVENT (23), 
		  UPDATE_ROWS_EVENT(24), 
		  DELETE_ROWS_EVENT (25), 
		  INCIDENT_EVENT(26), 
		  HEARTBEAT_LOG_EVENT(27), 
		  ENUM_END_EVENT(28); 
		  /* end marker */
		  private static Map<Integer,LogEventType> map=new HashMap<Integer, LogEventType>();
		  LogEventType(int value){
		  }
		  static{
			  map.put(0, UNKNOWN_EVENT);
			  map.put(1, START_EVENT_V3);
			  map.put(2, QUERY_EVENT);
			  map.put(3, STOP_EVENT);
			  map.put(4, ROTATE_EVENT);
			  map.put(5, INTVAR_EVENT);
			  map.put(6, LOAD_EVENT);
			  map.put(7, SLAVE_EVENT);
			  map.put(8, CREATE_FILE_EVENT);
			  map.put(9, APPEND_BLOCK_EVENT);
			  map.put(10, EXEC_LOAD_EVENT);
			  map.put(11, DELETE_FILE_EVENT);
			  map.put(12, NEW_LOAD_EVENT);
			  map.put(13, RAND_EVENT);
			  map.put(14, USER_VAR_EVENT);
			  map.put(15, FORMAT_DESCRIPTION_EVENT);
			  map.put(16, XID_EVENT);
			  map.put(17, BEGIN_LOAD_QUERY_EVENT);
			  map.put(18, EXECUTE_LOAD_QUERY_EVENT);
			  map.put(19, TABLE_MAP_EVENT );
			  map.put(20, PRE_GA_WRITE_ROWS_EVENT );
			  map.put(21, PRE_GA_UPDATE_ROWS_EVENT );
			  map.put(22, PRE_GA_DELETE_ROWS_EVENT );
			  map.put(23, WRITE_ROWS_EVENT );
			  map.put(24, UPDATE_ROWS_EVENT );
			  map.put(25, DELETE_ROWS_EVENT );
			  map.put(26, INCIDENT_EVENT);
			  map.put(27, HEARTBEAT_LOG_EVENT);
		  }
		  public static LogEventType get(int pos){
			LogEventType type= map.get(pos);
			return type;
		  }
}
