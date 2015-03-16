package com.bocom.bbip.gdeupsb.repository;



import java.util.Map;

import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsBetInfo;


public interface GDEupsBetInfoRepository extends PagingAndSortingRepository<GDEupsBetInfo, String>  {
	
    @Find
    public Page<GDEupsBetInfo>findInfo(Pageable pageable,Map<String,String> map);
    
   
}