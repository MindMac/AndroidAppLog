package com.mindmac.applog.util;

import java.util.ArrayList;

import com.mindmac.applog.R;
import com.mindmac.applog.adapter.DexMethodInfoAdapter;
import com.mindmac.applog.service.LogManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.Window;


public class MenuOptionExecutor {
	
	public static void optionAbout(Context context) {
		// About
		Dialog dlgAbout = new Dialog(context);
		dlgAbout.requestWindowFeature(Window.FEATURE_LEFT_ICON);
		dlgAbout.setTitle(R.string.menu_about);
		dlgAbout.setContentView(R.layout.about);
		dlgAbout.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_launcher);
		
		dlgAbout.setCancelable(true);
		dlgAbout.show();
	}
		
	public static void optionLaunch(Context context, String packageName) {
		Intent intentLaunch = context.getPackageManager().getLaunchIntentForPackage(packageName);
		context.startActivity(intentLaunch);
	}
	
	@SuppressLint("InlinedApi")
	public static void optionInfo(Context context, String packageName) {
		Intent intentSettings = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
				Uri.parse("package:" + packageName));
		context.startActivity(intentSettings);
	}
	
	public static void optionClearAll(final Context context, final int uid, final String className, 
			final DexMethodInfoAdapter dexMethodInfoAdapter) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle(context.getString(R.string.menu_clear_all));
		alertDialogBuilder.setMessage(context.getString(R.string.msg_sure));
		alertDialogBuilder.setIcon(R.drawable.ic_launcher);
		alertDialogBuilder.setPositiveButton(context.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				LogManager.deleteLogRecordByClass(uid, className);
				// Refresh display
				if (dexMethodInfoAdapter != null)
					dexMethodInfoAdapter.notifyDataSetChanged();
			}
		});
		alertDialogBuilder.setNegativeButton(context.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	public static void optionCheckAll(final Context context, final int uid, final DexClassInfo dexClassInfo, 
			final DexMethodInfoAdapter dexMethodInfoAdapter) {
		ArrayList<DexMethodInfo> dexMethodInfoList = dexClassInfo.getChildDexMethodInfos();
		for(DexMethodInfo dexMethodInfo : dexMethodInfoList){
			String className = null;
			if(!dexMethodInfo.getPacakgeName().equals(Util.DEFAULT_PACKAGE))
				className = String.format("%s.%s", dexMethodInfo.getPacakgeName(), dexMethodInfo.getClassName());
			else
				className = dexMethodInfo.getClassName();
			LogManager.setLogEnable(uid, className, dexMethodInfo.getMethodName(), true);
		}

		// Refresh display
		if (dexMethodInfoAdapter != null)
			dexMethodInfoAdapter.notifyDataSetChanged();
	}
}

