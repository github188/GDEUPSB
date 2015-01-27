package com.bocom.bbip.gdeupsb.utils;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import xmlDefinition.TdsBean;
import xmlDefinition.TdsBeanPro;


/**
 * 通过传入tableId及key，找到对应配置文件下的value，用于code switch
 * 
 * @author qc.w
 * 
 */
public class CodeSwitchUtils {

	public static String codeGenerator(String tableId, String key) {

		String xml = "classpath:config/switchcode/tds-test.xml";

		ApplicationContext ctx = null;
		ctx = new ClassPathXmlApplicationContext(new String[] { xml });

		TdsBean tds = ctx.getBean(tableId, TdsBean.class);

		Map<String, TdsBeanPro> p = tds.getKey();
		TdsBeanPro t = p.get(key);

		return t.getValue();
	}

}
