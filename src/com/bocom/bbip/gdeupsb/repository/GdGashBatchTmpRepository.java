package com.bocom.bbip.gdeupsb.repository;

import java.util.List;

import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdGashBatchTmp;

public interface GdGashBatchTmpRepository extends PagingAndSortingRepository<GdGashBatchTmp, String> {
	
	@Find
	public List<GdGashBatchTmp> findByBatNo(final String data);

	public void update(GdGashBatchTmp gdGashBatchTmp);
}