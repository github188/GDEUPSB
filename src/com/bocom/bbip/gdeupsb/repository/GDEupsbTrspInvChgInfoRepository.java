package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspInvChgInfo;

public interface GDEupsbTrspInvChgInfoRepository extends PagingAndSortingRepository<GDEupsbTrspInvChgInfo, String> {

	
	public List<GDEupsbTrspInvChgInfo> findInvGroup(Map<String, Object> map);
}