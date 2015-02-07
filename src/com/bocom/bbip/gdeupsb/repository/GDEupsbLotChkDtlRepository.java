package com.bocom.bbip.gdeupsb.repository;

import com.bocom.bbip.data.annotation.Delete;
import com.bocom.bbip.data.annotation.Insert;
import com.bocom.bbip.data.domain.NoId;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsbLotChkDtl;

public interface GDEupsbLotChkDtlRepository extends PagingAndSortingRepository<GDEupsbLotChkDtl, NoId> {
	
	@Delete
	public void delete(GDEupsbLotChkDtl LotChkDtl);
	
/*	@Insert
	public void insert(Object object);
	*/
	
}