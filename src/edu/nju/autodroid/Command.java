package edu.nju.autodroid;

import java.io.Serializable;

import com.android.uiautomator.core.UiObject;

public class Command implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final int cmdUnknown = 0x0000;
	
	public static final int cmdPressHome = 0x0001;
	public static final int cmdPressBack = 0x0002;
	
	public static final int cmdGetLayout = 0x1001;
	@Deprecated  //uiautomaot涓嶆敮鎸佽繖涓柟娉�
	public static final int cmdGetActivity = 0x1002;
	public static final int cmdGetPackage = 0x1003;
	
	public static final int cmdDoClick = 0x2001;
	public static final int cmdDoSetText = 0x2002;
	public static final int cmdDoLongClick = 0x2003;
	
	public static final int cmdDoScrollBack = 0x2101;
	
	public Command(){
		params = null;
		objs = null;
	}
	
	public int cmd;
	public String[] params;
	public UiObject[] objs;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String str = cmd + "";
		for(String p : params){
			str += " " + p;
		}
		return str;
	}
}
