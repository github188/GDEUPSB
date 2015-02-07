package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdElecFileBatchTmp;

public interface GdElecFileBatchTmpRepository extends PagingAndSortingRepository<GdElecFileBatchTmp, String> {
		
	/** 获取代收付文件信息 */
	public List<Map<String, Object>> findEleAgtFileTmp(GdElecFileBatchTmp gdElecFileBatchTmp);


}