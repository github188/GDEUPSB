package com.bocom.bbip.gdeupsb.repository;

import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.annotation.Update;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdLotChkDtl;

public interface GdLotChkDtlRepository extends PagingAndSortingRepository<GdLotChkDtl, String> {

    @Update
    public void updateByGameIdAndDrawId(GdLotChkDtl lotChkDtl);

    @Update
    public void updateMatchLotChkDtl(GdLotChkDtl gdLotChkDtl);

    @Find
    public int statLotChkDtlUnChk(GdLotChkDtl gdLotChkDtl);
}