package com.bocom.bbip.gdeupsb.repository;

import java.util.List;

import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.EupsStreamNo;
import com.bocom.bbip.gdeupsb.entity.EupsStreamNoList;

public interface EupsStreamNoRepository extends PagingAndSortingRepository<EupsStreamNo, String> {
	
	@Find
	public List<EupsStreamNo> findList(EupsStreamNoList eupsStreamNoList);
	@Find
	public List<EupsStreamNo> findLogService(EupsStreamNo eupsStreamNo );
}