package com.mindmac.applog.ui;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.mindmac.applog.R;
import com.mindmac.applog.adapter.DexClassInfoAdapter;
import com.mindmac.applog.util.AppInfoEx;
import com.mindmac.applog.util.DexClassInfo;
import com.mindmac.applog.util.DexInfo;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

public class ClassActivity extends Activity{
	public static final String DEX_PACKAGE_EXTRA = "dex_package";
	
	private String mDexPackageName = null;
	private String mAppPackageName = null;
	private DexClassInfoAdapter mDexClassInfoAdapter = null;
	
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
        setContentView(R.layout.class_list);
        // Get dex package name
        Bundle extras = getIntent().getExtras();
	    mDexPackageName = (extras.containsKey(DEX_PACKAGE_EXTRA) ? extras.getString(DEX_PACKAGE_EXTRA) : null);
	    mAppPackageName = (extras.containsKey(PackageActivity.APP_PACKAGE_EXTRA) ? 
	    		extras.getString(PackageActivity.APP_PACKAGE_EXTRA) : null);
	    if(mDexPackageName != null){
	    	// Set action bar
	    	AppInfoEx appInfoEx = new AppInfoEx(ClassActivity.this, mAppPackageName);
	    	getActionBar().setIcon(appInfoEx.getAppIcon());
	    	setTitle(String.format("%s-%s", appInfoEx.getAppName(), mDexPackageName));
	        // Get and show class info
	    	ClassListTask classListTask = new ClassListTask();
	    	classListTask.executeOnExecutor(mExecutor, (Object) null);
	    }
        
    }
    
    // Class information task
    private class ClassListTask extends AsyncTask<Object, Integer, List<DexClassInfo>> {
		@Override
		protected List<DexClassInfo> doInBackground(Object... params) {
			return DexInfo.getDexClassesByPackage(mDexPackageName);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(List<DexClassInfo> dexClassInfoList) {
			super.onPostExecute(dexClassInfoList);

			// Display class name list
			mDexClassInfoAdapter = new DexClassInfoAdapter(ClassActivity.this, 
					R.layout.class_item, dexClassInfoList, mAppPackageName);
			ListView lvClass = (ListView) findViewById(R.id.lvClass);
			lvClass.setAdapter(mDexClassInfoAdapter);
		}
	}
}
