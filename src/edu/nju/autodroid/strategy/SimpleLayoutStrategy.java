package edu.nju.autodroid.strategy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import edu.nju.autodroid.activity.LayoutNode;
import edu.nju.autodroid.activity.LayoutTree;
import edu.nju.autodroid.activity.TreeSearchOrder;
import edu.nju.autodroid.utils.AdbConnection;
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
		return new LayoutTree(AdbConnection.getLayout());
	}

	@Override
	protected LayoutTree getCurrentLayout(LayoutTree lastLayout) {
		LayoutTree currentLayout = new LayoutTree(AdbConnection.getLayout());
		if(isSameLayout(currentLayout, lastLayout)){//如果仍然为同一个layout
			currentLayout = lastLayout;
		}
		else{
			//先查找是否过去遍历过
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
		return currentLayout;
	}

	@Override
	protected boolean isSameLayout(LayoutTree t1, LayoutTree t2) {
		if(t1.similarityWith(t2) >= 0.9)
			return true;
		return false;
	}

	@Override
	protected boolean doOneAction(LayoutTree lt){
		List<LayoutNode> nodeList = null;
		Integer currentIndex = 0;
		if(!layoutNodeMap.containsKey(lt)){
			nodeList = lt.findAll(new Predicate<LayoutNode>() {
				@Override
				public boolean test(LayoutNode t) {
					return t.clickable || t.longClickable || t.scrollable;
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
			return false;
		LayoutNode node = nodeList.get(currentIndex);
		
		if(node.clickedCount == 0 && node.clickable){
			//AdbConnection.doClick(node);
			AdbConnection.doClickAndWaitForNewWindow(node, 1000);
			node.clickedCount ++;
		}else if(node.longClickedCount == 0 && node.longClickable){
			AdbConnection.doLongClick(node);
			node.longClickedCount ++;
		}else{
			layoutActionMap.put(lt, currentIndex+1);
		}
		
		return true;
	}
}
