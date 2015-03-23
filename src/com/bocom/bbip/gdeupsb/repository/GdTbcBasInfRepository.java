package com.bocom.bbip.gdeupsb.repository;

import com.bocom.bbip.data.annotation.Update;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdTbcBasInf;

public interface GdTbcBasInfRepository extends PagingAndSortingRepository<GdTbcBasInf, String> {

    @Update
    public void update(GdTbcBasInf tbcBasInfo);
}