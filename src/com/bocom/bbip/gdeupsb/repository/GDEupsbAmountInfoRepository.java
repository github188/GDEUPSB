package com.bocom.bbip.gdeupsb.repository;

import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsbAmountInfo;

public interface GDEupsbAmountInfoRepository extends PagingAndSortingRepository<GDEupsbAmountInfo, String> {

	public void deleteByCarNo(GDEupsbAmountInfo eupsAmountInfo);

}