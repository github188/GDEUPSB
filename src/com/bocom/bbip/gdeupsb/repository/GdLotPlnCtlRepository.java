package com.bocom.bbip.gdeupsb.repository;

import com.bocom.bbip.data.domain.NoId;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdLotPlnCtl;

public interface GdLotPlnCtlRepository extends PagingAndSortingRepository<GdLotPlnCtl, NoId> {
    /**
     * 定投计划执行，更新定投控制表状态
     * @param eupsLotAutPln
     */
    void updateTxnsts(GdLotPlnCtl gdLotPlnCtl);
}