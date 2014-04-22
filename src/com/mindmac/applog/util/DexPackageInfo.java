package com.mindmac.applog.util;

import java.util.ArrayList;

public class DexPackageInfo implements Comparable<DexPackageInfo>{
	private String packageName = null;
	private ArrayList<DexClassInfo> childDexClassInfos = new ArrayList<DexClassInfo>();
	
	public DexPackageInfo(String packageName){
		this.packageName = packageName;
	}
	
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public ArrayList<DexClassInfo> getChildDexClassInfos() {
		return childDexClassInfos;
	}
	
	public void addChildDexClassInfo(DexClassInfo dexClassInfo){
		if(dexClassInfo != null)
			childDexClassInfos.add(dexClassInfo);
	}

	@Override
	public int compareTo(DexPackageInfo another) {
		return getPackageName().compareToIgnoreCase(another.getPackageName());
	}
}
