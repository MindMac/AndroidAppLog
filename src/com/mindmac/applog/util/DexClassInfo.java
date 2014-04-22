package com.mindmac.applog.util;

import java.util.ArrayList;

public class DexClassInfo implements Comparable<DexClassInfo>{
	private String packageName = null;
	private String className = null;
	private ArrayList<DexMethodInfo> childDexMethodInfos = new ArrayList<DexMethodInfo>();
	
	public DexClassInfo(String packageName, String className){
		this.packageName = packageName;
		this.className = className;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	public ArrayList<DexMethodInfo> getChildDexMethodInfos() {
		return childDexMethodInfos;
	}

	public void addChildDexMethodInfo(DexMethodInfo dexMethodInfo){
		if(dexMethodInfo != null)
			childDexMethodInfos.add(dexMethodInfo);
	}

	@Override
	public int compareTo(DexClassInfo another) {
		return getClassName().compareToIgnoreCase(another.getClassName());
	}

}
