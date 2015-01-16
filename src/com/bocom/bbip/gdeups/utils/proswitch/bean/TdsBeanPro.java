package com.bocom.bbip.gdeups.utils.proswitch.bean;

/**
 * 属性bean
 * 
 * @author ac.w
 * 
 */
public class TdsBeanPro {

	private String key = null;

	private String value = null;

	public String getName() {
		return key;
	}

	public void setName(String name) {
		this.key = name;
	}

	public String toString() {
		return String.format("tangdi bean, name=[%s],!!!!!!", key, value);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
