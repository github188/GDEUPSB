package com.bocom.bbip.gdeupsb.repository;

import java.util.Map;

import com.bocom.bbip.data.domain.NoId;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdLotAwdDtl;

public interface GdLotAwdDtlRepository extends PagingAndSortingRepository<GdLotAwdDtl, NoId> {
    /**
     * 插入中奖明细信息
     * @param eupsLotAwdDtl
     */
    void insertAwdDtl(GdLotAwdDtl gdLotAwdDtl);
    /**
     * 统计返奖总金额
     * @param eupsLotAwdDtl
     */
    Map<String,Object> sumAmt(GdLotAwdDtl gdLotAwdDtl);
    /**
     * 检查同一游戏编号、期号、投注流水号是否重复记录
     * @param eupsLotAwdDtl
     */
    Map<String,Object> checkTxnLog();
}