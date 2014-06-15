package com.mindmac.applog.ui;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


import com.mindmac.applog.R;
import com.mindmac.applog.adapter.AppInfoAdapter;
import com.mindmac.applog.util.AppInfoEx;
import com.mindmac.applog.util.MenuOptionExecutor;
import com.mindmac.applog.util.Util;
import com.mindmac.applog.util.Util.ShowStatus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;


public class AppListActivity extends Activity{
	private AppInfoAdapter mAppInfoAdapter = null;
	private ShowStatus mCurrentShowStatus = ShowStatus.UserApps;
	private Menu mMenu = null;
	
	
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
        if(!Util.isAndroidAppLogEnabled()){
        	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AppListActivity.this);
			alertDialogBuilder.setTitle(R.string.app_name);
			alertDialogBuilder.setMessage(R.string.msg_enable_xposed);
			alertDialogBuilder.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
			alertDialogBuilder.setPositiveButton(getString(android.R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
			});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
        }
        
        setContentView(R.layout.app_list);
        
        // Get and show app info
        AppListTask appListTask = new AppListTask();
        appListTask.executeOnExecutor(mExecutor, (Object) null);
        
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_app, menu);
		
		mMenu = menu;
		menu.findItem(R.id.menu_user_apps).setEnabled(false);
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Context context = AppListActivity.this;
		switch (item.getItemId()) {
			case R.id.menu_refresh:
				AppListTask appListTask = new AppListTask();
		        appListTask.executeOnExecutor(mExecutor, (Object) null);
				return true;
			case R.id.menu_output:
				String menuStr = (String) item.getTitle();
				if(menuStr.equals(context.getString(R.string.menu_json)))
					item.setTitle(R.string.menu_default);
				else if(menuStr.equals(context.getString(R.string.menu_default)))
					item.setTitle(R.string.menu_json);
				MenuOptionExecutor.optionOutput(context, menuStr);
				return true;
			case R.id.menu_all_apps:
				// Update only the adapter data may be a better way
				this.mCurrentShowStatus = ShowStatus.AllApps;
				AppListTask appListTaskAll = new AppListTask();
		        appListTaskAll.executeOnExecutor(mExecutor, (Object) null);
		        item.setEnabled(false);
		        mMenu.findItem(R.id.menu_system_apps).setEnabled(true);
		        mMenu.findItem(R.id.menu_user_apps).setEnabled(true);
		        return true;
			case R.id.menu_system_apps:
				this.mCurrentShowStatus = ShowStatus.SystemApps;
				AppListTask appListTaskSystem = new AppListTask();
		        appListTaskSystem.executeOnExecutor(mExecutor, (Object) null);
		        item.setEnabled(false);
		        mMenu.findItem(R.id.menu_all_apps).setEnabled(true);
		        mMenu.findItem(R.id.menu_user_apps).setEnabled(true);
		        return true;
			case R.id.menu_user_apps:
				this.mCurrentShowStatus = ShowStatus.UserApps;
				AppListTask appListTaskUser = new AppListTask();
		        appListTaskUser.executeOnExecutor(mExecutor, (Object) null);
		        item.setEnabled(false);
		        mMenu.findItem(R.id.menu_system_apps).setEnabled(true);
		        mMenu.findItem(R.id.menu_all_apps).setEnabled(true);
		        return true;
			case R.id.menu_about:
				MenuOptionExecutor.optionAbout(context);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	
    // AppInfo task
    private class AppListTask extends AsyncTask<Object, Integer, List<AppInfoEx>> {
		private ProgressDialog mProgressDialog;

		@Override
		protected List<AppInfoEx> doInBackground(Object... params) {
			// Get app info list
			return Util.getAppInfoExList(AppListActivity.this, mCurrentShowStatus, mProgressDialog);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			// Show progress dialog
			ListView lvApp = (ListView) findViewById(R.id.lvApp);
			mProgressDialog = new ProgressDialog(lvApp.getContext());
			mProgressDialog.setMessage(getString(R.string.msg_loading));
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setProgressNumberFormat(null);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		}

		@Override
		protected void onPostExecute(List<AppInfoEx> appInfoExList) {
			super.onPostExecute(appInfoExList);

			// Display app list
			mAppInfoAdapter = new AppInfoAdapter(AppListActivity.this, R.layout.app_item, appInfoExList);
			ListView lvApp = (ListView) findViewById(R.id.lvApp);
			lvApp.setAdapter(mAppInfoAdapter);

			// Dismiss progress dialog
			try {
				mProgressDialog.dismiss();
			} catch (Throwable ex) {
				Util.bug(null, ex);
			}
		}
	}

 

}
