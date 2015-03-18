package com.bocom.bbip.gdeupsb.repository;

import com.bocom.bbip.data.annotation.Delete;
import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdLotChkCtl;

public interface GdLotChkCtlRepository extends PagingAndSortingRepository<GdLotChkCtl, Long> {

    @Find
    public void update(GdLotChkCtl lotChkCtlInput);
    @Delete
    public void deleteByGameId(GdLotChkCtl lotChkCtlInput);
}