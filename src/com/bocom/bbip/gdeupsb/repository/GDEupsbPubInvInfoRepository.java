package com.bocom.bbip.gdeupsb.repository;

import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsbPubInvInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspNpManag;

public interface GDEupsbPubInvInfoRepository extends PagingAndSortingRepository<GDEupsbPubInvInfo, String> {
	public int findCount(GDEupsbPubInvInfo gdeupsb);
	public Page<GDEupsbTrspNpManag> find(Pageable pageable, GDEupsbTrspNpManag gdeupsb);
}