package com.mindmac.applog.service;

import java.util.ArrayList;
import java.util.List;

import com.mindmac.applog.util.Util;

import android.text.TextUtils;

public class Parser {
	public static String parseParameters(String[] argTypeNames, Object[] argObjects, boolean isOutputJson){
		List<String> formattedArgsList = new ArrayList<String>();
		int argLength = argObjects.length;
		
		for(int i=0; i<argLength; i++){
			String argTypeName = argTypeNames[i];
			String argValueStr = "null";
			if(argObjects[i] != null)
				argValueStr = parseObject(argTypeName, argObjects[i]);
			if(isOutputJson)
				formattedArgsList.add(String.format("{\"type\":\"%s\", \"content\":\"%s\"}", 
						argTypeName, argValueStr));
			else
				formattedArgsList.add(argTypeName + " " + argValueStr);
			
		}
		
		String formattedArgsStr = TextUtils.join(", ", formattedArgsList.toArray());		
		return formattedArgsStr;
	}
	
	public static String parseReturnValue(String typeName, Object returnObject){
		String returnValue = parseObject(typeName, returnObject);
		return returnValue;
	}
	
	private static String parseObject(String typeName, Object object){
		String valueStr = "";
		if(typeName.startsWith("[")){
			// Parse array
			valueStr = parseArrayArg(object, typeName);
		}else{
			// Parse non-array
			valueStr = parseArg(object, typeName);
		}
		return valueStr;
	}
	
	// Parse Array Argument
	private static String parseArrayArg(Object arg, String typeName){
		if(typeName.startsWith("[B"))
			return parseArrayArg((byte[]) arg);
		if(typeName.startsWith("[S"))
			return parseArrayArg((short[]) arg);
		if(typeName.startsWith("[I"))
			return parseArrayArg((int[]) arg);
		if(typeName.startsWith("[J"))
			return parseArrayArg((long[]) arg);
		if(typeName.startsWith("[F"))
			return parseArrayArg((float[]) arg);
		if(typeName.startsWith("[D"))
			return parseArrayArg((double[]) arg);
		if(typeName.startsWith("[C"))
			return parseArrayArg((char[]) arg);
		if(typeName.startsWith("[Z"))
			return parseArrayArg((boolean[]) arg);
		else
			return parseArrayArg((Object[]) arg);
		
	}
	
	// Parse Argument
	private static String parseArg(Object arg, String typeName){
		
		return parseArg(arg);
		
	}
	
	private static String parseArrayArg(Object[] argArray){
		List<String> argValueList = new ArrayList<String>();
		
		Object tmpObject = null;
		int length = argArray.length;
		
		for(int i=0; i<length; i++){
			try{
				tmpObject = argArray[i];
				if(tmpObject == null)
					argValueList.add("null");
				else
					argValueList.add(tmpObject.toString());
			}catch(UnsupportedOperationException ex){
				argValueList.add("");
			}
		}
		return parseArrayArg(argValueList);

	}
	
	private static String parseArrayArg(byte[] argArray){
		List<Byte> argValueList = new ArrayList<Byte>();
		int length = argArray.length;
		
		for(int i=0; i<length; i++)
			argValueList.add(argArray[i]);
		
		return parseArrayArg(argValueList);
	
	}
	
	private static String parseArrayArg(short[] argArray){
		List<Short> argValueList = new ArrayList<Short>();
		int length = argArray.length;
		
		for(int i=0; i<length; i++)
			argValueList.add(argArray[i]);
		return parseArrayArg(argValueList);
	}
	
	private static String parseArrayArg(int[] argArray){
		List<Integer> argValueList = new ArrayList<Integer>();
		int length = argArray.length;
		
		for(int i=0; i<length; i++)
			argValueList.add(argArray[i]);
		return parseArrayArg(argValueList);
	}
	
	private static String parseArrayArg(long[] argArray){
		List<Long> argValueList = new ArrayList<Long>();
		int length = argArray.length;
		
		for(int i=0; i<length; i++)
			argValueList.add(argArray[i]);
		return parseArrayArg(argValueList);
	}
	
	private static String parseArrayArg(float[] argArray){
		List<Float> argValueList = new ArrayList<Float>();
		int length = argArray.length;
		
		for(int i=0; i<length; i++)
			argValueList.add(argArray[i]);
		return parseArrayArg(argValueList);
	}
	
	private static String parseArrayArg(double[] argArray){
		List<Double> argValueList = new ArrayList<Double>();
		int length = argArray.length;
		
		for(int i=0; i<length; i++)
			argValueList.add(argArray[i]);
		return parseArrayArg(argValueList);
	}
	
	private static String parseArrayArg(char[] argArray){
		List<Character> argValueList = new ArrayList<Character>();
		int length = argArray.length;
		
		for(int i=0; i<length; i++)
			argValueList.add(argArray[i]);
		return parseArrayArg(argValueList);
	}
	
	private static String parseArrayArg(boolean[] argArray){
		List<Boolean> argValueList = new ArrayList<Boolean>();
		int length = argArray.length;
		
		for(int i=0; i<length; i++)
			argValueList.add(argArray[i]);
		return parseArrayArg(argValueList);
	}
	
	private static String parseArrayArg(List<?> argValueList){
		String argValueStr = "";
		argValueStr = TextUtils.join(", ", argValueList);
		argValueStr = String.format("{ %s }", argValueStr);
		
		return argValueStr;
	}
	

	private static String parseArg(Object arg){
		String argValue = "";
		try{
			argValue = arg.toString();
		}catch(UnsupportedOperationException ex){
			// Do noting
		}
		
		return argValue;
	}
}
