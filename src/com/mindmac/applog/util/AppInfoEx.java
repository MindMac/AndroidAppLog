package com.mindmac.applog.util;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

public class AppInfoEx implements Comparable<AppInfoEx>{

	private String mPackageName;
	private String mAppName;
	private boolean mIsSystem;
	private Drawable mAppIcon;
	private int mUid = 0;
	
	
	public AppInfoEx(Context context, String packageName) {
		// Get app info
		try {
			ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(packageName, 
					PackageManager.GET_META_DATA);
			this.createCustomAppInfo(context, appInfo);
		} catch (NameNotFoundException ex) {
			// TODO Auto-generated catch block
			Util.bug(null, ex);
		}	
	}

	public AppInfoEx(Context context, ApplicationInfo appInfo){
		this.createCustomAppInfo(context, appInfo);
	}
	
	
	private void createCustomAppInfo(Context context, ApplicationInfo appInfo) {
		PackageManager pm = context.getPackageManager();

		mPackageName = appInfo.packageName;
		// mAppName maybe null
		mAppName = (String) pm.getApplicationLabel(appInfo);
		
		mUid = appInfo.uid;
		
		mIsSystem = ((appInfo.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0);

		// Get icon
		try {
			mAppIcon = pm.getApplicationInfo(mPackageName, PackageManager.GET_META_DATA).loadIcon(pm);
		} catch (NameNotFoundException ex) {
			// TODO Auto-generated catch block
			Util.bug(null, ex);
		}
	
	}
	
	public String getPackageName(){
		return mPackageName;
	}
	
	
	public String getAppName(){
		return mAppName==null ? "UnKnown" : mAppName;
	}
	
	
	public boolean isSystem(){
		return mIsSystem;
	}
	
	public Drawable getAppIcon(){
		return mAppIcon;
	}
	
	
	public int getUid(){
		return mUid;
	}
	
	@Override
	public int compareTo(AppInfoEx another) {
		// TODO Auto-generated method stub
		return getAppName().compareToIgnoreCase(another.getAppName());
	}
	


}
