package com.bocom.bbip.gdeupsb.repository;

import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdLotChkCtl;
import com.bocom.bbip.gdeupsb.entity.GdLotChkCtlKey;

public interface GdLotChkCtlRepository extends PagingAndSortingRepository<GdLotChkCtl, GdLotChkCtlKey> {

    @Find
    public void update(GdLotChkCtl lotChkCtlInput);
}