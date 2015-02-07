package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdsAgtInf;
import com.bocom.bbip.gdeupsb.entity.GdsAgtInfKey;

public interface GdsAgtInfRepository extends PagingAndSortingRepository<GdsAgtInf, GdsAgtInfKey> {

	 /** 协议信息查询-汇总*/
    public Page<Map<String,Object>> findAgentTotInf(Pageable pageable, Object para);
    public List<Map<String,Object>> findAgentTotInf(Object gdsAgtInf);
    
    /** 签约一站通-协议更新：查询原协议信息*/
	public List<Map<String, Object>> findOldAgtInfo(Map<String, Object> inpara);
	
	/** 签约一站通-协议更新：更新原协议信息*/
	public List<Map<String, Object>> updateOldAgtInfo(Map<String, Object> inpara);
	
}