package com.bocom.bbip.gdeupsb.utils;

import java.io.File;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.utils.FileUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.JumpException;

/**
 * 广州福彩XML转换
 * <p>
 * @version 1.0.0,2015-3-29
 * @author Administrator
 * @since 1.0.0
 */
public class GzFcXmlExchangeUtil{
	private static Logger log = LoggerFactory.getLogger(GzFcXmlExchangeUtil.class);
	
	//属性转换为子节点 (福彩方发送的解包报文)
	//交易码|节点名|属性名
	private static String[] addEleRules = new String[]{
		"201|pkgC.return|resultCode.resultDes",
		"201|pkgC.bindDealer|isBind",
		"212|pkgC.return|resultCode.resultDes",
		"200|pkgC.return|resultCode.resultDes",
		"231|pkgC.return|resultCode.resultDes",
		"231|pkgC.schemeInfo.betInfo|group.num",
		"231|pkgC.gamblerInfo.chargeInfo|isSettled",
	};
	
	//子节点转换为属性 (向福彩方发送的组包报文)
	//交易码|节点名|需要转换为属性的节点名
	private static String[] delEleRules = new String[]{
		"201|pkgC.gamblerAdditionalInfo.bindCard|isBind",
		"201|pkgC.bindDealer|isBind",
	};
	
	/**
	 * 广州福彩XML报文 属性转换为子节点
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static String GzFcXmlAdd(String xml) throws Exception {
		Document doc = DocumentHelper.parseText(xml);
		Element root = doc.getRootElement();
		String txncod = root.element("pkgH").elementText("action");
		log.info("txncod = "+txncod);
		if (StringUtils.isEmpty(txncod)) {
			throw new JumpException("获取交易码失败");
		}
		
		for (int i = 0; i < addEleRules.length; i++) {
			String[] rules = StringUtils.split(addEleRules[i],"|");
			if (rules[0].equals(txncod)) {
				String[] atrNames = StringUtils.split(rules[2],".");
				for (int j = 0; j < atrNames.length; j++) {
					addEle(root ,rules[1],atrNames[j]);
				}
			}
		}
		return root.asXML();
	}
	
	private static void addEle(Element root,String nodeName, String atrName) {
		String[] nodeNames = StringUtils.split(nodeName, ".");
		Element e = root;
		for (int i = 0; i < nodeNames.length; i++) {
			e = e.element(nodeNames[i]);
			if (e == null) {
				return;
			}
		}
		Attribute atr = e.attribute(atrName);
		Element ne = DocumentHelper.createElement(atr.getName());
		ne.setText(atr.getValue());
		e.remove(atr);
		e.add(ne);
	}
	
	/**
	 * 广州福彩XML报文子 节点转换为属性
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static String GzFcXmlDel(String xml) throws Exception {
		Document doc = DocumentHelper.parseText(xml);
		Element root = doc.getRootElement();
		String txncod = root.element("pkgH").elementText("action");
		log.info("txncod = "+txncod);
		if (StringUtils.isEmpty(txncod)) {
			throw new JumpException("获取交易码失败");
		}
		
		for (int i = 0; i < delEleRules.length; i++) {
			String[] rules = StringUtils.split(delEleRules[i],"|");
			if (rules[0].equals(txncod)) {
				String[] atrNames = StringUtils.split(rules[2],".");
				for (int j = 0; j < atrNames.length; j++) {
					delEle(root ,rules[1],atrNames[j]);
				}
			}
		}
		return root.asXML();
	}
	
	private static void delEle(Element root, String nodeName, String atrName) {
		String[] nodeNames = StringUtils.split(nodeName, ".");
		Element e = root;
		for (int i = 0; i < nodeNames.length; i++) {
			e = e.element(nodeNames[i]);
			if (e == null) {
				return;
			}
		}
		Element nexte = e.element(atrName);
		Attribute atr = DocumentHelper.createAttribute(e, nexte.getName(), nexte.getText());
		e.remove(nexte);
		e.add(atr);
	}

	public static void main(String[] args) throws Exception {
		byte[] b = FileUtils.readFileToByteArray(new File("E:/test/gzfcRsp.xml"));
		String ns = new String(b, "UTF-8");
		System.out.println("初始报文"+ns);
		ns = GzFcXmlDel(ns); //属性转为子节点
		System.out.println(ns);
//		ns = GzFcXmlDel(ns); //子节点转为属性
//		System.out.println(ns);
	}
}
