package edu.nju.autodroid.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;



public abstract class AdbHelper 
{
	//用于同步
	private static Object sSync = new Object();
	protected static boolean adbInitialized = false;
	protected static AndroidDebugBridge adb = null;
	
	protected IDevice device;
	
	protected AdbHelper()
	{
		
	}
	
	public static boolean initializeBridge() {
		synchronized (sSync) {
			if (!adbInitialized) {
				try {
					AndroidDebugBridge.init(false);
					//AndroidDebugBridge.init(true);
					adb = AndroidDebugBridge.createBridge(
							Configuration.getADBPath(), true);
					
					waitForInitialDeviceList();
					adbInitialized = true;
					Logger.logInfo("Init Bridge successfully!");
				} catch (Exception e) {
					Logger.logException(e);
				}
			}
			return adbInitialized;
		}
	}
	
	
	private static boolean waitForInitialDeviceList()
	{
		int count = 0;  
	    while (adb.hasInitialDeviceList() == false)   
	    {
	    	try   
	    	{
	    		Thread.sleep(100);  
	    		count++; 
	    	}
	    	catch (InterruptedException e)   
	    	{
	    		Logger.logException(e.getMessage());
	    		return false;
	    	}
	    	
	        if (count > 100)   
	        {
	        	Logger.logError("获取设备超时"); 
	        	return false;
	        }
	    }  
	    return true;
	}
	
	public static IDevice getDevice(){
		assert (adbInitialized);
		synchronized (sSync) {
			IDevice[] recognizedDevices = adb.getDevices();
			if(recognizedDevices == null || recognizedDevices.length==0)
				return null;
			return recognizedDevices[0];
		}
	}
	
	public static List<IDevice> getDevices(){
		assert (adbInitialized);
		synchronized (sSync) {
			return Arrays.asList(adb.getDevices());
		}
	}
	
	public static List<String> getDeviceNames()
	{
		assert (adbInitialized);
		//assert (!isDeviceBusy(deviceName));
		List<String> deviceNames = new ArrayList<String>();
		synchronized (sSync) {
			IDevice[] recognizedDevices = adb.getDevices();
			for (IDevice currDev : recognizedDevices) {
				if (currDev.isOnline()) {
					deviceNames.add(currDev.getName());
					break;
				}
			}
			return deviceNames;
		}
	}
	
	//获取IDevice
	public static IDevice getIDevice(String deviceName) {
		assert (adbInitialized);
		//assert (!isDeviceBusy(deviceName));
		synchronized (sSync) {
			IDevice targetDevice = null;
			IDevice[] recognizedDevices = adb.getDevices();
			for (IDevice currDev : recognizedDevices) {
				if (currDev.isOnline()
						&& currDev.toString().equalsIgnoreCase(deviceName)) {
					targetDevice = currDev;
					break;
				}
			}
			return targetDevice;
		}
	}
	
	public static IDevice getIDevice(int deviceIndex)
	{
		assert (adbInitialized);
		synchronized (sSync) {
			IDevice[] recognizedDevices = adb.getDevices();
			if(deviceIndex >= recognizedDevices.length || deviceIndex < 0)
				return null;
			return recognizedDevices[deviceIndex];
		}
	}
	
	public static String getFocusedActivity() throws TimeoutException, AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException{
		IDevice device = getDevice();
		final String[] result = new String[1];
		device.executeShellCommand("dumpsys activity | grep mFocusedActivity", new  IShellOutputReceiver() {
			
			@Override
			public boolean isCancelled() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void flush() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void addOutput(byte[] arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				String output = new String(arg0);
				int i1,i2;
				i1 = output.indexOf('{');
				i2 = output.indexOf('}');
				output = output.substring(i1+1, i2);
				result[0] = output.split(" ")[2];
			}
		});
		return result[0];
	}
	
	public static void StartActivity(String activityName){
		IDevice device = getDevice();
	}
	
	public static void terminateBridge()
	{
		if(!adbInitialized)
			return;

		synchronized (sSync) {
			AndroidDebugBridge.terminate();
			adbInitialized = false;
		}
	}
}