package edu.nju.autodroid.strategy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;

import edu.nju.autodroid.activity.LayoutNode;
import edu.nju.autodroid.activity.LayoutTree;
import edu.nju.autodroid.activity.TreeSearchOrder;
import edu.nju.autodroid.utils.AdbConnection;
import edu.nju.autodroid.utils.AdbHelper;
import edu.nju.autodroid.utils.Logger;

public class SimpleLayoutStrategy extends BaseLayoutStrategy {

	private HashMap<LayoutTree, List<LayoutNode>> layoutNodeMap;
	private HashMap<LayoutTree, Integer> layoutActionMap;
	
	public SimpleLayoutStrategy() {
		layoutNodeMap = new HashMap<LayoutTree, List<LayoutNode>>();
		layoutActionMap = new HashMap<LayoutTree, Integer>();
	}
	
	@Override
	public String getName() {
		return "SimpleLayoutStrategy";
	}

	@Override
	protected LayoutTree getFirstLayout() { 
		try {
			LayoutTree lt = new LayoutTree(AdbConnection.getLayout(), AdbHelper.getFocusedActivity());
			System.out.println("activity\t" + lt.getActivitytName());
			return lt;
		} catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected LayoutTree getCurrentLayout(LayoutTree lastLayout){		
		LayoutTree currentLayout = null;
		try {
			//获取当前的layout
			currentLayout = new LayoutTree(AdbConnection.getLayout(), AdbHelper.getFocusedActivity());
		} catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException e) {
			e.printStackTrace();
		}
		if(isSameLayout(currentLayout, lastLayout)){//如果仍然和上一个同一个layout
			currentLayout = lastLayout;
		}
		else{
			//先查找是否过去遍历过，如果是，则改为过去相同的
			Iterator<Entry<LayoutTree, List<LayoutNode>>> iter = layoutNodeMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<LayoutTree, List<LayoutNode>> entry = (Map.Entry<LayoutTree, List<LayoutNode>>) iter.next();
				LayoutTree key = entry.getKey();
				if(isSameLayout(currentLayout, key)){//查找到已经遍历过
					currentLayout = key;
					break;
				}
			}
		}
		System.out.println("activity\t" + currentLayout.getActivitytName());
		return currentLayout;
	}

	@Override
	protected boolean isSameLayout(LayoutTree t1, LayoutTree t2) {
		if(t1.similarityWith(t2) >= 0.9)
			return true;
		return false;
	}

	@Override
	protected String doOneAction(LayoutTree lt){
		List<LayoutNode> nodeList = null;
		Integer currentIndex = 0;
		if(!layoutNodeMap.containsKey(lt)){
			nodeList = lt.findAll(new Predicate<LayoutNode>() {
				@Override
				public boolean test(LayoutNode t) {
					return t.clickable || t.longClickable || t.scrollable || t.focusable;
				}
			}, TreeSearchOrder.BoardFirst);
			//添加可操作节点
			layoutNodeMap.put(lt, nodeList);
			layoutActionMap.put(lt, new Integer(0));
		}
		else{
			nodeList = layoutNodeMap.get(lt);
			currentIndex = layoutActionMap.get(lt);
		}
		
		if(currentIndex >= nodeList.size())
			return null;
		LayoutNode node = nodeList.get(currentIndex);
		String action = "unknown";
		if((node.clickedCount ==0 || !isChildrenAllInteracted(node)) && node.clickable){
			//AdbConnection.doClick(node);
			action = "click";
			System.out.println("click\t" + node.className +"\t"+ node.text);
			//AdbConnection.doClickAndWaitForNewWindow(node, 1000);
			AdbConnection.doClick(node);
			node.clickedCount ++;
		}else if((node.longClickedCount ==0 || !isChildrenAllInteracted(node)) && node.longClickable){
			System.out.println("longclick\t"+ node.className +"\t" + node.text);
			action = "longclick";
			AdbConnection.doLongClick(node);
			node.longClickedCount ++;
		}else if(node.setTextCount == 0){
			System.out.println("settext\t"+ node.className +"\t" + "testText");
			boolean setResult = AdbConnection.doSetText(node, "123");
			System.out.println("setResult\t" + setResult);
			action = "settext";
			node.setTextCount++;
		}
		else{
			System.out.println("noaction");
			action = "noaction";
			layoutActionMap.put(lt, currentIndex+1);
		}
		
		LayoutTree nlt = getCurrentLayout(lt);
		if(node.UIChildren == null && nlt!=lt){
			node.UIChildren = nlt;
		}
		return action;
	}
	
	private boolean isChildrenAllInteracted(LayoutNode node){
		LayoutTree lt = node.UIChildren;
		if(lt == null)
			return true;
		if(layoutNodeMap.containsKey(lt)){
			double currentIndex = 1.0*layoutActionMap.get(lt);
			if(currentIndex >= 0.9*lt.getPossibleUICount())
				return true;
			return false;
		}
		return true;
	}
}
