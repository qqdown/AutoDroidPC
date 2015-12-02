package edu.nju.autodroid.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.android.ddmlib.log.LogReceiver;
import com.android.ddmlib.log.LogReceiver.ILogListener;
import com.android.ddmlib.log.LogReceiver.LogEntry;

public class AdbHelper 
{
	//鐢ㄤ簬鍚屾
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
	        	Logger.logError("鑾峰彇璁惧瓒呮椂"); 
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
	
	//鑾峰彇IDevice
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
	
	public static void installApk(String apkFilePath){
		File apkFile = new File(apkFilePath);
		if(!apkFile.exists())
			return;
		CmdExecutor.execCmd(Configuration.getADBPath() + " install -r " + apkFile.getAbsolutePath());
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
				if(i1<0 || i2<0)
					return;
				output = output.substring(i1+1, i2);
				result[0] = output.split(" ")[2];
			}
		});
		return result[0];
	}
	
	public static void startActivity(String activityName) throws TimeoutException, AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException{
		IDevice device = getDevice();
		device.executeShellCommand("am start -n " + activityName, new  IShellOutputReceiver() {
			
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
				String output = new String(arg0);
				System.out.println("start activity: " + output);
			}
		});
		
	}
	
	public static void stopApplication(String packageName) throws TimeoutException, AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException{
		IDevice device = getDevice();
		device.executeShellCommand("am force-stop " + packageName, new  IShellOutputReceiver() {
			
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
				String output = new String(arg0);
				System.out.println("stop application: " + output);
			}
		});
	}
	
	//获取当前正在运行的Activity，返回包含Activity名的List，顺序为运行栈顶-》栈底
	public static List<String>  getRunningActivities() throws TimeoutException, AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException {
		IDevice device = getDevice();
		final List<String> result = new ArrayList<String>();
		device.executeShellCommand("dumpsys activity | grep 'Run #'", new  IShellOutputReceiver() {
			
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
				String output = new String(arg0);
				String[] lines = output.split("\n");
				
				for(int i=0; i<lines.length; i++){
					String line = lines[i];
					if(line.isEmpty())
						continue;
					int l,r;
					l = line.indexOf("{");
					r = line.indexOf("}");
					if(l<0 || r<0)
						continue;
					result.add(line.substring(l+1,r).split(" ")[2]);
				}
			}
		});

		return result;
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