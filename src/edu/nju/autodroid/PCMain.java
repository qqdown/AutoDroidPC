package edu.nju.autodroid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;

import edu.nju.autodroid.activity.LayoutNode;
import edu.nju.autodroid.activity.LayoutTree;
import edu.nju.autodroid.strategy.IStrategy;
import edu.nju.autodroid.strategy.SimpleLayoutStrategy;
import edu.nju.autodroid.utils.AdbConnection;
import edu.nju.autodroid.utils.AdbHelper;
import edu.nju.autodroid.utils.Logger;

public class PCMain {
	public static final String TAG = "pc";  
    public static int PC_LOCAL_PORT = 22222;  
    public static int PHONE_PORT = 22222;  

	public static void main(String[] args) throws TimeoutException, AdbCommandRejectedException, IOException, InterruptedException, ShellCommandUnresponsiveException {
		//ActivityManager am;
		Logger.initalize("logger.txt");
		AdbHelper.initializeBridge();
		AdbConnection.initializeConnection(PC_LOCAL_PORT, PHONE_PORT);
		
		IStrategy strategy = new SimpleLayoutStrategy();
		System.out.println("start strategy " + strategy.getName());
		if(strategy.run()){
			System.out.println("strategy finish successfully");
		}else{
			System.out.println("strategy finish failed");
		}
		
		AdbHelper.terminateBridge();
		Logger.endLogging();
	}
	
	private static String readFromFile(String fileName){
		String content = "";
		
		File file = new File(fileName);
		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] bs = new byte[fis.available()];
			fis.read(bs);
			fis.close();
			content = new String(bs);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return content;
	}
}
