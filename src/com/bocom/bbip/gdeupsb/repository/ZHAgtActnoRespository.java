package com.bocom.bbip.gdeupsb.repository;

import java.util.List;

import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.ZHAgtActno;

public interface ZHAgtActnoRespository extends PagingAndSortingRepository<ZHAgtActno, String> {
	
	//更新一本通主帐号到实体帐号
	@Find
	public List<ZHAgtActno> queryNewByOld(final String oldActNo);
	
	//更新实体帐号到一本通主帐号
	@Find
	public List<ZHAgtActno> queryOldByNew(final String ActNo);
}
