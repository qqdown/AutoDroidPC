package edu.nju.autodroid.activity;

import java.util.ArrayList;
import java.util.List;

public class AndroidWindow {
	public String id;
	public String activityName;
	public String session;
	
	public List<LayoutTree> layoutList = new ArrayList<LayoutTree>();
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)                                      //先检查是否其自反性，后比较obj是否为空。这样效率高
			return true;
		if(obj == null)         
			return false;
		if(!(obj instanceof AndroidWindow))
			return false;
			  
		final AndroidWindow  item = (AndroidWindow )obj;
			  
		return item.id.equals(this.id);
	}
}
