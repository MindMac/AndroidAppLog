package com.mindmac.applog.service;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.mindmac.applog.util.Util;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteStatement;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;

/***
 * 
 * @author Wenjun Hu
 * Remote service
 * 
 */

public class LogService {	
	private static ILogService mLogServiceClient = null;
	private static final String cLogServiceName = "logservice";
	
	// Uid of this application
	private static int mSelfUid = -1;
	
	// Database File
	private static final String cLogPolicyDbFile = "logpolicy.db";
	
	private static SQLiteDatabase mLogPolicyDatabase = null;
	
	// Database Table
	private static final String cTablePolicy = "policy";
	private static final String cTableApp = "app";
	
	// Database Statement
	private static SQLiteStatement mStmtGetPolicy = null;
	private static SQLiteStatement mStmtGetClassPolicy = null;
	private static SQLiteStatement mStmtGetUidPolicy = null;
	private static SQLiteStatement mStmtGetApp = null;

	
	public static void registerLogService() {
		try {
			// public static void addService(String name, IBinder service)
			Class<?> cServiceManager = Class.forName("android.os.ServiceManager");
			Method mAddService = cServiceManager.getDeclaredMethod("addService", String.class, IBinder.class);
			mAddService.invoke(null, cLogServiceName, mLogService);
			Util.log(null, "Register LogService: " + cLogServiceName);

		} catch (Throwable ex) {
			Util.bug(null, ex);
		}
	}
	
	public static ILogService getLogServiceClient() {
		if (mLogServiceClient == null)
			try {
				// public static IBinder getService(String name)
				Class<?> cServiceManager = Class.forName("android.os.ServiceManager");
				Method mGetService = cServiceManager.getDeclaredMethod("getService", String.class);
				mLogServiceClient = ILogService.Stub.asInterface(
						(IBinder) mGetService.invoke(null, cLogServiceName));
			} catch (Throwable ex) {
				mLogServiceClient = null;
				Util.bug(null, ex);
			}


		return mLogServiceClient;
	}
	

	public static File getDbFile() {
		File dbFile = null;
		String databaseDir = Environment.getDataDirectory() + File.separator + "data" + File.separator
				+ Util.SELF_PACKAGE_NAME + File.separator + "databases";
		File databaseDirFile = new File(databaseDir);
		if(!databaseDirFile.exists())
			databaseDirFile.mkdirs();
		dbFile = new File(databaseDir + File.separator + cLogPolicyDbFile);
		return dbFile;
	}
	

	public static void setupDatabase() {
		try {
			// Set parent directory permission
			File logPolicyDbFile = getDbFile();		
			Util.setPermission(logPolicyDbFile.getParentFile().getAbsolutePath(), 0770, 
					Util.ANDROID_UID, Util.ANDROID_UID);
			
			// Create database
			createDatabase(logPolicyDbFile);
			
			// Set database permission
			setDatabaseDbPermission(logPolicyDbFile);
		} catch (Throwable ex) {
			Util.bug(null, ex);
		}
	}
	

	private static void setDatabaseDbPermission(File dbFile){
		Util.setPermission(dbFile.getAbsolutePath(), 0770, Util.ANDROID_UID, Util.ANDROID_UID);
		File dbJournal = new File(dbFile + "-journal");
		if (dbJournal.exists())
			Util.setPermission(dbJournal.getAbsolutePath(), 0770, Util.ANDROID_UID, Util.ANDROID_UID);
	}
	

	private static void createDatabase(File dbFile){
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
		if (db.needUpgrade(1)) {
			db.beginTransaction();
			try {
				// http://www.sqlite.org/lang_createtable.html
				db.execSQL("CREATE TABLE policy (uid INTEGER NOT NULL, " +
						"class_name TEXT NOT NULL, method_name TEXT NOT NULL, log_enable INTEGER NOT NULL)");
				db.execSQL("CREATE TABLE app (package_name TEXT NOT NULL, hook_enable INTEGER NOT NULL)");
				db.execSQL("CREATE UNIQUE INDEX idx_policy ON policy(uid, class_name, method_name)");
				db.execSQL("CREATE UNIQUE INDEX idx_app ON app(package_name)");
				db.setVersion(1);
				db.setTransactionSuccessful();
				Util.log(null, "Database created: " + dbFile.getName());
			} finally {
				db.endTransaction();
			}
		}
		
		Util.log(null, String.format("Database %s version: %d", dbFile.getName(), db.getVersion()));
	}
	

