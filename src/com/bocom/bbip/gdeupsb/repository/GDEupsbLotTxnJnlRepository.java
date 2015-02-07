package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsbLotTxnJnl;

public interface GDEupsbLotTxnJnlRepository extends PagingAndSortingRepository<GDEupsbLotTxnJnl, String> {

	
//	@Find
//    public List<GDEupsbLotTxnJnl> qryLotWinRecord(GDEupsbLotTxnJnl gdEupsbLotTxnJnl);
		
    @Find
    public List<Map<String, Object>> qryLotWinRecord(Map<String, Object> map);
    
}