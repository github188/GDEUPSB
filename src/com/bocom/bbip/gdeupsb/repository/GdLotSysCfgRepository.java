package com.bocom.bbip.gdeupsb.repository;

import com.bocom.bbip.data.annotation.Update;
import com.bocom.bbip.data.domain.NoId;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdLotSysCfg;

public interface GdLotSysCfgRepository extends PagingAndSortingRepository<GdLotSysCfg, NoId> {
    @Update
    public void update(GdLotSysCfg lotSysCfgInfoInput);

    public GdLotSysCfg findSysCfg(String dealId);

}