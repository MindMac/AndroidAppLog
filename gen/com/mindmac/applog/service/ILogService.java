/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\02-ProgramWorkspace\\EclipseWorkspace\\AndroidAppLog\\src\\com\\mindmac\\applog\\service\\ILogService.aidl
 */
package com.mindmac.applog.service;
public interface ILogService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.mindmac.applog.service.ILogService
{
private static final java.lang.String DESCRIPTOR = "com.mindmac.applog.service.ILogService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.mindmac.applog.service.ILogService interface,
 * generating a proxy if needed.
 */
public static com.mindmac.applog.service.ILogService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.mindmac.applog.service.ILogService))) {
return ((com.mindmac.applog.service.ILogService)iin);
}
return new com.mindmac.applog.service.ILogService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getLogEnable:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
boolean _result = this.getLogEnable(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getLogEnableByClass:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
long _result = this.getLogEnableByClass(_arg0, _arg1);
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_getLogEnableByUid:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
long _result = this.getLogEnableByUid(_arg0);
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_getLogMethodList:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
java.util.List<com.mindmac.applog.service.ParcelableLogMethod> _result = this.getLogMethodList(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
case TRANSACTION_setLogEnable:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
boolean _arg3;
_arg3 = (0!=data.readInt());
this.setLogEnable(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_deleteLogRecord:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
int _result = this.deleteLogRecord(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getHookEnable:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.getHookEnable(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setHookEnable:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _arg1;
_arg1 = (0!=data.readInt());
this.setHookEnable(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_deleteHookRecord:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.deleteHookRecord(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setSetting:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.setSetting(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getSetting:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _result = this.getSetting(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.mindmac.applog.service.ILogService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public boolean getLogEnable(int uid, java.lang.String className, java.lang.String methodName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
_data.writeString(className);
_data.writeString(methodName);
mRemote.transact(Stub.TRANSACTION_getLogEnable, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public long getLogEnableByClass(int uid, java.lang.String className) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
_data.writeString(className);
mRemote.transact(Stub.TRANSACTION_getLogEnableByClass, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public long getLogEnableByUid(int uid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
mRemote.transact(Stub.TRANSACTION_getLogEnableByUid, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.util.List<com.mindmac.applog.service.ParcelableLogMethod> getLogMethodList(int uid, int limit, int offest) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<com.mindmac.applog.service.ParcelableLogMethod> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
_data.writeInt(limit);
_data.writeInt(offest);
mRemote.transact(Stub.TRANSACTION_getLogMethodList, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(com.mindmac.applog.service.ParcelableLogMethod.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setLogEnable(int uid, java.lang.String className, java.lang.String methodName, boolean isLogEnable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
_data.writeString(className);
_data.writeString(methodName);
_data.writeInt(((isLogEnable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setLogEnable, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public int deleteLogRecord(int uid, java.lang.String className, java.lang.String methodName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uid);
_data.writeString(className);
_data.writeString(methodName);
mRemote.transact(Stub.TRANSACTION_deleteLogRecord, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean getHookEnable(java.lang.String packageName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(packageName);
mRemote.transact(Stub.TRANSACTION_getHookEnable, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setHookEnable(java.lang.String packageName, boolean isHookEnable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(packageName);
_data.writeInt(((isHookEnable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setHookEnable, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public int deleteHookRecord(java.lang.String packageName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(packageName);
mRemote.transact(Stub.TRANSACTION_deleteHookRecord, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setSetting(java.lang.String name, java.lang.String value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(name);
_data.writeString(value);
mRemote.transact(Stub.TRANSACTION_setSetting, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getSetting(java.lang.String name) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(name);
mRemote.transact(Stub.TRANSACTION_getSetting, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getLogEnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getLogEnableByClass = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getLogEnableByUid = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getLogMethodList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_setLogEnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_deleteLogRecord = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_getHookEnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_setHookEnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_deleteHookRecord = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_setSetting = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_getSetting = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
}
public boolean getLogEnable(int uid, java.lang.String className, java.lang.String methodName) throws android.os.RemoteException;
public long getLogEnableByClass(int uid, java.lang.String className) throws android.os.RemoteException;
public long getLogEnableByUid(int uid) throws android.os.RemoteException;
public java.util.List<com.mindmac.applog.service.ParcelableLogMethod> getLogMethodList(int uid, int limit, int offest) throws android.os.RemoteException;
public void setLogEnable(int uid, java.lang.String className, java.lang.String methodName, boolean isLogEnable) throws android.os.RemoteException;
public int deleteLogRecord(int uid, java.lang.String className, java.lang.String methodName) throws android.os.RemoteException;
public boolean getHookEnable(java.lang.String packageName) throws android.os.RemoteException;
public void setHookEnable(java.lang.String packageName, boolean isHookEnable) throws android.os.RemoteException;
public int deleteHookRecord(java.lang.String packageName) throws android.os.RemoteException;
public void setSetting(java.lang.String name, java.lang.String value) throws android.os.RemoteException;
public java.lang.String getSetting(java.lang.String name) throws android.os.RemoteException;
}
