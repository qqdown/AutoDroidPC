package edu.nju.autodroid.graph;

import java.util.HashMap;
import java.util.HashSet;

import edu.nju.autodroid.Action;
import edu.nju.autodroid.activity.LayoutNode;

public class WindowEdge {
	//<node,Set<action>>键值对,同一个node可能会有多个action
	protected HashMap<LayoutNode, HashSet<Action>> edgeMap = new HashMap<LayoutNode, HashSet<Action>>();
	
	public WindowEdge(){}
	
	public WindowEdge(LayoutNode node, Action action){
		addAction(node, action);
	}
	
	public void addAction(LayoutNode node, Action action){
		if(!edgeMap.containsKey(node)){
			edgeMap.put(node, new HashSet<Action>());
		}
		
		edgeMap.get(node).add(action);
	}
}
