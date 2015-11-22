package edu.nju.autodroid.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.TimeoutException;

import edu.nju.autodroid.Command;
import edu.nju.autodroid.activity.ActivityNode;

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
	
	//读取命令，为同步函数，会一直阻塞知道得到数据
	public static Command receiveCommand(){
		 try {
			if(ois == null)
				ois = new ObjectInputStream(mSocket.getInputStream());
			return (Command)ois.readObject();
		} catch (ClassNotFoundException | IOException e) {
			System.out.println("server error " + e.getMessage());
			return null;
		}
	}
	
	/*
	 * 获取简单的命令结果
	 * 命令仅仅包含一个cmd，无任何其它参数
	 * 结果包含且仅包含一个String param，
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
	
	public static String getLayout(){
		return getSimpleString(Command.cmdGetLayout);
	}
	
	public static String getActivity(){
		return getSimpleString(Command.cmdGetActivity);
	}
	
	public static String getPackage(){
		return getSimpleString(Command.cmdGetPackage);
	}
	
	public static boolean doClick(ActivityNode btn){
		Command cmd = new Command();
		cmd.cmd = Command.cmdDoClick;
		cmd.params = new String[]{btn.indexXpath};
		sendCommand(cmd);
		cmd = receiveCommand();
		return Boolean.parseBoolean(cmd.params[0]);
	}
	
	public static boolean doSetText(ActivityNode node, String content){
		Command cmd = new Command();
		cmd.cmd = Command.cmdDoSetText;
		cmd.params = new String[]{node.indexXpath, content};
		sendCommand(cmd);
		cmd = receiveCommand();
		return Boolean.parseBoolean(cmd.params[0]);
	}
	
	public static boolean doLongClick(ActivityNode node){
		Command cmd = new Command();
		cmd.cmd = Command.cmdDoLongClick;
		cmd.params = new String[]{node.indexXpath};
		sendCommand(cmd);
		cmd = receiveCommand();
		return Boolean.parseBoolean(cmd.params[0]);
	}
}
