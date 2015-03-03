package com.bocom.bbip.gdeupsb.repository;



import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.AreaInfo;


public interface AreaInfoRepository extends PagingAndSortingRepository<AreaInfo, String>  {
	
    @Find
    public Page<AreaInfo>findByAreaName(Pageable pageable,String AreaNam);
    
   
}