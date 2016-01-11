package edu.nju.autodroid.viewgraph;

import edu.nju.autodroid.activity.LayoutTree;

public class ViewVertex {
	private int viewId;
	private LayoutTree layoutTree;
	
	public ViewVertex(LayoutTree layoutTree){
		this.layoutTree = layoutTree;
		this.viewId = layoutTree.layoutId;
	}
	
	public LayoutTree getLayoutTree(){
		return this.layoutTree;
	}
	
	public int getViewId(){
		return this.viewId;
	}
	
	@Override
	public int hashCode() {
		return viewId;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)                                      //先检查是否其自反性，后比较obj是否为空。这样效率高
			return true;
		if(obj == null)         
			return false;
		if(!(obj instanceof ViewVertex))
			return false;
			  
		final ViewVertex item = (ViewVertex)obj;
			  
		return item.viewId == this.viewId;
	}
}
