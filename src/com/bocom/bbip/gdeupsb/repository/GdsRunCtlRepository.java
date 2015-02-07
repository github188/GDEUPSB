package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdsRunCtl;

public interface GdsRunCtlRepository extends PagingAndSortingRepository<GdsRunCtl, String> {
	
	/** 签约一站通-查询某卡签订了哪些业务 */
	public List<Map<String, Object>> findSignCardBId(Map<String,Object> inpara);
	
}