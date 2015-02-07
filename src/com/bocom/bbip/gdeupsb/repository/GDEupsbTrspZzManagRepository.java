package com.bocom.bbip.gdeupsb.repository;

import java.util.List;

import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspZzManag;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspZzManagKey;

public interface GDEupsbTrspZzManagRepository extends PagingAndSortingRepository<GDEupsbTrspZzManag, GDEupsbTrspZzManagKey> {
	
	public List<Integer> findCount(GDEupsbTrspZzManag gdeupsb);
}