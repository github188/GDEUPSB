package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.annotation.Delete;
import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.annotation.Update;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdLotChkDtl;

public interface GdLotChkDtlRepository extends PagingAndSortingRepository<GdLotChkDtl, String> {
    
    @Delete
    public void deleteByGameId(GdLotChkDtl lotChkCtlInput);
    
    @Update
    public void updateByGameIdAndDrawId(GdLotChkDtl lotChkDtl);

    @Update
    public void updateMatchLotChkDtl(GdLotChkDtl gdLotChkDtl);

    @Find
    public int statLotChkDtlUnChk(GdLotChkDtl gdLotChkDtl);
    /**打印福彩方对账失败清单*/
    @Find
    public List<Map<String,String>>findThdFailList(Map<String,String> map);
}