package com.mindmac.applog.adapter;

import java.util.ArrayList;
import java.util.List;

import com.mindmac.applog.R;
import com.mindmac.applog.ui.ClassActivity;
import com.mindmac.applog.ui.PackageActivity;
import com.mindmac.applog.util.DexPackageInfo;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DexPackageInfoAdapter extends ArrayAdapter<DexPackageInfo>{
	
	private List<DexPackageInfo> mDexPackageInfoList;
	private LayoutInflater mInflater = null;
	private int mLayoutResource;
	private Context mContext;
	private String mAppPackageName = null;
	

	public DexPackageInfoAdapter(Context context, int resource, List<DexPackageInfo> objects, String appPackageName) {
		super(context, resource, objects);
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayoutResource = resource;
		mDexPackageInfoList = new ArrayList<DexPackageInfo>();
		mDexPackageInfoList.addAll(objects);
		mAppPackageName = appPackageName;
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

		// Get DexPackageInfo
		DexPackageInfo dexPackageInfo = getItem(holder.position);

		// Set icon
		holder.ivPackageIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.java_package));
		holder.ivPackageIcon.setVisibility(View.VISIBLE);
		
		// Set package name
		holder.tvPackageName.setText(dexPackageInfo.getPackageName());

		// Handle click
		holder.rlPackage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				DexPackageInfo dexPackageInfo = getItem(holder.position);
				if(dexPackageInfo != null){
					Intent intent = new Intent();
					String dexPackageName = dexPackageInfo.getPackageName();
					intent.putExtra(ClassActivity.DEX_PACKAGE_EXTRA, dexPackageName);
					intent.putExtra(PackageActivity.APP_PACKAGE_EXTRA, mAppPackageName);
					intent.setClass(mContext, ClassActivity.class);
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
		public RelativeLayout rlPackage;
		public ImageView ivPackageIcon;
		public TextView tvPackageName;
		
		public ViewHolder(View theRow, int thePosition) {
			row = theRow;
			position = thePosition;
			rlPackage = (RelativeLayout) row.findViewById(R.id.rlPackage);
			ivPackageIcon = (ImageView) row.findViewById(R.id.ivPackageIcon);
			tvPackageName = (TextView) row.findViewById(R.id.tvPackageName);
		}
	}
}
