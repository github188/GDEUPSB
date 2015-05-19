package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.annotation.Delete;
import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.annotation.FindOne;
import com.bocom.bbip.data.annotation.Insert;
import com.bocom.bbip.data.annotation.Update;
import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GdEupsTransJournal;

public interface GDEupsBatchConsoleInfoRepository extends PagingAndSortingRepository<GDEupsBatchConsoleInfo, String> {
	
	public List<Map<String, Object>> findConsoleInfo(GDEupsBatchConsoleInfo info);

	@Delete
	public void deleteConsoleInfo(String batNo);

	@Update
	public void updateConsoleInfo(GDEupsBatchConsoleInfo info);

	@Insert
	public void insertConsoleInfo(GDEupsBatchConsoleInfo info);
	
	@Find
	public List<Map<String, Object>> findTotalInfo(Map<String, Object> map);
	@Find
	public List<Map<String, Object>> findGasBatSucRecord(Map<String, Object> detailMap);
	@Find
	public List<Map<String, Object>> findGasBatFalRecord(Map<String, Object> detailMap);
	@Find
	public List<Map<String, Object>> findGasBatAllRecord(
			Map<String, Object> detailMap);
	@Find
	public List<Map<String, Object>> findBatchRptInfo(
			Map<String, Object> baseMap);
	@Find
	public List<Map<String, Object>> findBatInformation(
			Map<String, Object> baseMap);

	public Page<Map<String, Object>> findBatInformation(Pageable pageable,
			Map<String, Object> baseMap);
	@Find
	public List<Map<String, Object>> findElec02BatchRptInfo(
			Map<String, Object> baseMap);
	@Find
	public List<Map<String, Object>> findElec02BatchRptInformation(
			Map<String, Object> baseMap);
	
	@Find
	public List<Map<String, Object>> findWatrBatchRptInfo(
			Map<String, Object> baseMap);
	
}