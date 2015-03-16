package com.bocom.bbip.gdeupsb.repository;

import com.bocom.bbip.data.annotation.Update;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GDVechIndentInfo;

public interface GDVechIndentInfoRepository extends PagingAndSortingRepository<GDVechIndentInfo, String> {
	
	@Update
	public void update(GDVechIndentInfo updateInfo);
}