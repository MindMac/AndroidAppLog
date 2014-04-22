package com.mindmac.applog.adapter;

import java.util.ArrayList;
import java.util.List;

import com.mindmac.applog.R;
import com.mindmac.applog.service.LogManager;
import com.mindmac.applog.util.DexMethodInfo;
import com.mindmac.applog.util.Util;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class DexMethodInfoAdapter extends ArrayAdapter<DexMethodInfo>{
	
	private List<DexMethodInfo> mDexMethodInfoList;
	private LayoutInflater mInflater = null;
	private int mLayoutResource;
	private Context mContext;
	private int mUid = -1;

	public DexMethodInfoAdapter(Context context, int resource, List<DexMethodInfo> objects, 
			int uid) {
		super(context, resource, objects);
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayoutResource = resource;
		mDexMethodInfoList = new ArrayList<DexMethodInfo>();
		mDexMethodInfoList.addAll(objects);
		mUid = uid;
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

		// Get DexMethodInfo
		final DexMethodInfo dexMethodInfo = getItem(holder.position);

		// Set icon
		holder.ivMethodIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.java_method));
		holder.ivMethodIcon.setVisibility(View.VISIBLE);
		
		// Check if to show the check box
		if(Util.isAndroidAppLogEnabled()){
			holder.cbMethodLog.setVisibility(View.VISIBLE);
			
			String className = null;
			if(!dexMethodInfo.getPacakgeName().equals(Util.DEFAULT_PACKAGE))
				className = String.format("%s.%s", dexMethodInfo.getPacakgeName(), dexMethodInfo.getClassName());
			else
				className = dexMethodInfo.getClassName();
			boolean isMethodLog = LogManager.getLogEnable(mUid, className, dexMethodInfo.getMethodName());
			
			if(isMethodLog)
				holder.cbMethodLog.setChecked(true);
			else
				holder.cbMethodLog.setChecked(false);
		}else{
			holder.cbMethodLog.setVisibility(View.INVISIBLE);
		}
		
	
		// Handle click
		holder.cbMethodLog.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String className = null;
				if(!dexMethodInfo.getPacakgeName().equals(Util.DEFAULT_PACKAGE))
					className = String.format("%s.%s", dexMethodInfo.getPacakgeName(), dexMethodInfo.getClassName());
				else
					className = dexMethodInfo.getClassName();
				boolean isMethodLog = LogManager.getLogEnable(mUid, className, dexMethodInfo.getMethodName());
				holder.cbMethodLog.setChecked(!isMethodLog);
				LogManager.setLogEnable(mUid, className, dexMethodInfo.getMethodName(), !isMethodLog);
			}
		});

		// Set method name
		holder.tvMethodName.setText(dexMethodInfo.getMethodName());

		return convertView;
	}
	
	
	// ViewHolder
	class ViewHolder{
		private View row;
		public int position;
		public ImageView ivMethodIcon;
		public TextView tvMethodName;
		public CheckBox cbMethodLog;
		
		public ViewHolder(View theRow, int thePosition) {
			row = theRow;
			position = thePosition;
			ivMethodIcon = (ImageView) row.findViewById(R.id.ivMethodIcon);
			tvMethodName = (TextView) row.findViewById(R.id.tvMethodName);
			cbMethodLog = (CheckBox) row.findViewById(R.id.cbMethodLog);
		}
	}
}
