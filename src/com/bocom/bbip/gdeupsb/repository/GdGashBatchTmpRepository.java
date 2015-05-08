package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.annotation.Update;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdGashBatchTmp;

public interface GdGashBatchTmpRepository extends
		PagingAndSortingRepository<GdGashBatchTmp, String> {

	@Find
	public List<GdGashBatchTmp> findByBatNo(final String data);

	@Update
	public void update(GdGashBatchTmp gdGashBatchTmp);

	@Find
	public List<Map<String, Object>> findGasCheckRec(Map<String, Object> baseMap);

	public void updateByThdSqn(GdGashBatchTmp gdGashBatchTmp);
}