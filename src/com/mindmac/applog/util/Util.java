package com.mindmac.applog.util;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;


public class Util {
	public static final String SELF_PACKAGE_NAME = "com.mindmac.applog";
	public static final String LOG_TAG = "AppLog";
	public static final int ANDROID_UID = Process.SYSTEM_UID;
	public static final String DEFAULT_PACKAGE = "default-package";
	public static final String JSON_OUTPUT_SETTING = "json_output";
	
	public static final int DB_FETCH_LIMIT = 10;
	
	public enum ShowStatus{
		AllApps, SystemApps, UserApps
	}
	
	public static ArrayList<String> cExcludedClasses = new ArrayList<String>();
	static{
		cExcludedClasses.add("android.support");
	}
	
	public static File getLogFile() {
		return new File(Environment.getDataDirectory() + File.separator + "data" + File.separator
				+ Util.SELF_PACKAGE_NAME + File.separator + "log.txt");
	}
	

	private static void createLogFile(){
		File logFile = getLogFile();
		try {
			logFile.createNewFile();
			logFile.setReadable(true, false);
			logFile.setWritable(true, false);
		} catch (Throwable ex) {
			Log.d(Util.LOG_TAG, ex.toString());
		}
		
	}
	

	public static void initLogFile(){
		File logFile = getLogFile();
		if(logFile.exists()){
			if(logFile.delete())
				createLogFile();
		}else
			createLogFile();
	}
	
	// The parameter methodName here is full path with package and class name
	public static void bug(String methodName, Throwable ex) {
		log(methodName, ex.toString() + " uid=" + Process.myUid() + "\n" + Log.getStackTraceString(ex));
	}
	

	public static void log(String methodName, String msg) {
		logData(methodName, msg);
	}

	private static void logData(String methodName, String message) {
		if (getLogFile().exists()) {
			String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Calendar.getInstance().getTime());
			String tag = (methodName == null ? Util.LOG_TAG : 
				String.format("%s/%s", Util.LOG_TAG, methodName));

			FileWriter fw = null;
			try {
				fw = new FileWriter(getLogFile(), true);
				fw.write(String.format("%s %s: %s\n", time, tag, message));
				fw.flush();
			} catch (Throwable ex) {
				Log.d(Util.LOG_TAG, ex.toString());
			} finally {
				if (fw != null)
					try {
						fw.close();
						getLogFile().setReadable(true, false);
						getLogFile().setWritable(true, false);
					} catch (Throwable ex) {
						Log.d(Util.LOG_TAG, ex.toString());
					}
			}
		}else{
			initLogFile();
		}
		
	}
	
	public static void setPermission(String path, int mode, int uid, int gid) {
		try {
			Class<?> fileUtils = Class.forName("android.os.FileUtils");
			java.lang.reflect.Method setPermissions = fileUtils
					.getMethod("setPermissions", String.class, int.class, int.class, int.class);
			setPermissions.invoke(null, path, mode, uid, gid);
		} catch (Throwable ex) {
			Util.bug(null, ex);
		}
	}
	
	public static boolean isAndroidAppLogEnabled() {
		// Will be hooked to return true
		Util.log(null, "AndroidAppLog not enabled");
		return false;
	}
	
	// Parse package and class name from the given class descriptor,
	// class descriptor is something like "Lcom/test/java/hello;"
	public static String[] getPacakgeClassName(String classDescriptor){
		String[] packageClassName = new String[2];
		String trimedClassDescriptor = classDescriptor.substring(1, classDescriptor.length()-1);
		String[] stringItems = trimedClassDescriptor.split("/");
		
		ArrayList<String> listItems = new ArrayList<String>();
		int i = 0;
		for(i=0; i < stringItems.length-1; i++){
			listItems.add(stringItems[i]);
		}
		String packageName = TextUtils.join(".", listItems);
		String className = stringItems[i];
		
		packageClassName[0] = packageName;
		packageClassName[1] = className;
		return packageClassName;
	}
	
	// Get installed applications 
	public static List<AppInfoEx> getAppInfoExList(Context context, ShowStatus showStatus, ProgressDialog progressDialog) {
		PackageManager pm = context.getPackageManager();

		// Create AppInfoExList
		List<AppInfoEx> AppInfoExList = new ArrayList<AppInfoEx>();
		List<ApplicationInfo> appInfoList = pm.getInstalledApplications(PackageManager.GET_META_DATA);
		
		progressDialog.setMax(appInfoList.size());
		for (int i = 0; i < appInfoList.size(); i++){
			progressDialog.setProgress(i + 1);
			AppInfoEx appInfoEx = new AppInfoEx(context, appInfoList.get(i));
			if(showStatus == ShowStatus.UserApps && appInfoEx.isSystem())
				continue;
			if(showStatus == ShowStatus.SystemApps && !appInfoEx.isSystem())
				continue;
			AppInfoExList.add(appInfoEx);
		}

		// Sort result
		Collections.sort(AppInfoExList);
		return AppInfoExList;
	}
}
