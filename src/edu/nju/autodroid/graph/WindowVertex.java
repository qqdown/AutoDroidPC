package edu.nju.autodroid.graph;

import java.util.HashMap;

import edu.nju.autodroid.activity.AndroidWindow;

public class WindowVertex {
	
	public AndroidWindow window;
	
	//��window������window֮����ڽӾ���,String����id
	public HashMap<String, WindowEdge> adjMap = new HashMap<String, WindowEdge>();//���ڽӱ�

	public WindowVertex(AndroidWindow window){
		this.window = window;
	}
}
