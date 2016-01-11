package edu.nju.autodroid.strategy;

import java.io.IOException;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;

import edu.nju.autodroid.activity.LayoutTree;
import edu.nju.autodroid.utils.AdbConnection;
import edu.nju.autodroid.utils.AdbHelper;
import edu.nju.autodroid.viewgraph.ViewGraph;

public abstract class BaseLayoutStrategy implements IStrategy {
	private ViewGraph graph = new ViewGraph();
	private String runtimePackage;
	@Override
	public boolean run() {
		//获取第一个layout,作为当前layout
		runtimePackage = AdbConnection.getPackage();
		LayoutTree currentLayout = getFirstLayout();
		//上一个layout
		LayoutTree lastLayout = currentLayout;
		while(currentLayout != null){
			if(!tryStayInCurrentApplication())
				break;
			graph.addViewVertexIfNotExist(currentLayout);
			String action = doOneAction(currentLayout);
			if(action != null){//采取一个行为
				LayoutTree tempLayout = currentLayout;
				currentLayout = getCurrentLayout(lastLayout);//获取当前layout
				lastLayout = tempLayout;
				graph.addViewEdgeIfNotExist(lastLayout, currentLayout, action);//添加边
			}
			else{
				System.out.println("pressback");
				AdbConnection.pressBack();
			}
			
			if(!tryStayInCurrentApplication())
				break;
		}
		
		return true;
	}
	
	protected boolean tryStayInCurrentApplication(){
		//保证当前activity仍在当前package中
		try {
			if(!AdbHelper.getFocusedActivity().contains(getRuntimePackage()))
			{
				System.out.println("activity\t" + AdbHelper.getFocusedActivity());
				System.out.println("pressback");
				AdbConnection.pressBack();
				if(!AdbHelper.getFocusedActivity().contains(getRuntimePackage()))
					return false;
				System.out.println("activity\t" + AdbHelper.getFocusedActivity());
			}
		} catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException
				| IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	protected String getRuntimePackage(){
		return runtimePackage;
	}
	
	protected abstract String doOneAction(LayoutTree lt);
	
	protected abstract LayoutTree getFirstLayout();
	
	protected abstract LayoutTree getCurrentLayout(LayoutTree lastLayout);
	
	protected abstract boolean isSameLayout(LayoutTree t1, LayoutTree t2);

}
