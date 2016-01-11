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
		//��ȡ��һ��layout,��Ϊ��ǰlayout
		runtimePackage = AdbConnection.getPackage();
		LayoutTree currentLayout = getFirstLayout();
		//��һ��layout
		LayoutTree lastLayout = currentLayout;
		while(currentLayout != null){
			if(!tryStayInCurrentApplication())
				break;
			graph.addViewVertexIfNotExist(currentLayout);
			String action = doOneAction(currentLayout);
			if(action != null){//��ȡһ����Ϊ
				LayoutTree tempLayout = currentLayout;
				currentLayout = getCurrentLayout(lastLayout);//��ȡ��ǰlayout
				lastLayout = tempLayout;
				graph.addViewEdgeIfNotExist(lastLayout, currentLayout, action);//��ӱ�
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
		//��֤��ǰactivity���ڵ�ǰpackage��
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
