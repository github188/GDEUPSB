package com.bocom.bbip.gdeupsb.repository;

import com.bocom.bbip.data.annotation.Update;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdTbcCusAgtInfo;

public interface GdTbcCusAgtInfoRepository extends PagingAndSortingRepository<GdTbcCusAgtInfo, String> {

	@Update
	public void update(GdTbcCusAgtInfo cusAgtInfo);
}