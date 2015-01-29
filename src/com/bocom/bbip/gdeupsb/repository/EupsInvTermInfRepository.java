package com.bocom.bbip.gdeupsb.repository;

import com.bocom.bbip.data.domain.NoId;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.EupsInvTermInf;

public interface EupsInvTermInfRepository extends PagingAndSortingRepository<EupsInvTermInf, NoId> {
	
	void updateNum(EupsInvTermInf eupsInvTermInf);
	
	void updateInvalidNum(EupsInvTermInf eupsInvTermInf);
	
	void deleteInvTermInf(EupsInvTermInf eupsInvTermInf);
	
}