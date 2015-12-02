package edu.nju.autodroid.strategy;

import java.io.IOException;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;

import edu.nju.autodroid.activity.LayoutTree;
import edu.nju.autodroid.utils.AdbConnection;
import edu.nju.autodroid.utils.AdbHelper;

public abstract class BaseLayoutStrategy implements IStrategy {
	
	
	@Override
	public boolean run() {
		//获取第一个layout,作为当前layout
		String pkg = AdbConnection.getPackage();
		LayoutTree currentLayout = getFirstLayout();
		//上一个layout
		LayoutTree lastLayout = currentLayout;
		while(currentLayout != null){
			try {
				if(!AdbHelper.getFocusedActivity().contains(pkg))
					break;
			} catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException
					| IOException e) {
				e.printStackTrace();
				break;
			}
			
			if(doOneAction(currentLayout)){//采取一个行为
				
			}
			else{
				AdbConnection.pressBack();
			}
			
			LayoutTree tempLayout = currentLayout;
			currentLayout = getCurrentLayout(lastLayout);//获取当前layout
			lastLayout = tempLayout;
		}
		
		return true;
	}
	
	protected abstract boolean doOneAction(LayoutTree lt);
	
	protected abstract LayoutTree getFirstLayout();
	
	protected abstract LayoutTree getCurrentLayout(LayoutTree lastLayout);
	
	protected abstract boolean isSameLayout(LayoutTree t1, LayoutTree t2);

}
