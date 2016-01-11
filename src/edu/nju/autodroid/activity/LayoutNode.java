package edu.nju.autodroid.activity;

import java.util.ArrayList;
import java.util.List;


public class LayoutNode {
	public int index;
	public String text;
	public String className;
	public String packageName;
	public String contentDesc;
	public boolean checkable;
	public boolean checked;
	public boolean clickable;
	public boolean enabled;
	public boolean focusable;
	public boolean focuesd;
	public boolean scrollable;
	public boolean longClickable;
	public boolean password;
	public boolean selected;
	public int[] bound;
	public String indexXpath;//�ڵ�������xPath
	public int callIndex;
	
	public LayoutNode parent;
	public List<LayoutNode> children;
	public LayoutTree UIChildren;//�������ÿؼ�����Ҫ��ָclick�Ȳ���������ת����layout
	public int totalChildrenCount = 0;
	
	//������Ϊ����
	public int clickedCount = 0;
	public int longClickedCount = 0;
	public int scrolledCount = 0;
	public int setTextCount = 0;

	
	public LayoutNode(){
		resetActionCount();
		callIndex = 0;
		totalChildrenCount = 0;
		bound = new int[4];
		children = new ArrayList<LayoutNode>();
		UIChildren = null;
	}
	
	public void addChild(LayoutNode child){
		children.add(child);
	}

	public int getChildrenCount(){
		return children.size();
	}
	
	@Override
	public String toString() {
		String str = "";
		str += "index=" + index + " ";
		str += "text=" + text + " ";
		str += "className=" + className + " ";
		str += "packageName=" + packageName + " ";
		str += "contentDesc=" + contentDesc + " ";
		str += "checkable=" + checkable + " ";
		str += "checked=" + checked + " ";
		str += "clickable=" + clickable + " ";
		str += "enabled=" + enabled + " ";
		str += "focusable=" + focusable + " ";
		str += "focuesd=" + focuesd + " ";
		str += "scrollable=" + scrollable + " ";
		str += "longClickable=" + longClickable + " ";
		str += "password=" + password + " ";
		str += "selected=" + selected + " ";
		str += "bound=[" + bound[0] + "," + bound[1] + "][" + bound[2] + "," + bound[3] + "]";
		return str;
	}
	
	public double similarityWith(LayoutNode node){
		if(node == null)
			return 0;
		if(node.className.equals(this.className))
			return 1;
		
		return 0;
	}
	
	public boolean canUserInteracted(){
		if(clickable || longClickable || scrollable || focusable)
			return true;
		return false;
	}
	
	
	/**
	 * ��equals��ͬ����node֮���className��text����Ϊ�գ���ͬʱ������true
	 * @param node
	 * @return ��node֮���className��text����Ϊ�գ���ͬʱ������true
	 */
	public boolean equalWith(LayoutNode node){
		if(node == null)
			return false;
		if(node.className.equals(className) || (!node.text.isEmpty() && node.text.equals(text)))
			return true;
		else
			return false;
	}
	
	public void resetActionCount(){
		clickedCount = 0;
		longClickedCount = 0;
		scrolledCount = 0;
	}
}
