package com.bocom.bbip.gdeupsb.utils;

import java.util.Map;

import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.jump.bp.support.CollectionUtils;

public class SwitchCodeUtils {
	private static final Map <String,String>switchMap=CollectionUtils.createMap();
	private static final Map <String,String>switchMapIActNo=CollectionUtils.createMap();
	private static final Map <String,String>switchMapIActNm=CollectionUtils.createMap();

	
	static{
		switchMap.put(GDConstants.GD_ELE_APPNAM_GZ, GDConstants.GD_ELE_CAgtNo_GZ);
		switchMap.put(GDConstants.GD_ELE_APPNAM_ST, GDConstants.GD_ELE_CAgtNo_ST);
		switchMap.put(GDConstants.GD_ELE_APPNAM_JM, GDConstants.GD_ELE_CAgtNo_JM);
		switchMap.put(GDConstants.GD_ELE_APPNAM_JY, GDConstants.GD_ELE_CAgtNo_JY);
		switchMap.put(GDConstants.GD_ELE_APPNAM_JY2, GDConstants.GD_ELE_CAgtNo_JY2);
		switchMap.put(GDConstants.GD_ELE_APPNAM_DG, GDConstants.GD_ELE_CAgtNo_DG);
		switchMap.put(GDConstants.GD_ELE_APPNAM_ZS, GDConstants.GD_ELE_CAgtNo_ZS);
		switchMap.put(GDConstants.GD_ELE_APPNAM_FS, GDConstants.GD_ELE_CAgtNo_FS);
		switchMap.put(GDConstants.GD_ELE_APPNAM_HZ, GDConstants.GD_ELE_CAgtNo_HZ);
		switchMap.put(GDConstants.GD_ELE_APPNAM_FS, GDConstants.GD_ELE_CAgtNo_FS);
		
		switchMapIActNo.put(GDConstants.GD_ELE_APPNAM_GZ, GDConstants.GD_ELE_IActNo_GZ);
		switchMapIActNo.put(GDConstants.GD_ELE_APPNAM_ST, GDConstants.GD_ELE_IActNo_ST);
		switchMapIActNo.put(GDConstants.GD_ELE_APPNAM_JM, GDConstants.GD_ELE_IActNo_JM);
		switchMapIActNo.put(GDConstants.GD_ELE_APPNAM_JY, GDConstants.GD_ELE_IActNo_JY);
		switchMapIActNo.put(GDConstants.GD_ELE_APPNAM_JY2, GDConstants.GD_ELE_IActNo_JY2);
		switchMapIActNo.put(GDConstants.GD_ELE_APPNAM_DG, GDConstants.GD_ELE_IActNo_DG);
		switchMapIActNo.put(GDConstants.GD_ELE_APPNAM_ZS, GDConstants.GD_ELE_IActNo_ZS);
		switchMapIActNo.put(GDConstants.GD_ELE_APPNAM_FS, GDConstants.GD_ELE_IActNo_FS);
		switchMapIActNo.put(GDConstants.GD_ELE_APPNAM_HZ, GDConstants.GD_ELE_IActNo_HZ);
		switchMapIActNo.put(GDConstants.GD_ELE_APPNAM_FS, GDConstants.GD_ELE_IActNo_FS);
		
		switchMapIActNm.put(GDConstants.GD_ELE_APPNAM_GZ, GDConstants.GD_ELE_IActNm_GZ);
		switchMapIActNm.put(GDConstants.GD_ELE_APPNAM_ST, GDConstants.GD_ELE_IActNm_ST);
		switchMapIActNm.put(GDConstants.GD_ELE_APPNAM_JM, GDConstants.GD_ELE_IActNm_JM);
		switchMapIActNm.put(GDConstants.GD_ELE_APPNAM_JY, GDConstants.GD_ELE_IActNm_JY);
		switchMapIActNm.put(GDConstants.GD_ELE_APPNAM_JY2, GDConstants.GD_ELE_IActNm_JY2);
		switchMapIActNm.put(GDConstants.GD_ELE_APPNAM_DG, GDConstants.GD_ELE_IActNm_DG);
		switchMapIActNm.put(GDConstants.GD_ELE_APPNAM_ZS, GDConstants.GD_ELE_IActNm_ZS);
		switchMapIActNm.put(GDConstants.GD_ELE_APPNAM_FS, GDConstants.GD_ELE_IActNm_FS);
		switchMapIActNm.put(GDConstants.GD_ELE_APPNAM_HZ, GDConstants.GD_ELE_IActNm_HZ);
		switchMapIActNm.put(GDConstants.GD_ELE_APPNAM_FS, GDConstants.GD_ELE_IActNm_FS);

	}
	public static String getCAgtNo(final String appNam) {
		return switchMap.get(appNam);
	}
	public static String getIActNo(final String appNam) {
		return switchMapIActNo.get(appNam);
	}
	public static String getIActNm(final String appNam) {
		return switchMapIActNm.get(appNam);
	}
}
