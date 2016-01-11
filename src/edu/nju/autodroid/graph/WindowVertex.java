package edu.nju.autodroid.graph;

import java.util.HashMap;

import edu.nju.autodroid.activity.AndroidWindow;

public class WindowVertex {
	
	public AndroidWindow window;
	
	//该window到其他window之间的邻接矩阵,String代表id
	public HashMap<String, WindowEdge> adjMap = new HashMap<String, WindowEdge>();//出邻接表

	public WindowVertex(AndroidWindow window){
		this.window = window;
	}
}
