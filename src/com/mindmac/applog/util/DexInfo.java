package com.mindmac.applog.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.Method;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;


public class DexInfo {
	private static HashMap<String, DexPackageInfo> mDexPackageMap = new HashMap<String, DexPackageInfo>();
	
	private static void init(){
		mDexPackageMap.clear();
	}
	
	
	private static void addToDexPackageMap(String packageName, DexClassInfo dexClassInfo){
		DexPackageInfo dexPackageInfo = null;
		if(mDexPackageMap.containsKey(packageName)){
			dexPackageInfo = mDexPackageMap.get(packageName);
		}else{
			dexPackageInfo = new DexPackageInfo(packageName);
			mDexPackageMap.put(packageName, dexPackageInfo);
		}
		
		dexPackageInfo.addChildDexClassInfo(dexClassInfo);
	}
	
	public static void buildDexInfo(Context context, String appPackageName, ProgressDialog progressDialog){
		init();
		
		ApplicationInfo appInfo;
		try {
			appInfo = context.getPackageManager().
					getApplicationInfo(appPackageName, PackageManager.GET_META_DATA);
		} catch (NameNotFoundException ex) {
			Util.bug(null, ex);
			return;
		}
		
		int targetSdkVersion = appInfo.targetSdkVersion;
		String apkPath = appInfo.sourceDir;
		
		File apkFile = new File(apkPath);
		if(apkFile.exists()){
			DexBackedDexFile dexFile = null;
			try{
				dexFile = DexFileFactory.loadDexFile(apkFile.getAbsolutePath(), targetSdkVersion);
			}catch(IOException ex){
				Util.bug(null, ex);
				return;
			}
			
			// Sort
			List<? extends ClassDef> classDefs = Ordering.natural().sortedCopy(dexFile.getClasses());
			progressDialog.setMax(classDefs.size());
			
			int threadNum = Runtime.getRuntime().availableProcessors();
	        if (threadNum > 8) {
	        	 threadNum = 8;
	        }
	        
			ExecutorService executor = Executors.newFixedThreadPool(threadNum);
		    List<Future<DexClassInfo>> tasks = Lists.newArrayList();
		    for (final ClassDef classDef: classDefs) {
	            tasks.add(executor.submit(new Callable<DexClassInfo>() {
	                @Override 
	                public DexClassInfo call() throws Exception {
	                    return buildDexClassInfo(classDef);
	                }
	            }));
	        }
		    
		    int i = 0;
		    try {
	            for (Future<DexClassInfo> task: tasks) {
	            	i += 1;
	            	progressDialog.setProgress(i);
	                while(true) {
	                    try {
	                    	DexClassInfo dexClassInfo = task.get();
	                    	if(dexClassInfo != null){
		                    	String packageName = dexClassInfo.getPackageName();
		                    	addToDexPackageMap(packageName, dexClassInfo);
	                    	}
	                    	
	                    } catch (InterruptedException ex) {
	                        continue;
	                    } catch (ExecutionException ex) {
	                        throw new RuntimeException(ex);
	                    }
	                    break;
	                }
	            }
	        } finally {
	            executor.shutdown();
	        }
		}
	}
	
	private static DexClassInfo buildDexClassInfo(ClassDef classDef){
		DexClassInfo dexClassInfo = null;
		String classDescriptor = classDef.getType();

        // Validate
        if (classDescriptor.charAt(0) != 'L' ||
                classDescriptor.charAt(classDescriptor.length()-1) != ';') {
            Util.log(null, "Unrecognized class descriptor: " + classDescriptor);
            return null;
        }
		
        String[] packageClassName = Util.getPacakgeClassName(classDescriptor);
        String packageName = packageClassName[0];
        if(packageName == null || packageName.equals(""))
        	packageName = Util.DEFAULT_PACKAGE;
        // Check if excluded
        for(String excludedPackage : Util.cExcludedClasses){
        	if(packageName.startsWith(excludedPackage))
        		return null;
        }
        String className = packageClassName[1];
        dexClassInfo = new DexClassInfo(packageName, className);
        // Record the method name to avoid recording the same method name multiple times
        HashMap<String, Integer> methodMap = new HashMap<String, Integer>();
        for(Method method : classDef.getMethods()){
        	String methodName = method.getName();
        	if(!methodMap.containsKey(methodName)){
        		methodMap.put(methodName, 1);
        		DexMethodInfo dexMethodInfo = new DexMethodInfo(packageName, className, methodName);
        		dexClassInfo.addChildDexMethodInfo(dexMethodInfo);
        	}
	    }
		
		return dexClassInfo;
		
	}
	
	public static List<DexPackageInfo> getDexPackageInfoList(){
		List<DexPackageInfo> dexPackageInfoList = new ArrayList<DexPackageInfo>();
		for(String packageName : mDexPackageMap.keySet())
			dexPackageInfoList.add(mDexPackageMap.get(packageName));
		
		// Sort
		Collections.sort(dexPackageInfoList);
		return dexPackageInfoList;
	}
	
	public static DexPackageInfo getDexPackageByName(String packageName){
		return mDexPackageMap.get(packageName);
	}
	
	public static ArrayList<DexClassInfo> getDexClassesByPackage(String packageName){
		ArrayList<DexClassInfo> dexClassInfoList = new ArrayList<DexClassInfo>();
		DexPackageInfo dexPackageInfo = getDexPackageByName(packageName);
		if(dexPackageInfo != null){
			dexClassInfoList = dexPackageInfo.getChildDexClassInfos();
		}
		// Sort
		Collections.sort(dexClassInfoList);
		return dexClassInfoList;
	}
	
	
	public static DexClassInfo getDexClassByName(String packageName, String className){
		DexClassInfo dexClassInfo = null;
		List<DexClassInfo> dexClassInfos = getDexClassesByPackage(packageName);
		for(DexClassInfo dexClassInfoTmp : dexClassInfos){
			if(dexClassInfoTmp.getClassName().equals(className)){
				dexClassInfo = dexClassInfoTmp;
				break;
			}
			
		}
		
		return dexClassInfo;
	}
	
	public static ArrayList<DexMethodInfo> getDexMethodsByClass(String packageName, String className){
		ArrayList<DexMethodInfo> dexMethodInfoList = new ArrayList<DexMethodInfo>();
		DexClassInfo dexClassInfo = getDexClassByName(packageName, className);
		if(dexClassInfo != null){
			dexMethodInfoList = dexClassInfo.getChildDexMethodInfos();
		}
		
		// Sort
		Collections.sort(dexMethodInfoList);
		return dexMethodInfoList;
	}
	
	public static DexMethodInfo getDexMethodByName(String packageName, String className, String methodName){
		DexMethodInfo dexMethodInfo = null;
		List<DexMethodInfo> dexMethodInfos = getDexMethodsByClass(packageName, className);
		
		for(DexMethodInfo dexMethodInfoTmp : dexMethodInfos){
			if(dexMethodInfoTmp.getMethodName().equals(methodName)){
				dexMethodInfo = dexMethodInfoTmp;
				break;
			}
			
		}
		
		return dexMethodInfo;
	}

}
