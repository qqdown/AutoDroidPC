package edu.nju.autodroid.activity;

import java.io.ByteArrayInputStream;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.ArrayList;
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


public class ActivityLayoutTree 
{
	private ActivityLayoutNode root;//root node has empty content
	
	private List<ActivityLayoutNode> findList = new ArrayList<ActivityLayoutNode>();
	private String layoutXML;
	
	public ActivityLayoutTree(String layoutXML){
		root = new ActivityLayoutNode();
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
					ActivityLayoutNode an = parseActivityNode(node);
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
	
	public ActivityLayoutNode getRoot(){
		return root;
	}
	
	public void forAll(Consumer<ActivityLayoutNode> consumer){
		for(ActivityLayoutNode n: root.children){
			forAll(n, consumer);
		}
	}
	
	private void forAll(ActivityLayoutNode node, Consumer<ActivityLayoutNode> consumer){
		if(node == null)
			return;
		consumer.accept(node);
		for(ActivityLayoutNode n: node.children){
			forAll(n, consumer);
		}
	}
	
	public List<ActivityLayoutNode> findAll(Predicate<ActivityLayoutNode> predicate){
		findList.clear();
		for(ActivityLayoutNode n: root.children){
			findAll(n, predicate);
		}
		return findList;
	}
	
	private void findAll(ActivityLayoutNode node, Predicate<ActivityLayoutNode> predicate){
		if(node == null)
			return;
		if(predicate.test(node))
			findList.add(node);
		for(ActivityLayoutNode n: node.children){
			findAll(n, predicate);
		}
	}
	
	public String getLayoutXML(){
		return layoutXML;
	}
	
	private ActivityLayoutNode parseActivityNode(Node node){
		ActivityLayoutNode anode = new ActivityLayoutNode();
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
	
	private void createActivityTree(Node curNode, ActivityLayoutNode parent){
		if(curNode == null)
			return;
		NodeList nodes = curNode.getChildNodes();
		if(nodes == null)
			return;
		parent.totalChildrenCount += nodes.getLength();
		for(int i=0; i<nodes.getLength(); i++){
			Node node = nodes.item(i);
			if(node != null && node.getNodeType() == Node.ELEMENT_NODE){
				ActivityLayoutNode an = parseActivityNode(node);
				an.indexXpath = parent.indexXpath + " " + an.index;
				parent.addChild(an);
				createActivityTree(node, an);
				parent.totalChildrenCount += an.totalChildrenCount;
			}
		}
	}
	
	public void print(){
		for(ActivityLayoutNode n : root.children){
			print(n, 0);
		}
	}
	
	private void print(ActivityLayoutNode node, int depth){
		for(int i=0; i<depth; i++){
			System.out.print(" ");
		}
		System.out.println(node.indexXpath + " " + node.toString());
		for(ActivityLayoutNode n : node.children){
			print(n, depth+1);
		}
	}
	
	/*
	 * 计算和另一个layoutTree的相似度
	 * 
	 */
	public double similarityWith(ActivityLayoutTree layoutTree){
		if(layoutTree.layoutXML.equals(this.layoutXML))
			return 1.0;
		
		int editDis = Algorithm.EditDistance(getTreeBFSHashes(), layoutTree.getTreeBFSHashes());
		return 1.0-editDis*1.0/root.totalChildrenCount;
	}
	
	protected Integer[] getTreeBFSHashes(){
		Integer[] hashes = new Integer[root.totalChildrenCount];
		int i=0;
		Queue<ActivityLayoutNode> nodeQueue = new LinkedList<ActivityLayoutNode>();
		for(ActivityLayoutNode n : root.children){
			nodeQueue.offer(n);
		}
		
		while(!nodeQueue.isEmpty()){
			ActivityLayoutNode cn = nodeQueue.poll();
			//当出现TreeView时，我们不管内部的结构
			if(!cn.className.contains("TreeView")){
				for(ActivityLayoutNode n : cn.children){
					nodeQueue.offer(n);
				}
			}
			hashes[i++] = cn.className.hashCode();
		}
		
		return hashes;
	}
}
