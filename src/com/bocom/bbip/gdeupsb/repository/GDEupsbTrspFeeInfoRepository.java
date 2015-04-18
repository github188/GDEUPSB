package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;

public interface GDEupsbTrspFeeInfoRepository extends PagingAndSortingRepository<GDEupsbTrspFeeInfo, String> {
	public void update(GDEupsbTrspFeeInfo gdeupsb);

	public void updateStatus(GDEupsbTrspFeeInfo gdeupsb);

	public int findInfoCount(GDEupsbTrspFeeInfo gdeupsb);

	public void updateThdFeeInfo(GDEupsbTrspFeeInfo gdeupsb);

	public void updateCancelSt(GDEupsbTrspFeeInfo gdeupsb);

	public Page<GDEupsbTrspFeeInfo> findInfo(Pageable pageable, GDEupsbTrspFeeInfo gdeupsb);

	public List<GDEupsbTrspFeeInfo> findInfo(GDEupsbTrspFeeInfo gdeupsb);

	public List<GDEupsbTrspFeeInfo> findLogNo(GDEupsbTrspFeeInfo gdeupsb);

	public List<GDEupsbTrspFeeInfo> findForCancel(GDEupsbTrspFeeInfo gdeupsb);

	public void updateChkFlg(String tChkNo);

	public List<GDEupsbTrspFeeInfo> findNotCheck(String tChkNo);

	public List<Map<String, Object>> findSumForTxnAmt(String tChkNo);

	public void updateSt(GDEupsbTrspFeeInfo gdeupsb);

	public List<GDEupsbTrspFeeInfo> findOrder(GDEupsbTrspFeeInfo gdeupsb);

	
	public List<GDEupsbTrspFeeInfo> find0(Object map);
	
	public List<Map<String, Object>> findSum(Object map);

	
	public GDEupsbTrspFeeInfo findOneByTlogNo(String tlogNo);

	public List<GDEupsbTrspFeeInfo> findNoPrintList(Map<String, Object> map);
	
	public List<Map<String, Object>> findStatuesDeatil(Map<String, Object> map);
	
	public void updateMsgStatue(Map<String, Object> map);
	
	
}