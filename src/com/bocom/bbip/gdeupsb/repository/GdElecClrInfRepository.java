package com.bocom.bbip.gdeupsb.repository;

import java.util.List;

import com.bocom.bbip.data.annotation.Update;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdElecClrInf;

public interface GdElecClrInfRepository extends PagingAndSortingRepository<GdElecClrInf, String> {
	/** 获取清算信息 */
	public List<GdElecClrInf> findClrInf(GdElecClrInf inpara);
	
	/** 更新清算日期 */
	@Update
	public void updClrDte(GdElecClrInf inpara);
}