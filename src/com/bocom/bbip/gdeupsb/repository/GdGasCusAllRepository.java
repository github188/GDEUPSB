package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.annotation.Update;
import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdGasCusAll;

public interface GdGasCusAllRepository extends PagingAndSortingRepository<GdGasCusAll, String> {
	
	@Find
	public List<GdGasCusAll> findDataBeforeOptDat(GdGasCusAll cusAllAgt);
	
	@Find
	public List<GdGasCusAll> findDataByOptDate(Map<String, Object> map);
	
	@Update
	public void update(GdGasCusAll addGasCusAll);
	
	@Find
	public List<Map<String, Object>> findCusInfo(GdGasCusAll qryCusAll);

	public Page<GdGasCusAll> findDataByOptDate(Pageable pageable,
			Map<String, Object> map);
	
	
}