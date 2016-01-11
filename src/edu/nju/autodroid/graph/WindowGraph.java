package edu.nju.autodroid.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.nju.autodroid.Action;
import edu.nju.autodroid.activity.AndroidWindow;
import edu.nju.autodroid.activity.LayoutNode;

public class WindowGraph {
	protected HashMap<String, WindowVertex> vertexMap = new HashMap<String, WindowVertex>();
	
	public void addWindow(AndroidWindow window){
		if(!vertexMap.containsKey(window.id)){
			vertexMap.put(window.id, new WindowVertex(window));
		}
		else{
			System.err.println("已经存在window");
		}
	}
	
	/***
	 * 
	 * @param fromId 从某个window的id
	 * @param toId 到某个window的id
	 * @param node 触发跳转的控件节点
	 * @param action 触发跳转的行为
	 */
	public void addEdge(String fromId, String toId, LayoutNode node, Action action){
		if(!vertexMap.containsKey(fromId) || !vertexMap.containsKey(toId)){
			System.err.println("不存在window id");
			return;
		}
		WindowVertex fv = vertexMap.get(fromId);
		if(!fv.adjMap.containsKey(toId)){
			fv.adjMap.put(toId, new WindowEdge());
		}
		
	}
}
