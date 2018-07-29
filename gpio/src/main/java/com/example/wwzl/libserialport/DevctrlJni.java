package com.example.wwzl.libserialport;

import android.util.Log;

public class DevctrlJni
{
	static
	{
		Log.d("devctrljni", "load devctrlJni ");
		System.loadLibrary("DevctrlJni");
	}



	public native void bluetooth_onoff(int onoff);

	public native void FinancialModule_onoff(int onoff);

	public native void FinancialModule_wakeup();

	public native void electrscreen_onoff(int onoff);

	public native void uart_to232();
	
	public native void uart_toFinancialModule();
	
	public native void bluetooth_wakeup();
	
	public native void FinancialModule_sleep();

}
