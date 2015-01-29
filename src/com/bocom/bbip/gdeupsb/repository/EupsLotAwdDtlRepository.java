package com.bocom.bbip.gdeupsb.repository;

import java.util.Map;

import com.bocom.bbip.data.domain.NoId;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.EupsLotAwdDtl;

public interface EupsLotAwdDtlRepository extends PagingAndSortingRepository<EupsLotAwdDtl, NoId> {
	/**
	 * 插入中奖明细信息
	 * @param eupsLotAwdDtl
	 */
	void insertAwdDtl(EupsLotAwdDtl eupsLotAwdDtl);
	/**
	 * 统计返奖总金额
	 * @param eupsLotAwdDtl
	 */
	Map<String,Object> sumAmt(EupsLotAwdDtl eupsLotAwdDtl);
	/**
	 * 检查同一游戏编号、期号、投注流水号是否重复记录
	 * @param eupsLotAwdDtl
	 */
	Map<String,Object> checkTxnLog();
}