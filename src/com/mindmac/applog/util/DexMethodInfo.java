package com.mindmac.applog.util;

import java.util.ArrayList;

public class DexMethodInfo implements Comparable<DexMethodInfo>{
	private String pacakgeName = null;
	private String className = null;
	private String methodName = null;
	private ArrayList<String> methodSignatures = new ArrayList<String>();
	
	public DexMethodInfo(String packageName, String className, String methodName){
		this.pacakgeName = packageName;
		this.className = className;
		this.methodName = methodName;
	}
	
	public ArrayList<String> getMethodSignatures() {
		return methodSignatures;
	}
	
	public void addMethodSignature(String methodSignature) {
		methodSignatures.add(methodSignature);
	}

	
	public String getPacakgeName() {
		return pacakgeName;
	}

	public void setPacakgeName(String pacakgeName) {
		this.pacakgeName = pacakgeName;
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	@Override
	public int compareTo(DexMethodInfo another) {
		return getMethodName().compareToIgnoreCase(another.getMethodName());
	}


	
}
