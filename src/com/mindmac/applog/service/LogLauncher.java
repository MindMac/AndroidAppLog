package com.mindmac.applog.service;

import java.lang.reflect.Method;
import java.util.ArrayList;

import com.mindmac.applog.util.Util;
import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.os.Process;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.XC_MethodHook;
import static de.robv.android.xposed.XposedHelpers.findClass;

// This class will be called by Xposed
@SuppressLint("DefaultLocale")
public class LogLauncher implements IXposedHookLoadPackage, IXposedHookZygoteInit {	
	private static XC_MethodHook xcMethodHook = null;
	
	public void initZygote(StartupParam startupParam) throws Throwable {
		
		// Initialize log file
		Util.initLogFile();
		Util.log(null, "Initialize log file");
				
		Util.log(null, String.format("Load %s", startupParam.modulePath));
		
		Util.log(null, "Setup database");
		LogService.setupDatabase();

		// Register Privacy service
		try {
			// frameworks/base/services/java/com/android/server/SystemServer.java
			Class<?> cSystemServer = findClass("com.android.server.SystemServer", null);
			Method mMain = cSystemServer.getDeclaredMethod("main", String[].class);
			XposedBridge.hookMethod(mMain, new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					LogService.registerLogService();
				}
			});
		} catch (Throwable ex) {
			Util.bug(null, ex);
		}
		
		// Create hook method
		xcMethodHook = new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				if (!param.hasThrowable())
					try {
						if (Process.myUid() <= 0)
							return;
						after(param);
					} catch (Throwable ex) {
						Util.bug(null, ex);
						throw ex;
					}
			}
		};
		
	}

	// Call when package loaded
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
		final ClassLoader classLoader = lpparam.classLoader;
		ApplicationInfo appInfo = lpparam.appInfo;
		final int uid = appInfo.uid;
		String packageName = appInfo.packageName;
		
		if(packageName == null)
			return;
		
		// Hook self
		if(packageName.equals(Util.SELF_PACKAGE_NAME)){
			hookSelf(classLoader);
		}
		
		// Check if need to hook
		long totalLogCount = LogManager.getLogEnableCountByUid(uid);
		if(totalLogCount <= 0)
			return;

		ArrayList<ParcelableLogMethod> logMethodList = new ArrayList<ParcelableLogMethod>();
		
		for(int i=0; i<totalLogCount; i += Util.DB_FETCH_LIMIT){
			logMethodList.addAll(LogManager.getLogMethodList(uid, Util.DB_FETCH_LIMIT, i));
		}
		
		for(ParcelableLogMethod logMethod : logMethodList){
			hook(logMethod.className, logMethod.methodName, classLoader);
		}

	}
	
	// Hook self to check if AndroidAppLog is enabled by Xposed
	private static void hookSelf(ClassLoader classLoader){
		try {
			// Find hook class
			String hookClassName = Util.class.getName();
			Class<?> hookClass = findClass(hookClassName, classLoader);
			if (hookClass == null) {
				String message = String.format("Hook-Class not found: %s", hookClassName);
				Util.log(null, message);
				return;
			}

			// Add hook
			String hookMethodName = "isAndroidAppLogEnabled";
			for (Method method : hookClass.getDeclaredMethods()){
				if (method.getName().equals(hookMethodName)){
					XposedBridge.hookMethod(method, new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
							param.setResult(true);
						}
					});
				}
			}
		} catch (Throwable ex) {
			Util.bug(null, ex);
		}
	}
	
	private static void hook(String className, final String methodName, ClassLoader classLoader){
		try {
				// Don't hook excluded class
				for(String excludedClass : Util.cExcludedClasses){
					if(className.startsWith(excludedClass))
						return;
				}
				
				// Find hook class
				Class<?> hookClass = findClass(className, classLoader);
				if (hookClass == null) {
					String message = String.format("Hook-Class not found: %s", className);
					Util.log(null, message);
					return;
				}
	
				// Add hook
				for (Method method : hookClass.getDeclaredMethods()){
					if (method.getName().equals(methodName)){
						XposedBridge.hookMethod(method, xcMethodHook);
					}
				}
			} catch (Throwable ex) {
				Util.bug(null, ex);
			}
	}
	
	private static void after(MethodHookParam param){
		logMethod(param);
	}
	
	private static void logMethod(MethodHookParam param){
		boolean isOutputJson = false;
		String settingValue = LogManager.getSetting(Util.JSON_OUTPUT_SETTING);
		if(settingValue != null && settingValue.equals("true")){
			isOutputJson = true;
		}
		
		Method method = (Method) param.method;
		Class<?>[] argTypes = method.getParameterTypes();
		String[] argTypeNames = new String[argTypes.length];
		for(int i=0; i<argTypes.length; i++)
			argTypeNames[i] = argTypes[i].getName();
		
		String argsValue = Parser.parseParameters(argTypeNames, param.args, isOutputJson);
		
		Object returnObject = param.getResult();
		String returnTypeName = "null";
		String returnValue = "null";
		if(returnObject != null){
			returnTypeName = method.getReturnType().getName();
			returnValue = Parser.parseReturnValue(returnTypeName, returnObject);
		}
		
		String formattedRes = null;
		if(isOutputJson)
			formattedRes = String.format("{\"className\":\"%s\", \"methodName\":\"%s\", \"arguments\":[%s], \"returnType\":\"%s\", \"returnValue\":\"%s\"}", 
					method.getDeclaringClass().getName(), method.getName(), argsValue, returnTypeName, returnValue);
		else
			formattedRes = String.format("%s.%s(%s) %s", method.getDeclaringClass().getName(), 
				method.getName(), argsValue, returnValue);
		
		Log.i(Util.LOG_TAG, formattedRes);
	}
}
