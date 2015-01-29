package com.bocom.bbip.gdeupsb.repository;

import com.bocom.bbip.data.domain.NoId;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.EupsLotAutPln;

public interface EupsLotAutPlnRepository extends PagingAndSortingRepository<EupsLotAutPln, NoId> {
	/**
	 * 定投计划取消，更新定投计划表状态
	 * @param eupsLotAutPln
	 */
	void updateStatus(EupsLotAutPln eupsLotAutPln);
	/**
	 * 定投计划执行，更新定投计划表执行结果
	 * @param eupsLotAutPln
	 */
	void updateResult(EupsLotAutPln eupsLotAutPln);
}