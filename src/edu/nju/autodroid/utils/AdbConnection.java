package edu.nju.autodroid.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.TimeoutException;

import edu.nju.autodroid.Command;
import edu.nju.autodroid.activity.LayoutNode;

public class AdbConnection {
	private static Socket mSocket;
	private static IDevice mDevice;
	private static ObjectOutputStream oos = null;
	private static ObjectInputStream ois = null;
	public static boolean initializeConnection(int localPort, int phonePort) {
		mDevice = AdbHelper.getDevice();
		try {
			mDevice.createForward(localPort, phonePort);
			mSocket = new Socket("localhost", localPort);
			
		} catch (Exception e) {
			System.out.println("server error " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public static void terminateConnection(){
		try{			
			if(mSocket != null)
				mSocket.close();
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}

	public static void sendCommand(Command cmd){
		try {
			if(oos == null)
				oos =  new ObjectOutputStream(mSocket.getOutputStream());
			oos.writeObject(cmd);
			
		} catch (IOException e) {
			System.out.println("server error " + e.getMessage());
		}
	}
	
	/**
	 * 接受并返回命令，该命令为阻塞函数，直到收到命令才返回。
	 * @return
	 */
	public static Command receiveCommand(){
		 try {
			if(ois == null)
				ois = new ObjectInputStream(mSocket.getInputStream());
			return (Command)ois.readObject();
		} catch (ClassNotFoundException | IOException e) {
			System.out.println("PC error " + e.getMessage());
			return null;
		}
	}
	
	/*
	 * 鑾峰彇绠�鍗曠殑鍛戒护缁撴灉
	 * 鍛戒护浠呬粎鍖呭惈涓�涓猚md锛屾棤浠讳綍鍏跺畠鍙傛暟
	 * 缁撴灉鍖呭惈涓斾粎鍖呭惈涓�涓猄tring param锛�
	 */
	private static String getSimpleString(int cmdI){
		Command cmd = new Command();
		cmd.cmd = cmdI;
		sendCommand(cmd);
		cmd = receiveCommand();
		if(cmd.cmd != cmdI)
			return null;
		return cmd.params[0];
	}
	
	public static void pressHome(){
		Command cmd = new Command();
		cmd.cmd = Command.cmdPressHome;
		sendCommand(cmd);
		cmd = receiveCommand();
	}
	
	public static void pressBack(){
		Command cmd = new Command();
		cmd.cmd = Command.cmdPressBack;
		sendCommand(cmd);
		cmd = receiveCommand();
	}
	
	public static String getLayout(){
		return getSimpleString(Command.cmdGetLayout);
	}
	
	@Deprecated
	public static String getActivity(){
		return getSimpleString(Command.cmdGetActivity);
	}
	
	public static String getPackage(){
		return getSimpleString(Command.cmdGetPackage);
	}
	
	public static boolean doClick(LayoutNode btn){
		Command cmd = new Command();
		cmd.cmd = Command.cmdDoClick;
		cmd.params = new String[]{btn.indexXpath};
		sendCommand(cmd);
		cmd = receiveCommand();
		return Boolean.parseBoolean(cmd.params[0]);
	}
	
	public static boolean doSetText(LayoutNode node, String content){
		Command cmd = new Command();
		cmd.cmd = Command.cmdDoSetText;
		cmd.params = new String[]{node.indexXpath, content};
		sendCommand(cmd);
		cmd = receiveCommand();
		return Boolean.parseBoolean(cmd.params[0]);
	}
	
	public static boolean doLongClick(LayoutNode node){
		Command cmd = new Command();
		cmd.cmd = Command.cmdDoLongClick;
		cmd.params = new String[]{node.indexXpath};
		sendCommand(cmd);
		cmd = receiveCommand();
		return Boolean.parseBoolean(cmd.params[0]);
	}
	
	//默认为5秒
	public static boolean doClickAndWaitForNewWindow(LayoutNode node){
		return doClickAndWaitForNewWindow(node, 5000);
	}
	
	public static boolean doClickAndWaitForNewWindow(LayoutNode node, long timeout){
		Command cmd = new Command();
		cmd.cmd = Command.cmdDoClickAndWaitForNewWindow;
		cmd.params = new String[]{node.indexXpath, timeout+""};
		sendCommand(cmd);
		cmd = receiveCommand();
		return Boolean.parseBoolean(cmd.params[0]);
	}
	
	//默认55步 每步5ms
	public static boolean doScrollBackward(LayoutNode node){
		return doScrollBackward(node, 55);
	}
	
	//每步5ms
	public static boolean doScrollBackward(LayoutNode node, int steps){
		Command cmd = new Command();
		cmd.cmd = Command.cmdDoScrollBackward;
		cmd.params = new String[]{node.indexXpath, steps+""};
		sendCommand(cmd);
		cmd = receiveCommand();
		return Boolean.parseBoolean(cmd.params[0]);
	}
	
	public static boolean doScrollToEnd(LayoutNode node, int maxSwipes, int steps){
		Command cmd = new Command();
		cmd.cmd = Command.cmdDoScrollToEnd;
		cmd.params = new String[]{node.indexXpath, maxSwipes+"", steps+""};
		sendCommand(cmd);
		cmd = receiveCommand();
		return Boolean.parseBoolean(cmd.params[0]);
	}
	
	public static boolean doScrollToBeginning(LayoutNode node, int maxSwipes, int steps){
		Command cmd = new Command();
		cmd.cmd = Command.cmdDoScrollToBeginning;
		cmd.params = new String[]{node.indexXpath, maxSwipes+"", steps+""};
		sendCommand(cmd);
		cmd = receiveCommand();
		return Boolean.parseBoolean(cmd.params[0]);
	}
	
	public static boolean doScrollIntoView(LayoutNode node, LayoutNode viewObj){
		Command cmd = new Command();
		cmd.cmd = Command.cmdDoScrollIntoView;
		cmd.params = new String[]{node.indexXpath, viewObj.indexXpath};
		sendCommand(cmd);
		cmd = receiveCommand();
		return Boolean.parseBoolean(cmd.params[0]);
	}
}
