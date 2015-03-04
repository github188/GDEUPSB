package com.bocom.bbip.gdeupsb.repository;

import java.util.List;

import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdGasCusAll;

public interface GdGasCusAllRepository extends PagingAndSortingRepository<GdGasCusAll, String> {
	@Find
	List<GdGasCusAll> findDataBeforeOptDat(GdGasCusAll cusAllAgt);
}