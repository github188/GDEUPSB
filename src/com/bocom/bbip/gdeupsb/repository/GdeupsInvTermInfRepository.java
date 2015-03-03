package com.bocom.bbip.gdeupsb.repository;

import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdeupsInvTermInf;

public interface GdeupsInvTermInfRepository extends PagingAndSortingRepository<GdeupsInvTermInf, String> {
	
	void updateInvalidNum(GdeupsInvTermInf gdeupsInvTermInf);
	
	void updateNum(GdeupsInvTermInf gdeupsInvTermInf);
	
	void deleteInvTermInf(GdeupsInvTermInf gdeupsInvTermInf);
	
}