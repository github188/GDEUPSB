package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.annotation.Delete;
import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.annotation.Insert;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsZhAGBatchTemp;

public interface GDEupsZHAGBatchTempRepository extends
		PagingAndSortingRepository<GDEupsZhAGBatchTemp, String> {

	@Find
	public List<GDEupsZhAGBatchTemp> findByBatNo(final String BatNo);
	@Find
	public List<Map> findByBatNo2(final String BatNo);
	@Insert
	public void batchInsert(List <GDEupsZhAGBatchTemp>list);

	@Delete
	public void deleteByBatNo(final String BatNo);

	@Delete
	public void deleteAll();

}