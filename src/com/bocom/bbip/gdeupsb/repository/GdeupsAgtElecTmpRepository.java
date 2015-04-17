package com.bocom.bbip.gdeupsb.repository;


import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdeupsAgtElecTmp;



public interface GdeupsAgtElecTmpRepository 
	extends 
PagingAndSortingRepository<GdeupsAgtElecTmp, String>
 {
	@Find
	public GdeupsAgtElecTmp findBase(GdeupsAgtElecTmp agtElec);
}