package com.bocom.bbip.gdeupsb.repository;


import com.bocom.bbip.data.annotation.Update;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsbLotSysCfgInfo;

public interface GDEupsbLotSysCfgInfoRepository extends PagingAndSortingRepository<GDEupsbLotSysCfgInfo, String> {

    @Update
    public void update(GDEupsbLotSysCfgInfo lotSysCfgInfo);
}