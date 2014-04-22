package com.mindmac.applog.ui;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.mindmac.applog.R;
import com.mindmac.applog.adapter.DexPackageInfoAdapter;
import com.mindmac.applog.util.AppInfoEx;
import com.mindmac.applog.util.DexInfo;
import com.mindmac.applog.util.DexPackageInfo;
import com.mindmac.applog.util.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

public class PackageActivity extends Activity{
	public static final String APP_PACKAGE_EXTRA = "app_package";
	
	private String mAppPackageName = null;
	private DexPackageInfoAdapter mDexPackageInfoAdapter = null;
	
	private static ExecutorService mExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
			new PriorityThreadFactory());

	private static class PriorityThreadFactory implements ThreadFactory {
		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.package_list);
        // Get package name
        Bundle extras = getIntent().getExtras();
        mAppPackageName = (extras.containsKey(APP_PACKAGE_EXTRA) ? extras.getString(APP_PACKAGE_EXTRA) : null);
        
	    if(mAppPackageName != null){
	    	// Set action bar
	    	AppInfoEx appInfoEx = new AppInfoEx(PackageActivity.this, mAppPackageName);
	    	setTitle(appInfoEx.getAppName());
	    	getActionBar().setIcon(appInfoEx.getAppIcon());
	    	
	        // Get and show package info
	        PackageListTask packageListTask = new PackageListTask();
	        packageListTask.executeOnExecutor(mExecutor, (Object) null);
	    }
	    
	    
        
    }
    
    // Package information task
    private class PackageListTask extends AsyncTask<Object, Integer, List<DexPackageInfo>> {
		private ProgressDialog mProgressDialog;

		@Override
		protected List<DexPackageInfo> doInBackground(Object... params) {
			DexInfo.buildDexInfo(PackageActivity.this, mAppPackageName, mProgressDialog);
			return DexInfo.getDexPackageInfoList();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			// Show progress dialog
			ListView lvPackage = (ListView) findViewById(R.id.lvPackage);
			mProgressDialog = new ProgressDialog(lvPackage.getContext());
			mProgressDialog.setMessage(getString(R.string.msg_loading));
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setProgressNumberFormat(null);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		}

		@Override
		protected void onPostExecute(List<DexPackageInfo> dexPackageInfoList) {
			super.onPostExecute(dexPackageInfoList);

			// Display package name list
			mDexPackageInfoAdapter = new DexPackageInfoAdapter(PackageActivity.this, 
					R.layout.package_item, dexPackageInfoList, mAppPackageName);
			ListView lvApp = (ListView) findViewById(R.id.lvPackage);
			lvApp.setAdapter(mDexPackageInfoAdapter);

			// Dismiss progress dialog
			try {
				mProgressDialog.dismiss();
			} catch (Throwable ex) {
				Util.bug(null, ex);
			}
		}
	}
}
