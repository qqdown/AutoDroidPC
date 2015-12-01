package edu.nju.autodroid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

import android.app.ActivityManager;
import android.app.NativeActivity;
import android.graphics.Rect;
import edu.nju.autodroid.activity.ActivityLayoutNode;
import edu.nju.autodroid.activity.ActivityLayoutTree;
import edu.nju.autodroid.utils.AdbConnection;
import edu.nju.autodroid.utils.AdbHelper;
import edu.nju.autodroid.utils.Algorithm;
import edu.nju.autodroid.utils.CmdExecutor;
import edu.nju.autodroid.utils.Configuration;
import edu.nju.autodroid.utils.Logger;

public class PCMain {
	public static final String TAG = "pc";  
    public static int PC_LOCAL_PORT = 22222;  
    public static int PHONE_PORT = 22222;  

	public static void main(String[] args) throws TimeoutException, AdbCommandRejectedException, IOException, InterruptedException, ShellCommandUnresponsiveException {
		//ActivityManager am;
	
		Logger.initalize(null);
		AdbHelper.initializeBridge();
		AdbConnection.initializeConnection(PC_LOCAL_PORT, PHONE_PORT);

		
		ActivityLayoutTree at = new ActivityLayoutTree(AdbConnection.getLayout());
		int count = 0;
		while(true){
			ActivityLayoutTree at1 = new ActivityLayoutTree(AdbConnection.getLayout());
			System.out.println(at1.getRoot().totalChildrenCount + "");
			System.out.println(at.similarityWith(at1));
			Thread.sleep(1000);
			if(count++>100)
				break;
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
