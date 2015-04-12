package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.annotation.Insert;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdFbpdObusBatchTmp;

public interface GdFbpdObusBatchTmpRepository extends PagingAndSortingRepository<GdFbpdObusBatchTmp, String> {

	@Find
	List<GdFbpdObusBatchTmp> findByBatNo(String data);
	
	@Find
	public List<Map<String, Object>> findTot(Map<String, Object> map);
	
	@Insert
	public void batchInsert(List<GdFbpdObusBatchTmp> list);
}