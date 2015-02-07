package com.bocom.bbip.gdeupsb.repository;

import com.bocom.bbip.data.annotation.Delete;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.TrspCheckTmp;

public interface TrspCheckTmpRepository extends PagingAndSortingRepository<TrspCheckTmp, String> {
		@Delete
		public void deleteAll(String string);
		
		public void updateAll(String tChkNo);
}