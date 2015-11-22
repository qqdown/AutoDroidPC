package edu.nju.autodroid.uiautomator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.android.ddmlib.IDevice;
import com.android.sdklib.IAndroidTarget;
import com.android.sdklib.SdkManager;
import com.android.sdklib.internal.project.ProjectCreator;
import com.android.sdklib.internal.project.ProjectProperties;
import com.android.sdklib.internal.project.ProjectProperties.PropertyType;
import com.android.sdklib.internal.project.ProjectPropertiesWorkingCopy;
import com.android.utils.IReaderLogger;
import com.android.utils.ILogger;

import edu.nju.autodroid.utils.CmdExecutor;
import edu.nju.autodroid.utils.Configuration;
import edu.nju.autodroid.utils.Logger;

public class UiAutomatorHelper 
{
	private final static String adb = Configuration.getADBPath();
	private final static String android = Configuration.getAndroidPath();
	
	private String jarName,testClass,testName;
	private int androidId, targetDevice;
	private ILogger mSdkLog;
	private SdkManager mSdkManager;
	
	public UiAutomatorHelper(){
		createLogger();
		mSdkManager = SdkManager.createManager(Configuration.getAndroidSDKPath(), this.mSdkLog);
		if(mSdkManager == null)
			Logger.logError("Unable to parse SDK content!");
	}
	
	/**
	 * 开始测试,默认为测试第一个设备
	 * @param jarName 生成的jar名
	 * @param testClass 需要测试的方法所在类
	 * @param testName 需要测试的方法名
	 * @param androidId 目标android版本
	 */
	public void BeginTest(String jarName, String testClass, String testName,
			int androidId){
		System.out.println("-----------start--uiautomator--debug-------------");
		this.jarName = jarName;
		this.testClass = testClass;
		this.testName = testName;
		this.androidId = androidId;
		this.targetDevice = 0;
		
		runUiautomator();
	}
	
	/**
	 * 开始测试
	 * @param jarName 生成的jar名
	 * @param testClass 需要测试的方法所在类
	 * @param testName 需要测试的方法名
	 * @param androidId 目标android版本
	 * @param targetDevice 测试哪一个设备
	 */
	public void BeginTest(String jarName, String testClass, String testName,
			int androidId, int targetDevice){
		System.out.println("-----------start--uiautomator--debug-------------");
		this.jarName = jarName;
		this.testClass = testClass;
		this.testName = testName;
		this.androidId = androidId;
		this.targetDevice = targetDevice;
		
		runUiautomator();
	}
	
	//Start
	private void runUiautomator(){
		//createUiTestProject();
		createBuildXml();
		modfileBuild();
		buildWithAnt();
		
		/*
		createBuildXml();
		
		
		pushTestJar(getWorkSpase() + "/bin/" + jarName + ".jar");*/
	}
	
	//step-1
	private void createBuildXml(){
		String workSpace = getWorkSpase();
		if(workSpace.contains(" ")){
			System.err.println("Warning: workSpace \"" + workSpace + "\" conatins space, which may encounter some problem!");
		}
		CmdExecutor.execCmd(android + " create uitest-project -n " + jarName
				+ " -t " + androidId + " -p " + getWorkSpase());
	}
	
	//step-1 参照com.android.sdkmanager.Main中的createUiTestProject()
	private void createUiTestProject(){
		String projectDir = getWorkSpase();
		
		IAndroidTarget[] targets = this.mSdkManager.getTargets();
		for(IAndroidTarget t : targets){
			System.out.println(t.getVersion().getApiLevel() + "\t" + t.getFullName() + "\t" + t.isPlatform());
		}
		
		try{
			File srcFolder = new File(projectDir, "src");
			srcFolder.mkdir();
			
			ProjectPropertiesWorkingCopy localProperties = ProjectProperties.create(projectDir, PropertyType.LOCAL);
			localProperties.setProperty("sdk.dir", Configuration.getAndroidSDKPath());
			localProperties.save();
			
			ProjectPropertiesWorkingCopy projectProperties = ProjectProperties.create(projectDir, PropertyType.PROJECT);
			projectProperties.setProperty("target", targets[androidId-1].hashString());
			projectProperties.save();
			
			Map<String, String> keywords = new HashMap<String,String>();
			keywords.put("PROJECT_NAME", new File(projectDir).getName());
			
			ProjectCreator creator = getProjectCreator();
			creator.installTemplate("uibuild.template", new File(projectDir, "build.xml"), keywords);
		}
		catch(Exception e){
			Logger.logException(e);
		}
	}
	
