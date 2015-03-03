package com.bocom.bbip.gdeupsb.repository;

import com.bocom.bbip.data.domain.NoId;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdeupsTlNoManager;

public interface GdeupsTlNoManagerRepository extends PagingAndSortingRepository<GdeupsTlNoManager, NoId> {
	/**更改密码*/
	public void updatePwd(GdeupsTlNoManager gdeupsTlNoManager);
}