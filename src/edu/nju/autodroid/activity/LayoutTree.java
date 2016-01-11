package edu.nju.autodroid.activity;

import java.io.ByteArrayInputStream;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.nju.autodroid.utils.Algorithm;
import edu.nju.autodroid.utils.Logger;


public class LayoutTree 
{
	private LayoutNode root;//root node has empty content
	
	private List<LayoutNode> findList = new ArrayList<LayoutNode>();
	private String layoutXML;
	private String activityName;
	
	private int possibleUICount = 0;//���ܵĿɴ����¼�����
	
	public int layoutId = 0;
	
	public LayoutTree(String layoutXML, String activityName){
		layoutId = layoutXML.hashCode();
		this.activityName = activityName;
		root = new LayoutNode();
		try{
			this.layoutXML = layoutXML;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(layoutXML.getBytes("utf-8")));
			Element rootEle = doc.getDocumentElement();
			if(rootEle == null)
				return;
			NodeList nodes = rootEle.getChildNodes();
			if(nodes==null) return;
			root.totalChildrenCount += nodes.getLength();
			for(int i=0; i<nodes.getLength(); i++){
				Node node = nodes.item(i);
				if(node != null && node.getNodeType() == Node.ELEMENT_NODE){
					LayoutNode an = parseActivityNode(node);
					if(an.canUserInteracted())
						possibleUICount++;
					an.indexXpath = an.index + "";
					root.addChild(an);
					createActivityTree(node, an);
					root.totalChildrenCount += an.totalChildrenCount;
				}
			}
			
		}catch(Exception e){
			Logger.logException(e);
		}
	}
	
	public int getPossibleUICount(){
		return possibleUICount;
	}
	
	public LayoutNode getRoot(){
		return root;
	}
	
	public void forAll(Consumer<LayoutNode> consumer){
		for(LayoutNode n: root.children){
			forAll(n, consumer);
		}
	}
	
	private void forAll(LayoutNode node, Consumer<LayoutNode> consumer){
		if(node == null)
			return;
		consumer.accept(node);
		for(LayoutNode n: node.children){
			forAll(n, consumer);
		}
	}
	
	/**
	 * �����������������Ľڵ�
	 * @param predicate ����
	 * @param searchOrder ����˳��
	 * @return ������������searhOrderΪ˳��Ľڵ��б���Ϊ���ҵ�������һ�����б�
	 */
	public List<LayoutNode> findAll(Predicate<LayoutNode> predicate, TreeSearchOrder searchOrder){
		findList.clear();
		if(searchOrder == TreeSearchOrder.DepthFirst){
			for(LayoutNode n: root.children){
				findAll(n, predicate);
			}
		}
		else if(searchOrder == TreeSearchOrder.BoardFirst){//������Ȳ���
			Queue<LayoutNode> q = new LinkedList<LayoutNode>();
			for(LayoutNode n: root.children){
				q.offer(n);
			}
			
			while(!q.isEmpty()){
				LayoutNode cn = q.poll();
				if(predicate.test(cn)){
					findList.add(cn);
				}
				for(LayoutNode n: cn.children){
					q.offer(n);
				}
			}
		}
		return findList;
	}
	
	/**
	 * ���������˳���������ҽڵ�
	 * @param predicate ����
	 * @return ���ҵ��Ľڵ��б�δ���ҵ�����Ľڵ㷵��һ����List
	 */
	public List<LayoutNode> findAll(Predicate<LayoutNode> predicate){
		findList.clear();
		for(LayoutNode n: root.children){
			findAll(n, predicate);
		}
		return findList;
	}
	
	//����DFSģʽ
	private void findAll(LayoutNode node, Predicate<LayoutNode> predicate){
		if(node == null)
			return;
		if(predicate.test(node))
			findList.add(node);
		for(LayoutNode n: node.children){
			findAll(n, predicate);
		}
	}
	
	public String getLayoutXML(){
		return layoutXML;
	}
	
	private LayoutNode parseActivityNode(Node node){
		LayoutNode anode = new LayoutNode();
		NamedNodeMap nnm = node.getAttributes();
		anode.index = Integer.parseInt(nnm.getNamedItem("index").getNodeValue());
		anode.text = nnm.getNamedItem("text").getNodeValue();
		anode.className = nnm.getNamedItem("class").getNodeValue();
		anode.packageName = nnm.getNamedItem("package").getNodeValue();
		anode.contentDesc = nnm.getNamedItem("content-desc").getNodeValue();
		anode.checkable = Boolean.parseBoolean(nnm.getNamedItem("checkable").getNodeValue());
		anode.checked = Boolean.parseBoolean(nnm.getNamedItem("checked").getNodeValue());
		anode.clickable = Boolean.parseBoolean(nnm.getNamedItem("clickable").getNodeValue());
		anode.enabled = Boolean.parseBoolean(nnm.getNamedItem("enabled").getNodeValue());
		anode.focusable = Boolean.parseBoolean(nnm.getNamedItem("focusable").getNodeValue());
		anode.focuesd = Boolean.parseBoolean(nnm.getNamedItem("focused").getNodeValue());
		anode.scrollable = Boolean.parseBoolean(nnm.getNamedItem("scrollable").getNodeValue());
		anode.longClickable = Boolean.parseBoolean(nnm.getNamedItem("long-clickable").getNodeValue());
		anode.password = Boolean.parseBoolean(nnm.getNamedItem("password").getNodeValue());
		anode.selected = Boolean.parseBoolean(nnm.getNamedItem("selected").getNodeValue());
		String boundStr = nnm.getNamedItem("bounds").getNodeValue();
		Matcher matcher = Pattern.compile("[0-9]+").matcher(boundStr);
		matcher.find();
		anode.bound[0] = Integer.parseInt(matcher.group());
		matcher.find();
		anode.bound[1] = Integer.parseInt(matcher.group());
		matcher.find();
		anode.bound[2] = Integer.parseInt(matcher.group());
		matcher.find();
		anode.bound[3]= Integer.parseInt(matcher.group());
		return anode;
	}
	
	private void createActivityTree(Node curNode, LayoutNode parent){
		if(curNode == null)
			return;
		NodeList nodes = curNode.getChildNodes();
		if(nodes == null)
			return;
		parent.totalChildrenCount += nodes.getLength();
		for(int i=0; i<nodes.getLength(); i++){
			Node node = nodes.item(i);
			if(node != null && node.getNodeType() == Node.ELEMENT_NODE){
				LayoutNode an = parseActivityNode(node);
				an.indexXpath = parent.indexXpath + " " + an.index;
				parent.addChild(an);
				createActivityTree(node, an);
				parent.totalChildrenCount += an.totalChildrenCount;
			}
		}
	}
	
	public void print(){
		for(LayoutNode n : root.children){
			print(n, 0);
		}
	}
	
	private void print(LayoutNode node, int depth){
		for(int i=0; i<depth; i++){
			System.out.print(" ");
		}
		System.out.println(node.indexXpath + " " + node.toString());
		for(LayoutNode n : node.children){
			print(n, depth+1);
		}
	}
	
	/**
	 * ���㵱ǰlayoutTree����һ��layoutTree�����ƶ�,��������BFS���еı༭������Ϊ����
	 * @param layoutTree ���Ƚϵ���һ��layoutTree
	 * @return 0.0-1.0֮������ƶ�ֵ
	 */
	public double similarityWith(LayoutTree layoutTree){
		if(layoutTree.layoutXML.equals(this.layoutXML))
			return 1.0;
		
		int editDis = Algorithm.EditDistance(getTreeBFSHashes(), layoutTree.getTreeBFSHashes());
		return 1.0-editDis*1.0/root.totalChildrenCount;
	}
	
	protected Integer[] getTreeBFSHashes(){
		Integer[] hashes = new Integer[root.totalChildrenCount];
		int i=0;
		Queue<LayoutNode> nodeQueue = new LinkedList<LayoutNode>();
		for(LayoutNode n : root.children){
			nodeQueue.offer(n);
		}
		
		while(!nodeQueue.isEmpty()){
			LayoutNode cn = nodeQueue.poll();
			//������TreeViewʱ�����ǲ����ڲ��Ľṹ
			if(!cn.className.contains("TreeView")){
				for(LayoutNode n : cn.children){
					nodeQueue.offer(n);
				}
			}
			hashes[i++] = cn.className.hashCode();
		}
		
		return hashes;
	}
	
	/**
	 *����һ��LayoutTree�ϲ�
	 * @param lt
	 */
	public void mergeWith(LayoutTree lt){
		Queue<LayoutNode> thisQueue = new LinkedList<LayoutNode>();
		//List<LayoutNode> ltList = new ArrayList<LayoutNode>();
		Queue<LayoutNode> ltQueue = new LinkedList<LayoutNode>();
		thisQueue.offer(root);
		ltQueue.offer(lt.root);
		
		while(!thisQueue.isEmpty()){
			LayoutNode parent = thisQueue.poll();
			LayoutNode ltParent = ltQueue.poll();
			for(LayoutNode n : ltParent.children){
				LayoutNode fNode = findNodeInList(parent.children, n);
				if(fNode == null){//û���ҵ�����ô�ͽ�n��ӽ�parent����ɺϲ�
					parent.children.add(n);
				}
				else{//�ҵ�����������½��кϲ�
					thisQueue.offer(fNode);
					ltQueue.offer(n);
				}
			}
			
		}
		
	}
	
	private static LayoutNode findNodeInList(Collection<LayoutNode> nodeList, LayoutNode node){
		for(LayoutNode n : nodeList){
			if(n.equalWith(node))
				return n;
		}
		return null;
	}

	public String getActivitytName() {
		return activityName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)                                      //�ȼ���Ƿ����Է��ԣ���Ƚ�obj�Ƿ�Ϊ�ա�����Ч�ʸ�
			return true;
		if(obj == null)         
			return false;
		if(!(obj instanceof LayoutTree))
			return false;
			  
		final LayoutTree  item = (LayoutTree )obj;
			  
		return item.layoutId == this.layoutId;
	}
}
