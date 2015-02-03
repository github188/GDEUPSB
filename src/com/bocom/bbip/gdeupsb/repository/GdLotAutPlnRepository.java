package com.bocom.bbip.gdeupsb.repository;

import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdLotAutPln;

public interface GdLotAutPlnRepository extends PagingAndSortingRepository<GdLotAutPln, String> {
	
	/**
	 * 定投计划取消，更新定投计划表状态
	 * @param eupsLotAutPln
	 */
	void updateStatus(GdLotAutPln gdLotAutPln);
	/**
	 * 定投计划执行，更新定投计划表执行结果
	 * @param eupsLotAutPln
	 */
	void updateResult(GdLotAutPln gdLotAutPln);
}