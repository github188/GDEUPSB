package com.bocom.bbip.gdeupsb.repository;

import java.util.List;

import com.bocom.bbip.data.domain.NoId;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.EupsLotDrwTbl;

public interface EupsLotDrwTblRepository extends PagingAndSortingRepository<EupsLotDrwTbl, NoId> {
	/**
	 * 查询奖期信息表未返奖奖期信息
	 * @param eupsLotDrwTbl
	 * @return
	 */
	List<EupsLotDrwTbl> query(EupsLotDrwTbl eupsLotDrwTbl);
	/**
	 * 更新返奖流程控制标志
	 * @param eupsLotDrwTbl
	 */
	void updateFlwCtl(EupsLotDrwTbl eupsLotDrwTbl);
	/**
	 * 更新返奖垫付标志、返奖金额
	 * @param eupsLotDrwTbl
	 */
	void updatePayFlg(EupsLotDrwTbl eupsLotDrwTbl);
	/**
	 * 更新返奖垫付标志、返奖金额
	 * @param eupsLotDrwTbl
	 */
	void updatePrzAmt(EupsLotDrwTbl eupsLotDrwTbl);
}