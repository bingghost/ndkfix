package org.bingghost.ndkfix.popup.actions;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XmlOprate {
	private String mPath = null;
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	DocumentBuilder mBuilder = null;
	Document mDocument = null;
	Element mRoot = null;
	
	String mError = "";
	Boolean mhashSpecifiedNode = false;

	/**
	 * 构造方法
	 * @param path:xml文件的路径
	 */
	public XmlOprate(String path) {
		super();
		this.mPath = path;
		
		init();
	}
	
	public void init() {
		try {
			mBuilder = factory.newDocumentBuilder();
			mDocument = mBuilder.parse(new File(mPath));	
			mRoot = mDocument.getDocumentElement();

		} catch (Exception e) {
			mError = e.toString();
		}		
	}
	
	public Node getChildNodeFirst(Node node,String nodeName) {
		NodeList nodelist = ((Element)node).getElementsByTagName(nodeName);
		Node childNode = nodelist.item(0);
		
		return childNode;
	}
	
	public void fixNatures() { 
		Node natures = getChildNodeFirst(mRoot, "natures");
		NodeList allNodes = natures.getChildNodes();
		int size = allNodes.getLength();
		for (int j = 0; j < size; j++) {
			Node curNode = allNodes.item(j);
			if (curNode == null) {
				break;
			}
			
			if (hasSpecifiedNode(curNode,"org.eclipse.cdt.core.cnature")) {
				natures.removeChild(curNode);
				continue;
			}
			
			if (hasSpecifiedNode(curNode,"org.eclipse.cdt.core.ccnature")) {
				natures.removeChild(curNode);
				continue;
			}
			
			if (hasSpecifiedNode(curNode,"org.eclipse.cdt.managedbuilder.core.managedBuildNature")) {
				natures.removeChild(curNode);
				continue;
			}
			
			if (hasSpecifiedNode(curNode,"org.eclipse.cdt.managedbuilder.core.ScannerConfigNature")) {
				natures.removeChild(curNode);
				break;
			}
		}
		
	}
	
	public void fixBuildCommand() {
		Node buildSpec = getChildNodeFirst(mRoot, "buildSpec");
		NodeList allNodes = buildSpec.getChildNodes();
		int size = allNodes.getLength();
		for (int j = 0; j < size; j++) {
			Node buildCommand = allNodes.item(j);
			if (buildCommand == null) {
				break;
			}
			
			if (hasSpecifiedNode(buildCommand,"org.eclipse.cdt.managedbuilder.core.genmakebuilder")) {
				buildSpec.removeChild(buildCommand);
				continue;
			}
			
			if (hasSpecifiedNode(buildCommand,"org.eclipse.cdt.managedbuilder.core.ScannerConfigBuilder")) {
				buildSpec.removeChild(buildCommand);
				break;
			}
		}
	}
	
	public void save() {
		try {
			TransformerFactory tFactory =TransformerFactory.newInstance(); 
			Transformer transformer;
			transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(mDocument); 
			StreamResult result = new StreamResult(new java.io.File(mPath)); 
			transformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		} 

	}
	
	public void fixProjectConfig() {
		fixBuildCommand();
		
		fixNatures();
		
		save();
	}
	
	public Boolean hasSpecifiedNode(Node node, String checkText) {
		Boolean ret = false;
		
		// 文本类型，检查文本
		if (node.getNodeType() == Node.TEXT_NODE) {
			String curText = ((Text) node).getWholeText();
			if (curText.equals(checkText)) {
				mhashSpecifiedNode = true;
				return true;
			}
		}

		// 获取所要遍历节点的子节点
		NodeList allNodes = node.getChildNodes();
		int size = allNodes.getLength();
		if (size > 0) {
			for (int j = 0; j < size; j++) {
				Node childNode = allNodes.item(j);
				ret = hasSpecifiedNode(childNode, checkText);
				if (ret) {
					break;
				}
			}
		}
		
		return ret;
	}
	
	public static void main(String[] args) {
		// test
		XmlOprate xmlOprate = new XmlOprate("/Users/bingghost/code/.project");
		xmlOprate.fixProjectConfig();

	}
}
