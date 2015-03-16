package com.bocom.bbip.gdeupsb.repository;

import com.bocom.bbip.data.annotation.Delete;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdLotPrzCtl;

public interface GdLotPrzCtlRepository extends PagingAndSortingRepository<GdLotPrzCtl, String> {


@Delete
public void deleteByGameId(GdLotPrzCtl ctl);
}