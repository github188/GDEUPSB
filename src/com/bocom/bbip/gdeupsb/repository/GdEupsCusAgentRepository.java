package com.bocom.bbip.gdeupsb.repository;

import java.util.List;

import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdEupsCusAgent;

public interface GdEupsCusAgentRepository extends PagingAndSortingRepository<GdEupsCusAgent, String> {
	
	/**	惠州燃气代扣用户信息查询 */
    public List<String> findGasJnlInfo(GdEupsCusAgent gdEupsCusAgent);
    
}