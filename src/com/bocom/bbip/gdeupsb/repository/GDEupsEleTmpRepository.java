package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsEleTmp;

public interface GDEupsEleTmpRepository extends PagingAndSortingRepository<GDEupsEleTmp, String> {
		
		public void deleteAll(String del);
		
		public void updateInfo(String cusAc,String thdCusNo);
		
		public List<GDEupsEleTmp> findAllOrderBySqn(String batNo);
		
		public List<GDEupsEleTmp> findFail(Map<String, Object> map);
		
		public void updateOne(GDEupsEleTmp gdEupsEleTmp);
}