package com.mindmac.applog.ui;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.mindmac.applog.R;
import com.mindmac.applog.adapter.DexMethodInfoAdapter;
import com.mindmac.applog.util.AppInfoEx;
import com.mindmac.applog.util.DexClassInfo;
import com.mindmac.applog.util.DexInfo;
import com.mindmac.applog.util.DexMethodInfo;
import com.mindmac.applog.util.MenuOptionExecutor;
import com.mindmac.applog.util.Util;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

public class MethodActivity extends Activity{
	public static final String DEX_CLASS_EXTRA = "dex_class";
	
	private String mAppPackageName = null;
	private String mDexPackageName = null;
	private String mDexClassName = null;
	
	private int mUid = -1;
	
	private DexMethodInfoAdapter mDexMethodInfoAdapter = null;
	
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
        setContentView(R.layout.method_list);
        // Get dex method name
        Bundle extras = getIntent().getExtras();
        mAppPackageName = (extras.containsKey(PackageActivity.APP_PACKAGE_EXTRA) ?
        		extras.getString(PackageActivity.APP_PACKAGE_EXTRA) : null);
	    mDexPackageName = (extras.containsKey(ClassActivity.DEX_PACKAGE_EXTRA) ? 
	    		extras.getString(ClassActivity.DEX_PACKAGE_EXTRA) : null);
	    mDexClassName = (extras.containsKey(DEX_CLASS_EXTRA) ? 
	    		extras.getString(DEX_CLASS_EXTRA) : null);
	    if(mDexPackageName != null && mDexClassName != null){
	    	// Set action bar
	    	AppInfoEx appInfoEx = new AppInfoEx(MethodActivity.this, mAppPackageName);
	    	getActionBar().setIcon(appInfoEx.getAppIcon());
	    	setTitle(String.format("%s-%s.%s", appInfoEx.getAppName(), mDexPackageName, mDexClassName));
	    	mUid = appInfoEx.getUid();
	    	
	        // Get and show method info
	    	MethodListTask methodListTask = new MethodListTask();
	    	methodListTask.executeOnExecutor(mExecutor, (Object) null);
	    }
        
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.method_menu, menu);
		
		return true;
	}
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		PackageManager pm = getPackageManager();
				
		// Launch
		MenuItem launch = menu.findItem(R.id.menu_launch);
		if (pm.getLaunchIntentForPackage(mAppPackageName) == null)
			launch.setEnabled(false);

		return super.onPrepareOptionsMenu(menu);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Context context = MethodActivity.this;
		
		switch (item.getItemId()) {
			case R.id.menu_launch:
				MenuOptionExecutor.optionLaunch(context, mAppPackageName);
				return true;
			case R.id.menu_info:
				MenuOptionExecutor.optionInfo(context, mAppPackageName);
				return true;
			case R.id.menu_clear_all:
				String className = null;
				className = mDexPackageName.equals(Util.DEFAULT_PACKAGE) ? mDexClassName : 
					String.format("%s.%s", mDexPackageName, mDexClassName);
				MenuOptionExecutor.optionClearAll(context, mUid, className, mDexMethodInfoAdapter);
				return true;
			case R.id.menu_check_all:
				DexClassInfo dexClassInfo = DexInfo.getDexClassByName(mDexPackageName, mDexClassName);
				MenuOptionExecutor.optionCheckAll(context, mUid, dexClassInfo, mDexMethodInfoAdapter);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
    // Method information task
    private class MethodListTask extends AsyncTask<Object, Integer, List<DexMethodInfo>> {
		@Override
		protected List<DexMethodInfo> doInBackground(Object... params) {
			return DexInfo.getDexMethodsByClass(mDexPackageName, mDexClassName);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(List<DexMethodInfo> dexMethodInfoList) {
			super.onPostExecute(dexMethodInfoList);

			// Display method name list
			mDexMethodInfoAdapter = new DexMethodInfoAdapter(MethodActivity.this, 
					R.layout.method_item, dexMethodInfoList, mUid);
			ListView lvMethod = (ListView) findViewById(R.id.lvMethod);
			lvMethod.setAdapter(mDexMethodInfoAdapter);
		}
	}
}
