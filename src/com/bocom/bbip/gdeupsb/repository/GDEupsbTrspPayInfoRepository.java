package com.bocom.bbip.gdeupsb.repository;

import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspPayInfo;

public interface GDEupsbTrspPayInfoRepository extends PagingAndSortingRepository<GDEupsbTrspPayInfo, String> {
	public void update(GDEupsbTrspPayInfo gdEupsbTrspPayInfo);
}