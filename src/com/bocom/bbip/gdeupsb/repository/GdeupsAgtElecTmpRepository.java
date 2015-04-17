package com.bocom.bbip.gdeupsb.repository;


import java.util.List;

import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdeupsAgtElecTmp;



public interface GdeupsAgtElecTmpRepository 
	extends 
PagingAndSortingRepository<GdeupsAgtElecTmp, String>
 {
	@Find
	public List<GdeupsAgtElecTmp> findBase(GdeupsAgtElecTmp agtElec);
	
	@Find
	public void deleteByAc(GdeupsAgtElecTmp agtElec);
	
	@Find
	public void updateByAc(GdeupsAgtElecTmp agtElec);
	
	
	
	
}