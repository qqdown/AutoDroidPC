package edu.nju.autodroid;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Random;
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

import android.graphics.Rect;
import edu.nju.autodroid.activity.ActivityLayoutNode;
import edu.nju.autodroid.activity.ActivityLayoutTree;
import edu.nju.autodroid.utils.AdbConnection;
import edu.nju.autodroid.utils.AdbHelper;
import edu.nju.autodroid.utils.CmdExecutor;
import edu.nju.autodroid.utils.Configuration;
import edu.nju.autodroid.utils.Logger;

public class PCMain {
	public static final String TAG = "pc";  
    public static int PC_LOCAL_PORT = 22222;  
    public static int PHONE_PORT = 22222;  

	public static void main(String[] args) throws TimeoutException, AdbCommandRejectedException, IOException, InterruptedException, ShellCommandUnresponsiveException {

		Logger.initalize("logger.txt");
		AdbHelper.initializeBridge();

		AdbConnection.initializeConnection(PC_LOCAL_PORT, PHONE_PORT);
		int count = 100;
		String packageName = AdbConnection.getPackage();
		Logger.logInfo("package "  + packageName);
		while(true)
		{
			Logger.logInfo("Activity " + AdbHelper.getFocusedActivity());
			if(!AdbHelper.getFocusedActivity().contains(packageName))
			{
				AdbConnection.pressBack();
				if(!AdbHelper.getFocusedActivity().contains(packageName))
					break;
			}
			ActivityLayoutTree curAT = new ActivityLayoutTree(AdbConnection.getLayout());
			List<ActivityLayoutNode> clickableNodes = curAT.findAll(new Predicate<ActivityLayoutNode>() {
				@Override
				public boolean test(ActivityLayoutNode t) {
					return t.clickable;
				}
			});
			
			if(clickableNodes.size() == 0)
				break;
			
			int randI = new Random().nextInt(clickableNodes.size());
			ActivityLayoutNode node = clickableNodes.get(randI);
			AdbConnection.doClick(node);
			Logger.logInfo("click " + node.className + " " + node.text);
			//Thread.sleep(500);
		}
		
		AdbHelper.terminateBridge();
		Logger.endLogging();
	}
}
