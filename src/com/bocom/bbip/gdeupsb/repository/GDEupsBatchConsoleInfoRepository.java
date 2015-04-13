package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.annotation.Delete;
import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.annotation.FindOne;
import com.bocom.bbip.data.annotation.Insert;
import com.bocom.bbip.data.annotation.Update;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;

public interface GDEupsBatchConsoleInfoRepository extends PagingAndSortingRepository<GDEupsBatchConsoleInfo, String> {
	
	@FindOne
	public GDEupsBatchConsoleInfo findConsoleInfo(GDEupsBatchConsoleInfo info);

	@Delete
	public void deleteConsoleInfo(String batNo);

	@Update
	public void updateConsoleInfo(GDEupsBatchConsoleInfo info);

	@Insert
	public void insertConsoleInfo(GDEupsBatchConsoleInfo info);
	
	@Find
	public List<Map<String, Object>> findTotalInfo(Map<String, Object> map);
	
}