	private static SQLiteDatabase getDatabaseInstance(){
		File dbFile = getDbFile();
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
		return db;
	}


	private static SQLiteDatabase getDatabase() {
		SQLiteDatabase sqliteDatabase = null;
		
		if(mLogPolicyDatabase == null)
			mLogPolicyDatabase = getDatabaseInstance();
		sqliteDatabase = mLogPolicyDatabase;

		return sqliteDatabase;
		
	}
	
	private static boolean allowAccess() {
		int uid = Binder.getCallingUid();
		if (uid != getSelfUid())
			return false;
		else
			return true;
	}
	
	private final static ILogService.Stub mLogService = new ILogService.Stub() {
		private ReentrantReadWriteLock mLock = new ReentrantReadWriteLock(true);
		
		@Override
		public boolean getLogEnable(int uid, String className, String methodName) 
				throws RemoteException {
			boolean isLogEnable = false;
			try {
				// No permissions required
				SQLiteDatabase policyDb = getDatabase();

				// Compile statement
				if (mStmtGetPolicy == null) {
					String sql = "SELECT log_enable FROM " + cTablePolicy
							+ " WHERE uid=? AND class_name=? AND method_name=?";
					mStmtGetPolicy = policyDb.compileStatement(sql);
				}

				// Execute statement
				mLock.readLock().lock();
				policyDb.beginTransaction();
				try {
					try {
						synchronized (mStmtGetPolicy) {
							mStmtGetPolicy.clearBindings();
							mStmtGetPolicy.bindLong(1, uid);
							mStmtGetPolicy.bindString(2, className);
							mStmtGetPolicy.bindString(3, methodName);
							isLogEnable = (mStmtGetPolicy.simpleQueryForLong() > 0);
						}
					} catch (SQLiteDoneException ignored) {
						isLogEnable = false;
					}

					policyDb.setTransactionSuccessful();
				} finally {
					try {
						policyDb.endTransaction();
					} finally {
						mLock.readLock().unlock();
					}
				}
			} catch (Throwable ex) {
				Util.bug(null, ex);
				throw new RemoteException(ex.toString());
			}
			
			return isLogEnable;
		}

		@Override
		public long getLogEnableByClass(int uid, String className)
				throws RemoteException {
			long totalCount = 0;
			try {
				// No permissions required
				SQLiteDatabase policyDb = getDatabase();

				// Compile statement
				if (mStmtGetClassPolicy == null) {
					String sql = "SELECT COUNT(*) FROM " + cTablePolicy
							+ " WHERE uid=? AND class_name=? AND log_enable=?";
					mStmtGetClassPolicy = policyDb.compileStatement(sql);
				}

				// Execute statement
				mLock.readLock().lock();
				policyDb.beginTransaction();
				try {
					try {
						synchronized (mStmtGetClassPolicy) {
							mStmtGetClassPolicy.clearBindings();
							mStmtGetClassPolicy.bindLong(1, uid);
							mStmtGetClassPolicy.bindString(2, className);
							mStmtGetClassPolicy.bindLong(3, 1);
							totalCount = mStmtGetClassPolicy.simpleQueryForLong();
						}
					} catch (SQLiteDoneException ignored) {
						totalCount = 0;
					}

					policyDb.setTransactionSuccessful();
				} finally {
					try {
						policyDb.endTransaction();
					} finally {
						mLock.readLock().unlock();
					}
				}
			} catch (Throwable ex) {
				Util.bug(null, ex);
				throw new RemoteException(ex.toString());
			}
			
			return totalCount;
		}

		@Override
		public long getLogEnableByUid(int uid) throws RemoteException {
			long totalCount = 0;
			try {
				// No permissions required
				SQLiteDatabase policyDb = getDatabase();

				// Compile statement
				if (mStmtGetUidPolicy == null) {
					String sql = "SELECT COUNT(*) FROM " + cTablePolicy
							+ " WHERE uid=? AND log_enable=?";
					mStmtGetUidPolicy = policyDb.compileStatement(sql);
				}

				// Execute statement
				mLock.readLock().lock();
				policyDb.beginTransaction();
				try {
					try {
						synchronized (mStmtGetUidPolicy) {
							mStmtGetUidPolicy.clearBindings();
							mStmtGetUidPolicy.bindLong(1, uid);
							mStmtGetUidPolicy.bindLong(2, 1);
							totalCount = mStmtGetUidPolicy.simpleQueryForLong();
						}
					} catch (SQLiteDoneException ignored) {
						totalCount = 0;
					}

					policyDb.setTransactionSuccessful();
				} finally {
					try {
						policyDb.endTransaction();
					} finally {
						mLock.readLock().unlock();
					}
				}
			} catch (Throwable ex) {
				Util.bug(null, ex);
				throw new RemoteException(ex.toString());
			}
			
			return totalCount;
		}
		
		@Override
		public List<ParcelableLogMethod> getLogMethodList(int uid, int limit, int offset)
				throws RemoteException {
			List<ParcelableLogMethod> logMethodList = new ArrayList<ParcelableLogMethod>();
			try{
				SQLiteDatabase policyDb = getDatabase();
				
				mLock.readLock().lock();
				policyDb.beginTransaction();
				try{
					Cursor cursor;
					
					String sql = String.format("SELECT class_name, method_name from %s WHERE uid=%d AND log_enable=1 LIMIT %d OFFSET %d",
							cTablePolicy, uid, limit, offset);
					cursor = policyDb.rawQuery(sql, new String[]{});
					if(cursor != null){
						try{
							while(cursor.moveToNext()){
								ParcelableLogMethod logMethod = new ParcelableLogMethod();
								logMethod.className = cursor.getString(0);
								logMethod.methodName = cursor.getString(1);
								
								logMethodList.add(logMethod);
							}
						}catch(Exception ex){
							Util.bug(null, ex);
						}finally{
							cursor.close();
						}
						
						policyDb.setTransactionSuccessful();
					}
				}catch(Exception ex){
					Util.bug(null, ex);
				}finally{
					try{
						policyDb.endTransaction();
					}finally{
						mLock.readLock().unlock();
					}
				}
			}catch(Throwable ex){
				Util.bug(null, ex);
				throw new RemoteException(ex.toString());
			}
			
			return logMethodList;
		}
		
		
		@Override
		public void setLogEnable(int uid, String className,
				String methodName, boolean isLogEnable) throws RemoteException {
			if(allowAccess()){
				try {
					// Set database only allowed
					SQLiteDatabase policyDb = getDatabase();
					
					mLock.writeLock().lock();
					policyDb.beginTransaction();
					try {
						// Create record
						ContentValues cvalues = new ContentValues();
						cvalues.put("uid", uid);
						cvalues.put("class_name", className);
						cvalues.put("method_name", methodName);
						cvalues.put("log_enable", isLogEnable);
						policyDb.insertWithOnConflict(cTablePolicy, null, cvalues, SQLiteDatabase.CONFLICT_REPLACE);
						
						policyDb.setTransactionSuccessful();
					} finally {
						try{
							policyDb.endTransaction();
						}finally{
							mLock.writeLock().unlock();
						}
					}
				} catch (Throwable ex) {
					Util.bug(null, ex);
					throw new RemoteException(ex.toString());
				}
			}
		}
		
		@Override
		public int deleteLogRecord(int uid, String className,
				String methodName) throws RemoteException {
			// TODO Auto-generated method stub
			int deletedNum = 0;
			if(allowAccess()){
				try {
				
					SQLiteDatabase db = getDatabase();
					mLock.writeLock().lock();
					db.beginTransaction();
					try {
						if(className != null && methodName == null)
							deletedNum = db.delete(cTablePolicy, "uid=? AND class_name=?", 
									new String[] { Integer.toString(uid), className });
						else if(className != null && methodName != null)
							deletedNum = db.delete(cTablePolicy, "uid=? AND class_name=? AND method_name=?", 
									new String[] { Integer.toString(uid), className, methodName });
						db.setTransactionSuccessful();
					} finally {
						try{
							db.endTransaction();
						}finally{
							mLock.writeLock().unlock();
						}
					}
				} catch (Throwable ex) {
					Util.bug(null, ex);
					throw new RemoteException(ex.toString());
				}
			}
			return deletedNum;
		}

		@Override
		public boolean getHookEnable(String packageName) throws RemoteException {
			// TODO Auto-generated method stub
			boolean isHookEnable = false;
			try {
				// No permissions required
				SQLiteDatabase policyDb = getDatabase();

				// Compile statement
				if (mStmtGetApp == null) {
					String sql = "SELECT hook_enable FROM " + cTableApp
							+ " WHERE package_name=?";
					mStmtGetApp = policyDb.compileStatement(sql);
				}

				// Execute statement
				mLock.readLock().lock();
				policyDb.beginTransaction();
				try {
					try {
						synchronized (mStmtGetApp) {
							mStmtGetApp.clearBindings();
							mStmtGetApp.bindString(1, packageName);
							
							isHookEnable = (mStmtGetApp.simpleQueryForLong() > 0);
						}
					} catch (SQLiteDoneException ignored) {
						isHookEnable = false;
					}

					policyDb.setTransactionSuccessful();
				} finally {
					try {
						policyDb.endTransaction();
					} finally {
						mLock.readLock().unlock();
					}
				}
			} catch (Throwable ex) {
				Util.bug(null, ex);
				throw new RemoteException(ex.toString());
			}
			
			return isHookEnable;
		}

		@Override
		public void setHookEnable(String packageName, boolean isHookEnable)
				throws RemoteException {
			if(allowAccess()){
				try {
					// Set database only allowed
					SQLiteDatabase policyDb = getDatabase();
					
					mLock.writeLock().lock();
					policyDb.beginTransaction();
					try {
						// Create record
						ContentValues cvalues = new ContentValues();
						cvalues.put("package_name", packageName);
						cvalues.put("hook_enable", isHookEnable);
						policyDb.insertWithOnConflict(cTableApp, null, cvalues, SQLiteDatabase.CONFLICT_REPLACE);
						
						policyDb.setTransactionSuccessful();
					} finally {
						try{
							policyDb.endTransaction();
						}finally{
							mLock.writeLock().unlock();
						}
					}
				} catch (Throwable ex) {
					Util.bug(null, ex);
					throw new RemoteException(ex.toString());
				}
			}
			
		}

		@Override
		public int deleteHookRecord(String packageName) throws RemoteException {
			// TODO Auto-generated method stub
			int deletedNum = 0;
			if(allowAccess()){
				try {
				
					SQLiteDatabase db = getDatabase();
					mLock.writeLock().lock();
					db.beginTransaction();
					try {
						if(packageName != null){
							deletedNum = db.delete(cTableApp, "package_name=?", 
									new String[] {packageName});
							db.setTransactionSuccessful();
						}
					} finally {
						try{
							db.endTransaction();
						}finally{
							mLock.writeLock().unlock();
						}
					}
				} catch (Throwable ex) {
					Util.bug(null, ex);
					throw new RemoteException(ex.toString());
				}
			}
			return deletedNum;
		}

	};
	
	

	private static int getSelfUid() {
		if (mSelfUid < 0){
			try {
				Context context = getContext();
				if (context != null) {
					ApplicationInfo applicationInfo = context.getPackageManager()
							.getApplicationInfo(Util.SELF_PACKAGE_NAME, PackageManager.GET_META_DATA);
					mSelfUid = applicationInfo.uid;
				}
			} catch (Throwable ex) {
				Util.bug(null, ex);
			}
		}
		
		return mSelfUid;
	}
	
	private static Context getContext() {
		// public static ActivityManagerService self()
		// frameworks/base/services/java/com/android/server/am/ActivityManagerService.java
		Context context = null;
		try {
			Class<?> cam = Class.forName("com.android.server.am.ActivityManagerService");
			Object am = cam.getMethod("self").invoke(null);
			if(am != null){
				context = (Context) cam.getDeclaredField("mContext").get(am);
			}
		} catch (Throwable ex) {
			Util.bug(null, ex);
		}
		return context;
	}
}
