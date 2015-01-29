package com.bocom.bbip.gdeupsb.repository;

import java.util.List;

import com.bocom.bbip.data.domain.NoId;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.EupsInvDtlBok;

public interface EupsInvDtlBokRepository extends PagingAndSortingRepository<EupsInvDtlBok, NoId> {
	List<EupsInvDtlBok> findIsExist(EupsInvDtlBok eupsInvDtlBok);
	
	void updateCheck(EupsInvDtlBok eupsInvDtlBok);
	
	void updateStatus(EupsInvDtlBok eupsInvDtlBok);
	
	void deleteInvDtlBok(EupsInvDtlBok eupsInvDtlBok);
}