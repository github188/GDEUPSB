package com.bocom.bbip.gdeupsb.repository;

import java.util.List;

import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.annotation.Update;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdLotSysCfg;

public interface GdLotSysCfgRepository extends
		PagingAndSortingRepository<GdLotSysCfg, String> {

	@Update
	public void update(GdLotSysCfg lotSysCfgInfoInput);

	@Find
	public List<GdLotSysCfg> findSysCfg();

}