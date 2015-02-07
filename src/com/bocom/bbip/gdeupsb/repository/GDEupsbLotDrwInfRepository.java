package com.bocom.bbip.gdeupsb.repository;

import java.util.Map;

import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsbLotDrwInf;

public interface GDEupsbLotDrwInfRepository extends PagingAndSortingRepository<GDEupsbLotDrwInf, String> {

    @Find
    public GDEupsbLotDrwInf qryLotDrwInf(Map<String, Object> map);
}