package com.bocom.bbip.gdeupsb.action.sign;

import java.util.HashMap;
import java.util.Map;

import com.bocom.bbip.eups.action.BaseAction;

public class SwitchActInfo extends BaseAction{
	public String getIdTypeInMap (String idTyp){
	 Map<String, String> switchMap = new HashMap<String, String>();
	 switchMap.put("0102", "15");
	 switchMap.put("15", "0102");
	 
	 String idTypAftSwitch = switchMap.get(idTyp);
	 return idTypAftSwitch;
	 
 }
}
