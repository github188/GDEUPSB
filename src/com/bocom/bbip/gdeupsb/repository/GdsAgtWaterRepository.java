package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdsAgtWater;
import com.bocom.bbip.gdeupsb.entity.GdsAgtWaterKey;

public interface GdsAgtWaterRepository extends PagingAndSortingRepository<GdsAgtWater, GdsAgtWaterKey> {

	/** 签约一站通-签约明细查询 */
	public List<Map<String, Object>> findSignDeatilInfo(Map<String, Object> inpara);

	/** 签约一站通-多页查询签约明细 */
	public Page<Map<String, Object>> findSignDeatilList(Pageable pageable, Map<String, Object> inpara);

	public List<Map<String, Object>> findSignDeatilList(Object inpara);

	/** 签约一站通-协议维护：协议信息查询 */
	public List<Map<String, Object>> findSignDeatilForQry(Map<String, Object> inpara);
	
	/** 签约一站通-协议维护：将原子表信息作废 */
	public void updateOldAgtInfCnl(Map<String, Object> inpara);
	
	/** 签约一站通-协议维护：检查用户编号是否已存在*/
	public List<Map<String,Object>> findCusIsExist(Map<String, Object> inpara);
	
	/** 签约一站通-协议维护：获取协议子表原协议信息*/
	public List<Map<String,Object>> findOldAgtInf(Map<String, Object> inpara);
	
	/** 签约一站通-协议维护：更新协议子表原协议信息*/
	public void updateOldAgtInf(Map<String, Object> inpara);
	
	/** 签约一站通-协议维护：新增协议子表协议信息*/
	public void insertDetailAgtInf(Map<String, Object> inpara);
	
	/** 签约一站通-查找拷盘数据*/
	public List<Map<String, Object>> findFileSndInfo(Map<String, Object> inpara);
	
	/** 签约一站通-查找拷盘数据(广州有线)*/
	public List<Map<String, Object>> findFileSndInfoTel(Map<String, Object> inpara);
	
	
	/** 签约一站通-更新该批次的批次号，并设置为不可制盘*/
	public void updateBchUsbFlg(Map<String, Object> inpara);
	
	/** 签约一站通-协议数据拷盘异常处理：查询是否有该批次的数据*/
	public List<Integer> findBatchCnt(Map<String, Object> inpara);
	
	/** 签约一站通-协议数据拷盘异常处理：更新制盘信息*/
	public void updateFleSndInf(Map<String, Object> inpara);
	
	/** 签约一站通-代理协议校验:获取协议表数据*/
	public List<Map<String, Object>> findAgtCheckInf(Map<String, Object> inpara);
	
	/** 签约一站通-代理协议校验:更新协议子表*/
	public void updateAgtChkSts(Map<String, Object> inpara);
	
	/** 签约一站通-协议更新:更新协议子表处理状态*/
	public void updateAgtWtrDelSts(Map<String, Object> inpara);
	
	/** 签约一站通-协议更新:更新协议子表所有协议处理状态*/
	public void updateAgtWtrDelStsAll(Map<String, Object> inpara);
	
	/** 签约一站通-协议更新:更新协议子表处理状态-珠江数码*/
	public void updateAgtDegDelSts(Map<String, Object> inpara);
	
	
	/** 签约一站通-协议更新:统计批次数量(待删除)*/
	public List<Integer> findBatchAgtCnt(GdsAgtWater gdsAgtWater);
	
	/** 签约一站通-协议更新:统计批次数量*/
	public List<Integer> findBatchAgtCntAll(Map<String, Object> inpara);
	
	
	/** 根据回盘标识查询协议记录*/
	public List<String> findActInfUsbFlg(Map<String, Object> inpara);
	
	/** 更新协议子表为可制盘*/
	public void updateBatchUsbFlg(Map<String, Object> inpara);
	
	/** 更新协议子表为可制盘*/
	public void updateBatchUsbFlgTel(Map<String, Object> inpara);
	

	/** 广东移动签约一站通-协议维护：将原子表信息作废  add by tandun*/
	public void updateGdmobbOldAgtInf(Map<String, Object> inpara);
	
	/**广东移动 签约一站通-协议维护：新增协议子表协议信息 add by tandun*/
	public void insertgdmobbDetailAgtInf(Map<String, Object> inpara);
	
	/** 广东联通签约一站通-协议维护：将原子表信息作废 add by tandun */
	public void updateGduncbOldAgtInf(Map<String, Object> inpara);
	
	/**广东联通 签约一站通-协议维护：新增协议子表协议信息 add by tandun*/
	public void insertgduncbDetailAgtInf(Map<String, Object> inpara);
	/**验证是否已签约*/
	public List<Map<String, Object>> findExist(Map<String, Object> inpara);
	
	/**  更新协议子表状态消息*/
	public void updateStbInf(Map<String, Object> inpara);
}