package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspInvChgInfo;

public interface GDEupsbTrspInvChgInfoRepository extends PagingAndSortingRepository<GDEupsbTrspInvChgInfo, String> {

	/** 协办行对账--更改发票清单 add by WangMQ */
	@Find
	public List<Map<String, Object>> find2(Map<String, Object> map);
	
	public List<GDEupsbTrspInvChgInfo> findInvGroup(Map<String, Object> map);
}