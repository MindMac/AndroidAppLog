package com.mindmac.applog.adapter;

import java.util.ArrayList;
import java.util.List;

import com.mindmac.applog.R;
import com.mindmac.applog.ui.PackageActivity;
import com.mindmac.applog.util.AppInfoEx;
import com.mindmac.applog.util.MenuOptionExecutor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AppInfoAdapter extends ArrayAdapter<AppInfoEx>{
	
	private List<AppInfoEx> mAppInfoExList;
	private LayoutInflater mInflater = null;
	private int mLayoutResource;
	private Context mContext;

	public AppInfoAdapter(Context context, int resource, List<AppInfoEx> objects) {
		super(context, resource, objects);
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayoutResource = resource;
		mAppInfoExList = new ArrayList<AppInfoEx>();
		mAppInfoExList.addAll(objects);
	}
	
	public void setAppInfoExList(List<AppInfoEx> appInfoExList){
		this.mAppInfoExList = appInfoExList;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(mLayoutResource, null);
			holder = new ViewHolder(convertView, position);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			holder.position = position;
		}

		// Get AppInfoEx
		final AppInfoEx appInfoEx = getItem(holder.position);

		// Set app icon
		holder.ivAppIcon.setImageDrawable(appInfoEx.getAppIcon());
		holder.ivAppIcon.setVisibility(View.VISIBLE);
		
		// Set app name
		holder.tvAppName.setText(appInfoEx.getAppName());
		
		// Handle click event
		holder.tvAppName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MenuOptionExecutor.optionLaunch(mContext, appInfoEx.getPackageName());
			}
		});
		
		holder.rlApp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(appInfoEx != null){
					String appPackageName = appInfoEx.getPackageName();
					Intent intent = new Intent();
					intent.putExtra(PackageActivity.APP_PACKAGE_EXTRA, appPackageName);
					intent.setClass(mContext, PackageActivity.class);
					mContext.startActivity(intent);
				}
			}
		});
		
		return convertView;
	}

	// ViewHolder
	private class ViewHolder{
		private View row;
		private int position;
		public RelativeLayout rlApp;
		public ImageView ivAppIcon;
		public TextView tvAppName;
		
		
		public ViewHolder(View theRow, int thePosition) {
			row = theRow;
			position = thePosition;;
			rlApp = (RelativeLayout) row.findViewById(R.id.rlApp);
			ivAppIcon = (ImageView) row.findViewById(R.id.ivAppIcon);
			tvAppName = (TextView) row.findViewById(R.id.tvAppName);
		}
	}
}
