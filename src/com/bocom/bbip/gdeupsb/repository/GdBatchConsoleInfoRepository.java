package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdBatchConsoleInfo;

public interface GdBatchConsoleInfoRepository extends PagingAndSortingRepository<GdBatchConsoleInfo, String> {

	@Find
	public List<Map<String, Object>> findCountByBatInfo(
			GdBatchConsoleInfo findBatCnt);

}