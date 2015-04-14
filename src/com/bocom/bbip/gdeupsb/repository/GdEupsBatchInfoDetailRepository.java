package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.annotation.Update;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.eups.entity.EupsBatchInfoDetail;
import com.bocom.bbip.gdeupsb.entity.GdEupsBatchInfoDetail;

public interface GdEupsBatchInfoDetailRepository extends
		PagingAndSortingRepository<GdEupsBatchInfoDetail, String> {
	
	@Update
	public void update(GdEupsBatchInfoDetail gdEupsBatchInfoDetail);

	@Find
	public List<Map<String, Object>> findFirmBatDetail(Map<String, Object> map);
	
	@Find
	public List<Map<String, Object>> findBankBookBatDetail(Map<String, Object> map);
	
	@Find
	public List<Map<String, Object>> findCardBatDetail(Map<String, Object> map);


}