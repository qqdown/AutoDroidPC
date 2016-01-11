package edu.nju.autodroid.viewgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


import edu.nju.autodroid.activity.LayoutTree;

public class ViewGraph {
	
	private HashMap<ViewVertex, List<ViewVertex>> vertexMap;
	private HashMap<ViewVertex, List<ViewEdge>> edgeMap;
	
	public ViewGraph(){
		vertexMap = new HashMap<ViewVertex, List<ViewVertex>>();
		edgeMap = new  HashMap<ViewVertex, List<ViewEdge>>();
	}
	
	public ViewVertex addViewVertexIfNotExist(LayoutTree layoutTree){
		ViewVertex vv = findByLayout(layoutTree);
		if(vv == null)
		{
			vv = new ViewVertex(layoutTree);
			vertexMap.put(vv, new ArrayList<ViewVertex>());
			edgeMap.put(vv, new ArrayList<ViewEdge>());
		}
		return vv;
	}
	
	public void addViewEdgeIfNotExist(LayoutTree from, LayoutTree to, String action){
		ViewVertex vvFrom = findByLayout(from);
		if(vvFrom == null)
			return;
		
		List<ViewVertex> vvList = vertexMap.get(vvFrom);
		List<ViewEdge> veList = edgeMap.get(vvFrom);
		
		ViewVertex vvTo =  addViewVertexIfNotExist(to);
		int index = vvList.indexOf(vvTo);
		if(index < 0){//未曾添加过from到to的边
			vvList.add(vvTo);
			veList.add(new ViewEdge(action));
		}else{//添加过边，所以判断action是否相同，相同则不再添加，否则就添加
			ViewEdge ve = veList.get(index);
			if(!ve.action.equals(action)){
				vvList.add(vvTo);
				veList.add(new ViewEdge(action));
			}
		}
	}
	
	protected ViewVertex findByLayout(LayoutTree layoutTree){
		if(layoutTree == null) return null;
		Iterator<ViewVertex> it = vertexMap.keySet().iterator();
		while(it.hasNext()){
			ViewVertex vv = it.next();
			if(vv.getViewId() == layoutTree.layoutId)
				return vv;
		}
		return null;
	}
}
