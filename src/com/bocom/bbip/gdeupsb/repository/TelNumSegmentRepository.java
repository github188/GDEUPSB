package com.bocom.bbip.gdeupsb.repository;

import java.util.List;

import com.bocom.bbip.data.domain.NoId;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.TelNumSegment;

public interface TelNumSegmentRepository extends PagingAndSortingRepository<TelNumSegment, NoId> {
	/** 通过电话号码找到该地区*/
	public List<TelNumSegment> findAreaid(TelNumSegment telNumSegment);
}