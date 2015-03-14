package com.bocom.bbip.gdeupsb.repository;

import java.util.List;

import com.bocom.bbip.data.domain.NoId;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdeupsTyfInvRec;

public interface GdeupsTyfInvRecRepository extends PagingAndSortingRepository<GdeupsTyfInvRec, NoId> {
	public List<Integer> findCount(GdeupsTyfInvRec gdeupsTyfInvRec);
	public void delete1(GdeupsTyfInvRec gdeupsTyfInvRec);
}