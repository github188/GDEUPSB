package com.bocom.bbip.gdeupsb.repository;

import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdEupsWatAgtInf;

public interface GdEupsWatAgtInfRepository extends PagingAndSortingRepository<GdEupsWatAgtInf, String> {
	public void update(GdEupsWatAgtInf gdeups);
}