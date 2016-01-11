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
		if(this == obj)                                      //�ȼ���Ƿ����Է��ԣ���Ƚ�obj�Ƿ�Ϊ�ա�����Ч�ʸ�
			return true;
		if(obj == null)         
			return false;
		if(!(obj instanceof AndroidWindow))
			return false;
			  
		final AndroidWindow  item = (AndroidWindow )obj;
			  
		return item.id.equals(this.id);
	}
}
