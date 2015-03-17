package com.bocom.bbip.gdeupsb.repository;

import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsEleTmp;

public interface GDEupsEleTmpRepository extends PagingAndSortingRepository<GDEupsEleTmp, String> {
		public void deleteAll(String del);
		
		public void updateInfo(String cusAc,String thdCusNo);
}