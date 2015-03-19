package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.annotation.Delete;
import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.annotation.Insert;
import com.bocom.bbip.data.annotation.Update;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.ZHActNoInf;

public interface ZHActNoInfRepository extends PagingAndSortingRepository<ZHActNoInf, String>{
	
	//更新一本通帐号到主帐号－实体帐号
	@Find
	public List<ZHActNoInf> queryNewByOld(Map<String,String> map);
	@Delete
	public void deleteInfo(Map<String,String> map);
	@Update
	public void updateInfo(Map<String,String> map);
	@Insert
	public void insertInfo(ZHActNoInf info);
}
