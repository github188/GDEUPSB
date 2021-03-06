package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdEupsWatAgtInf;

public interface GdEupsWatAgtInfRepository extends PagingAndSortingRepository<GdEupsWatAgtInf, String> {
	public void update(GdEupsWatAgtInf gdeups);
	public List<GdEupsWatAgtInf> findA(GdEupsWatAgtInf gdeups);
	public List<GdEupsWatAgtInf> findDelFail(Map<String, Object> map);
}