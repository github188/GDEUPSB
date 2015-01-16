package com.bocom.bbip.gdeups.utils.proswitch.definition;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * 
 * @author qc.w
 *
 */
public class TdsNamespaceHandler extends NamespaceHandlerSupport {
	public void init() {
		registerBeanDefinitionParser("table", new TdsCustomBeanDefinitionParser());
	}
}
