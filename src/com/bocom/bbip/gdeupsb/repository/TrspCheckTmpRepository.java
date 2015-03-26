package com.bocom.bbip.gdeupsb.repository;

import java.util.List;

import com.bocom.bbip.data.annotation.Delete;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.TrspCheckTmp;

public interface TrspCheckTmpRepository extends PagingAndSortingRepository<TrspCheckTmp, String> {
		@Delete
		public void deleteAll(String tchkNo);
		
		public void updateAll(String tChkNo);
		
		public List<TrspCheckTmp> findByTChkNo(String tChkNo);
}