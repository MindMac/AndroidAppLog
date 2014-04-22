package com.mindmac.applog.adapter;

import java.util.ArrayList;
import java.util.List;

import com.mindmac.applog.R;
import com.mindmac.applog.ui.ClassActivity;
import com.mindmac.applog.ui.MethodActivity;
import com.mindmac.applog.ui.PackageActivity;
import com.mindmac.applog.util.DexClassInfo;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DexClassInfoAdapter extends ArrayAdapter<DexClassInfo>{
	
	private List<DexClassInfo> mDexClassInfoList;
	private LayoutInflater mInflater = null;
	private int mLayoutResource;
	private Context mContext;
	private String mAppPackageName;
	
	public DexClassInfoAdapter(Context context, int resource, List<DexClassInfo> objects, String appPackageName) {
		super(context, resource, objects);
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayoutResource = resource;
		mDexClassInfoList = new ArrayList<DexClassInfo>();
		mDexClassInfoList.addAll(objects);
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

		// Get DexClassInfo
		DexClassInfo dexClassInfo = getItem(holder.position);

		// Set icon
		holder.ivClassIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.java_class));
		holder.ivClassIcon.setVisibility(View.VISIBLE);
		
		// Set class name
		holder.tvClassName.setText(dexClassInfo.getClassName());

		// Handle click
		holder.rlClass.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				DexClassInfo dexClassInfo = mDexClassInfoList.get(holder.position);
				if(dexClassInfo != null){
					Intent intent = new Intent();
					intent.putExtra(ClassActivity.DEX_PACKAGE_EXTRA, dexClassInfo.getPackageName());
					intent.putExtra(MethodActivity.DEX_CLASS_EXTRA, dexClassInfo.getClassName());
					intent.putExtra(PackageActivity.APP_PACKAGE_EXTRA, mAppPackageName);
					intent.setClass(mContext, MethodActivity.class);
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
		public RelativeLayout rlClass;
		public ImageView ivClassIcon;
		public TextView tvClassName;
		
		public ViewHolder(View theRow, int thePosition) {
			row = theRow;
			position = thePosition;
			rlClass = (RelativeLayout) row.findViewById(R.id.rlClass);
			ivClassIcon = (ImageView) row.findViewById(R.id.ivClassIcon);
			tvClassName = (TextView) row.findViewById(R.id.tvClassName);
		}
	}
}
