package com.bocom.bbip.gdeupsb.repository;

import java.util.List;

import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdeupsInvDtlBok;
import com.bocom.bbip.gdeupsb.entity.GdeupsInvDtlBokKey;
import com.bocom.bbip.gdeupsb.entity.GdeupsInvTermInf;

public interface GdeupsInvDtlBokRepository extends PagingAndSortingRepository<GdeupsInvDtlBok, GdeupsInvDtlBokKey> {
	
	List<GdeupsInvDtlBok> findIsExist(GdeupsInvDtlBok gdeupsInvDtlBok);
	
	void deleteInvDtlBok(GdeupsInvDtlBok gdeupsInvDtlBok);
	
	void updateCheck(GdeupsInvDtlBok gdeupsInvDtlBok);
	
	
	
	void updateStatus(GdeupsInvDtlBok gdeupsInvDtlBok);
	
}