package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspTxnJnl;

public interface GDEupsbTrspTxnJnlRepository extends PagingAndSortingRepository<GDEupsbTrspTxnJnl, String> {
	public void update(GDEupsbTrspTxnJnl gdeupsb);
	public void updateSt(GDEupsbTrspTxnJnl gdeupsb);
	public List<Map<String, Object>> findPayInfo(GDEupsbTrspTxnJnl gdeupsb);
}