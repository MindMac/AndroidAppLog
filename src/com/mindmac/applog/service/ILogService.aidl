package com.mindmac.applog.service;

import com.mindmac.applog.service.ParcelableLogMethod;

interface ILogService {
	boolean getLogEnable(int uid, String className, String methodName);
	long getLogEnableByClass(int uid, String className);
	long getLogEnableByUid(int uid);
	List<ParcelableLogMethod> getLogMethodList(int uid, int limit, int offest);
	void setLogEnable(int uid, String className, String methodName, boolean isLogEnable);
	int deleteLogRecord(int uid, String className, String methodName);
	
	
	boolean getHookEnable(String packageName);
	void setHookEnable(String packageName, boolean isHookEnable);
	int deleteHookRecord(String packageName);
	
	void setSetting(String name, String value);
	String getSetting(String name);
	
}
