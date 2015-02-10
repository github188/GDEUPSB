package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.annotation.Update;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdLotDrwTbl;
import com.bocom.bbip.gdeupsb.entity.GdLotTxnJnl;

public interface GdLotTxnJnlRepository extends PagingAndSortingRepository<GdLotTxnJnl, String> {


    @Update
    public void updateByGameIdAndDrawId(GdLotTxnJnl lotTxnJnl);

    @Update
    public void update(GdLotTxnJnl lotTxnJnlInput);
    
    @Find
    public List<GdLotTxnJnl> qryLotTxnJnl(Pageable page, Map<String, Object> map);

    @Update
    public void updateMatchLotTxnJnl(GdLotTxnJnl gdLotTxnJnl);

    @Update
    public void updateUnMatchLotTxnJnl(GdLotTxnJnl gdLotTxnJnl);

    @Find
    public int statLotTxnJnlUnChk(GdLotTxnJnl gdLotTxnJnl);
    /**
     * 更新投注流水表中的中奖标志
     * @param gdLotDrwTbl
     */
    @Update
    public void updateAwdFlg(GdLotDrwTbl gdLotDrwTbl);

}