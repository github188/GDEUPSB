package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspNpManag;

public interface GDEupsbTrspNpManagRepository extends PagingAndSortingRepository<GDEupsbTrspNpManag, String> {
	
	public List<Integer> findCountByStatus(GDEupsbTrspNpManag gdeupsb);
	public List<Integer> findCount(GDEupsbTrspNpManag gdeupsb);
	public List<Integer> findCountSum(GDEupsbTrspNpManag gdeupsb);
	
	
	 public Page<Map<String,Object>> findNpInfo(Pageable pageable, Object para);
	public List<Map<String,Object>> findNpInfo(Object para);
	   

	 /** 协议信息查询-汇总*/
   public Page<Map<String,Object>> findAgentTotInf(Pageable pageable, Object para);
   public List<Map<String,Object>> findAgentTotInf(Object gdsAgtInf);
	
	
	public void updateSt(GDEupsbTrspNpManag gdeupsb);
	    public void update(GDEupsbTrspNpManag gdeupsb);
}