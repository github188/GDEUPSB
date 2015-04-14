package com.bocom.bbip.gdeupsb.repository;

import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdFbpeFileBatchTmp;

public interface GdFbpeFileBatchTmpRepository extends PagingAndSortingRepository<GdFbpeFileBatchTmp, String> {

	public void updateFbpe(GdFbpeFileBatchTmp gdFbpeFileBatchTmp);
}