package com.bocom.bbip.gdeupsb.repository;

import com.bocom.bbip.data.annotation.Update;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdLotCusInf;

public interface GdLotCusInfRepository extends PagingAndSortingRepository<GdLotCusInf, String> {

    @Update
    public void update(GdLotCusInf lotCusInfInput);
}