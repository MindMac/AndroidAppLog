package com.mindmac.applog.service;

import java.util.ArrayList;
import java.util.List;

import com.mindmac.applog.util.Util;

public class LogManager {
	private static final String SERVICE_NOT_UP = "Log service not up";
	
	public static boolean getLogEnable(int uid, String className, String methodName){
		boolean isLogEnable = false;
		try{
			ILogService iLogService = LogService.getLogServiceClient();
			if(iLogService == null){
				Util.log(null, SERVICE_NOT_UP);
				return false;
			}
			else{
				isLogEnable = iLogService.getLogEnable(uid, className, methodName);
			}
		}catch(Throwable ex){
			Util.bug(null, ex);
		}
		return isLogEnable;
	}
	
	
	public static boolean getLogEnableByClass(int uid, String className){
		boolean isLogEnable = false;
		try{
			ILogService iLogService = LogService.getLogServiceClient();
			if(iLogService == null){
				Util.log(null, SERVICE_NOT_UP);
				return false;
			}
			else{
				isLogEnable = (iLogService.getLogEnableByClass(uid, className) > 0);
			}
		}catch(Throwable ex){
			Util.bug(null, ex);
		}
		return isLogEnable;
	}
	
	public static long getLogEnableCountByClass(int uid, String className){
		long totalCount = 0;
		try{
			ILogService iLogService = LogService.getLogServiceClient();
			if(iLogService == null){
				Util.log(null, SERVICE_NOT_UP);
				return 0;
			}
			else{
				totalCount = iLogService.getLogEnableByClass(uid, className);
			}
		}catch(Throwable ex){
			Util.bug(null, ex);
		}
		return totalCount;
	}
	
	
	
	public static boolean getLogEnableByUid(int uid){
		boolean isLogEnable = false;
		try{
			ILogService iLogService = LogService.getLogServiceClient();
			if(iLogService == null){
				Util.log(null, SERVICE_NOT_UP);
				return false;
			}
			else{
				isLogEnable = (iLogService.getLogEnableByUid(uid) > 0);
			}
		}catch(Throwable ex){
			Util.bug(null, ex);
		}
		return isLogEnable;
	}
	
	public static long getLogEnableCountByUid(int uid){
		long totalCount = 0;
		try{
			ILogService iLogService = LogService.getLogServiceClient();
			if(iLogService == null){
				Util.log(null, SERVICE_NOT_UP);
				return 0;
			}
			else{
				totalCount = iLogService.getLogEnableByUid(uid);
			}
		}catch(Throwable ex){
			Util.bug(null, ex);
		}
		return totalCount;
	}
	
	public static List<ParcelableLogMethod> getLogMethodList(int uid, int limit, int offest){
		ArrayList<ParcelableLogMethod> logMethodList = new ArrayList<ParcelableLogMethod>();
		
		try{
			ILogService iLogService = LogService.getLogServiceClient();
			if(iLogService == null){
				Util.log(null, SERVICE_NOT_UP);
				return logMethodList;
			}
			else{
				logMethodList = (ArrayList<ParcelableLogMethod>) iLogService.getLogMethodList(uid, limit, offest);
			}
		}catch(Throwable ex){
			Util.bug(null, ex);
		}
		return logMethodList;
	}
	
	public static void setLogEnable(int uid, String className, String methodName, boolean isLogEnable){
		try{
			ILogService iLogService = LogService.getLogServiceClient();
			if(iLogService == null){
				Util.log(null, SERVICE_NOT_UP);
			}
			else{
				iLogService.setLogEnable(uid, className, methodName, isLogEnable);
			}
		}catch(Throwable ex){
			Util.bug(null, ex);
		}
	}
	

	public static int deleteLogRecordByClass(int uid, String className){
		int deletedNum = 0;
		if(className == null)
			return 0;
		try{
			ILogService iLogService = LogService.getLogServiceClient();
			if(iLogService == null){
				Util.log(null, SERVICE_NOT_UP);
			}
			else{
				iLogService.deleteLogRecord(uid, className, null);
			}
		}catch(Throwable ex){
			Util.bug(null, ex);
		}
		
		return deletedNum;
	}
	
	public static int deleteLogRecordByMethod(int uid, String className, String methodName){
		int deletedNum = 0;
		if(className == null && methodName == null)
			return 0;
		try{
			ILogService iLogService = LogService.getLogServiceClient();
			if(iLogService == null){
				Util.log(null, SERVICE_NOT_UP);
			}
			else{
				iLogService.deleteLogRecord(uid, className, methodName);
			}
		}catch(Throwable ex){
			Util.bug(null, ex);
		}
		
		return deletedNum;
	}
	
	public static boolean getHookEnable(String packageName){
		boolean isHookEnable = false;
		try{
			ILogService iLogService = LogService.getLogServiceClient();
			if(iLogService == null){
				Util.log(null, SERVICE_NOT_UP);
				return false;
			}
			else{
				isHookEnable = iLogService.getHookEnable(packageName);
			}
		}catch(Throwable ex){
			Util.bug(null, ex);
		}
		return isHookEnable;
	}
	
	public static void setHookEnable(String packageName, boolean isHookEnable){
		try{
			ILogService iLogService = LogService.getLogServiceClient();
			if(iLogService == null){
				Util.log(null, SERVICE_NOT_UP);
			}
			else{
				iLogService.setHookEnable(packageName, isHookEnable);
			}
		}catch(Throwable ex){
			Util.bug(null, ex);
		}
	}
	
	public static int deleteHookRecord(String packageName){
		int deletedNum = 0;
		try{
			ILogService iLogService = LogService.getLogServiceClient();
			if(iLogService == null){
				Util.log(null, SERVICE_NOT_UP);
				return deletedNum;
			}
			else{
				deletedNum = iLogService.deleteHookRecord(packageName);
			}
		}catch(Throwable ex){
			Util.bug(null, ex);
		}
		
		return deletedNum;
	}
	
	public static void setSetting(String name, String value){
		try{
			ILogService iLogService = LogService.getLogServiceClient();
			if(iLogService == null){
				Util.log(null, SERVICE_NOT_UP);
			}
			else{
				iLogService.setSetting(name, value);
			}
		}catch(Throwable ex){
			Util.bug(null, ex);
		}
	
	}
	
	public static String getSetting(String name){
		String value = null;
		try{
			ILogService iLogService = LogService.getLogServiceClient();
			if(iLogService == null){
				Util.log(null, SERVICE_NOT_UP);
				return null;
			}
			else{
				value = iLogService.getSetting(name);
			}
		}catch(Throwable ex){
			Util.bug(null, ex);
		}
		
		return value;
	}

}