	private ProjectCreator getProjectCreator()
	  {
	    ProjectCreator creator = new ProjectCreator(this.mSdkManager, Configuration.getAndroidSDKPath(), ProjectCreator.OutputLevel.NORMAL, this.mSdkLog);
	    
	    return creator;
	  }
	
	//step-2
	private void modfileBuild(){
		StringBuffer stringBuffer = new StringBuffer();
		try{
			File file = new File("build.xml");
			if(file.isFile() && file.exists()){
				InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
				BufferedReader bufferedReader = new BufferedReader(reader);
				String lineTxt = null;
				while((lineTxt=bufferedReader.readLine()) != null){
					if(lineTxt.matches(".*help.*")){
						lineTxt = lineTxt.replaceAll("help", "build");
					}
					stringBuffer = stringBuffer.append(lineTxt + "\t\n");
				}
				reader.close();
			}
			else{
				System.err.println("Can't find build.xml");
			}
		}catch(Exception e){
			System.err.println("Error: read build.xml failed!\n " + e.getMessage());
		}
		writeText("build.xml", new String(stringBuffer));
		System.out.println("--------修改build完成---------");
	}
	
	//step-3
	private void buildWithAnt(){
		CmdExecutor.execCmd("ant");
	}
	
	//step-4
	private void pushTestJar(String localPath){
		
		System.out.println("----jar包路径： "+localPath);
		String pushCmd = adb + " push " + localPath + " /data/local/tmp/";
		System.out.println("----" + pushCmd);
		CmdExecutor.execCmd(pushCmd);
	}
	

	  private void createLogger()
	  {
	    this.mSdkLog = new IReaderLogger()
	    {
	      public void error(Throwable t, String errorFormat, Object... args)
	      {
	        if (errorFormat != null)
	        {
	          System.err.printf("Error: " + errorFormat, args);
	          if (!errorFormat.endsWith("\n")) {
	            System.err.printf("\n", new Object[0]);
	          }
	        }
	        if (t != null) {
	          System.err.printf("Error: %s\n", new Object[] { t.getMessage() });
	        }
	      }
	      
	      public void warning(String warningFormat, Object... args)
	      {
	        //if (Main.this.mSdkCommandLine.isVerbose())
	        {
	          System.out.printf("Warning: " + warningFormat, args);
	          if (!warningFormat.endsWith("\n")) {
	            System.out.printf("\n", new Object[0]);
	          }
	        }
	      }
	      
	      public void info(String msgFormat, Object... args)
	      {
	        System.out.printf(msgFormat, args);
	      }
	      
	      public void verbose(String msgFormat, Object... args)
	      {
	        System.out.printf(msgFormat, args);
	      }
	      
	      public int readLine(byte[] inputBuffer)
	        throws IOException
	      {
	        return System.in.read(inputBuffer);
	      }
	    };
	  }
	  
	public static void writeText(String path, String content){
		File dirFile = new File(new File(path).getAbsolutePath());
		try {
			//System.out.println(dirFile.getp);
			if(!dirFile.getParentFile().exists())
				dirFile.getParentFile().mkdirs();
			if(!dirFile.exists())
				dirFile.createNewFile();
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(path));
			bw.write(content);
			bw.flush();
			bw.close();
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}
	
	public static String getWorkSpase() {
		File directory = new File("");
		String abPath = directory.getAbsolutePath();
		return abPath;
	}
}
