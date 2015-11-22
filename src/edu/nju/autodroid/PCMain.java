package edu.nju.autodroid;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

import android.graphics.Rect;
import edu.nju.autodroid.activity.ActivityNode;
import edu.nju.autodroid.activity.ActivityTree;
import edu.nju.autodroid.uiautomator.UiAutomatorHelper;
import edu.nju.autodroid.utils.AdbConnection;
import edu.nju.autodroid.utils.AdbHelper;
import edu.nju.autodroid.utils.CmdExecutor;
import edu.nju.autodroid.utils.Configuration;
import edu.nju.autodroid.utils.Logger;

public class PCMain {
	public static final String TAG = "server";  
    public static int PC_LOCAL_PORT = 22222;  
    public static int PHONE_PORT = 22222;  

	public static void main(String[] args) throws TimeoutException, AdbCommandRejectedException, IOException, InterruptedException, ShellCommandUnresponsiveException {

		Logger.initalize("logger.txt");
		AdbHelper.initializeBridge();

		AdbConnection.initializeConnection(PC_LOCAL_PORT, PHONE_PORT);
		int count = 100;
		while(count>0)
		{
			Logger.logInfo(AdbHelper.getFocusedActivity());
			Thread.sleep(1000);
		}
		
		
		AdbHelper.terminateBridge();
		Logger.endLogging();
	}
}
