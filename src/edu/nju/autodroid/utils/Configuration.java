package edu.nju.autodroid.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
	private static String propPath = "/autodroid.properties";
	private static Properties prop;
	
	static
	{
		prop = new Properties();
		try
		{
			System.out.println(propPath);
			InputStream in = Object.class.getResourceAsStream(propPath);
			prop.load(in);
			in.close();
			System.out.println("Init properties successfully!");
		}
		catch(Exception e)
		{
			System.out.println("Init properties error!\n" + e.getMessage());
		}
	}
	
	public static String getProperty(String key)
	{
		return prop.getProperty(key);
	}
	
	public static String getAndroidSDKPath()
	{
		return prop.getProperty("android_sdk_path");
	}
	
	public static String getADBPath()
	{
		if(System.getProperty("os.name").contains("linux"))
			return getAndroidSDKPath() + "/platform-tools/adb";
		return getAndroidSDKPath() + "/platform-tools/adb.exe";
	}
	
	public static String getAndroidPath()
	{
		if(System.getProperty("os.name").contains("linux"))
			return getAndroidSDKPath() + "/tools/android";
		return getAndroidSDKPath() + "/tools/android.bat";
	}
}
