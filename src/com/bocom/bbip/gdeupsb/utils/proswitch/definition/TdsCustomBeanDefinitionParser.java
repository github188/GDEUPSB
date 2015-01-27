//package com.bocom.bbip.gdeupsb.utils.proswitch.definition;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.springframework.beans.factory.config.BeanDefinition;
//import org.springframework.beans.factory.support.RootBeanDefinition;
//import org.springframework.beans.factory.xml.BeanDefinitionParser;
//import org.springframework.beans.factory.xml.ParserContext;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//
//import com.bocom.bbip.gdeupsb.utils.proswitch.bean.TdsBean;
//import com.bocom.bbip.gdeupsb.utils.proswitch.bean.TdsBeanPro;
//
///**
// * 实现spring的bean定义解析类，
// * 
// * @author qc.w
// * 
// */
//public class TdsCustomBeanDefinitionParser implements BeanDefinitionParser {
//
//	public BeanDefinition parse(Element element, ParserContext parserContext) {
//
//		RootBeanDefinition beanDef = new RootBeanDefinition();
//		beanDef.setBeanClass(TdsBean.class);
//
//		String id = element.getAttribute("id");
//
//		NodeList el = element.getChildNodes();
//		Map<String, TdsBeanPro> map = new HashMap<String, TdsBeanPro>();
//		for (int i = 0; i < el.getLength(); i++) {
//			Node node = el.item(i);
//			if (node instanceof Element) {
//				Element ele = (Element) node;
//				String key1 = ele.getAttribute("name");
//				String value1 = ele.getAttribute("value");
//				TdsBeanPro td = new TdsBeanPro();
//				td.setName(key1);
//				td.setValue(value1);
//				map.put(key1, td);
//				beanDef.getPropertyValues().addPropertyValue("key", map);
//			}
//		}
//
//		parserContext.getRegistry().registerBeanDefinition(id, beanDef);
//
//		return beanDef;
//	}
//}