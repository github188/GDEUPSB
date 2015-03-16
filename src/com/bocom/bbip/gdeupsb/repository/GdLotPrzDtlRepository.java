package com.bocom.bbip.gdeupsb.repository;

import com.bocom.bbip.data.annotation.Delete;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdLotPrzDtl;

public interface GdLotPrzDtlRepository extends PagingAndSortingRepository<GdLotPrzDtl, String> {

@Delete
public void deleteByGameId(GdLotPrzDtl dtl);
}