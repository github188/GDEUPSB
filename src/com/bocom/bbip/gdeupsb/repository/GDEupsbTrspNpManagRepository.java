package com.bocom.bbip.gdeupsb.repository;

import java.util.List;

import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspNpManag;

public interface GDEupsbTrspNpManagRepository extends PagingAndSortingRepository<GDEupsbTrspNpManag, String> {
	
	public List<Integer> findCountByStatus(GDEupsbTrspNpManag gdeupsb);
	public List<Integer> findCount(GDEupsbTrspNpManag gdeupsb);
	public List<Integer> findCountSum(GDEupsbTrspNpManag gdeupsb);
	 public Page<GDEupsbTrspNpManag> findNpInfo(Pageable pageable, GDEupsbTrspNpManag gdeupsb);
	    public List<GDEupsbTrspNpManag> findNpInfo(GDEupsbTrspNpManag gdeupsb);
	    public void updateSt(GDEupsbTrspNpManag gdeupsb);
	    public void update(GDEupsbTrspNpManag gdeupsb);
}