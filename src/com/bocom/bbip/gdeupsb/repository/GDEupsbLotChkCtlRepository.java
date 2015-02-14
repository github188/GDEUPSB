package com.bocom.bbip.gdeupsb.repository;

import com.bocom.bbip.data.annotation.Delete;
import com.bocom.bbip.data.domain.NoId;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsbLotChkCtl;

public interface GDEupsbLotChkCtlRepository extends PagingAndSortingRepository<GDEupsbLotChkCtl, NoId> {
	
	@Delete
	public void delete(GDEupsbLotChkCtl LotChkCtl);
	
/*	@Insert
	public void insert(Object object);*/
	
}