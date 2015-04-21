package com.bocom.bbip.gdeupsb.repository;

import java.util.List;

import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.annotation.Update;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsZhAGBatchTemp;
import com.bocom.bbip.gdeupsb.entity.GDEupsbElecstBatchTmp;

public interface GDEupsbElecstBatchTmpRepository extends PagingAndSortingRepository<GDEupsbElecstBatchTmp, Integer> {

	@Find
	public List<GDEupsbElecstBatchTmp> findByBatNo(final String BatNo);

	@Update
	public void update(GDEupsbElecstBatchTmp elec02batchTmp);
}