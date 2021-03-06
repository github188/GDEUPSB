package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdEupsTransJournal;

public interface GdEupsTransJournalRepository extends PagingAndSortingRepository<GdEupsTransJournal, String> {

	/** 对账-广州电力缴费总信息查询 */
	public List<Map<String, Object>> findGzEleChkJFInfo(GdEupsTransJournal gdEupsTransJournal);

	/** 对账-广州电力缴费明细查询 */
	public List<Map<String, Object>> findGzEleChkJFDetail(GdEupsTransJournal gdEupsTransJournal);

	/** 对账-广州电力划扣明细查询 */
	public List<Map<String, Object>> findGzEleChkHKDetail(GdEupsTransJournal gdEupsTransJournal);

	/** 广州电力划扣总信息查询 */
	public List<Map<String, Object>> findGzEleChkHKInfo(GdEupsTransJournal gdEupsTransJournal);
	
	 /** 广州电力对公缴费记账回执打印*/
    public List<Map<String, Object>> findComPayInfo(Map<String, Object> inpara);

	@Find	
	public List<GdEupsTransJournal> findCheck(GdEupsTransJournal gdEupsTransJournal);

	
	/** 惠州燃气代扣流水信息查询*/
	@Find
	public List<GdEupsTransJournal> findGasJnlInfo(GdEupsTransJournal gdEupsTransJournal);
	

	/** 广东烟草流水信息查询*/
	@Find
    public List<Map<String, Object>> findTbcTransJournal(GdEupsTransJournal transJournal);
   
    /** 广东烟草清算信息查询*/
    @Find
    public List<Map<String, Object>> qryClearAccount(GdEupsTransJournal transJournal);
    
    public List<Map<String, Object>> findSumTxnAmt(GdEupsTransJournal transJournal);
    /** 广东烟草流水详细信息查询*/
    @Find
    public List<GdEupsTransJournal> findTbcTransJournalDetails(GdEupsTransJournal eupsTransJournal);
    
    @Find
    public Page<GdEupsTransJournal>getCheckInfo(Pageable pageable,Map<String,String>map);
    
    /** 报表打印 -全部*/
    @Find
	public List<Map<String, Object>> findAllTxnList(GdEupsTransJournal eupsJnl);
    
    /** 报表打印 -成功*/
    @Find
	public List<Map<String, Object>> findSuccTxnList(GdEupsTransJournal eupsJnl);
    
    /** 报表打印 -失败*/
    @Find
	public List<Map<String, Object>> findFailTxnList(GdEupsTransJournal eupsJnl);
    
    public List<Map<String, Object>> findElecFailTxnList(GdEupsTransJournal eupsJnl);
    
    /** 报表打印 -可疑*/
    @Find
	public List<Map<String, Object>> findDoubtTxnList(GdEupsTransJournal eupsJnl);
    
    public List<Map<String, Object>> findElecDoubtTxnList(GdEupsTransJournal eupsJnl);
    /** 报表打印 -其他*/
    @Find
	public List<Map<String, Object>> findOthTxnList(GdEupsTransJournal eupsJnl);

    public List<Map<String, Object>> findElecOthTxnList(GdEupsTransJournal eupsJnl);
    /** 流水信息查询*/
    @Find
	public List<Map<String, Object>> findTxnJnlInfo(GdEupsTransJournal txnJnl);
    
    /** 查询广州电力清算信息*/
	public List<Map<String, Object>> findGzClrJnl(GdEupsTransJournal txnJnl);
	  
    /** 财务对账:查询广州电力主机成功流水*/
	public List<Map<String, Object>> findGzEleHstSucJnl(Map<String, Object> para);
	
	/** 查找广州电力总笔数，金额 */
	public List<Map<String, Object>> findGzAllCntAmt(GdEupsTransJournal para);
	
	/** 查找广州电力缴费明确成功的汇总信息 */
	public List<Map<String, Object>> findGzSucJnlJF(GdEupsTransJournal para);
	
	/** 查找广州电力划扣明确成功的汇总信息 */
	public List<Map<String, Object>> findGzSucJnlHK(GdEupsTransJournal para);
	
	/** 查找广州电力明确失败的汇总信息 */
	public List<Map<String, Object>> findGzFalJnl(GdEupsTransJournal para);
	
	/** 查找广州电力存疑的汇总信息*/
	public List<Map<String, Object>> findGzUnsJnl(GdEupsTransJournal para);
	
	/** 查找广州电力汇总消息 */
	public List<Map<String, Object>> findGdJnlDtlTot(GdEupsTransJournal para);
	
	/** 查找广州电力所有的明细 */
	public List<Map<String, Object>> findGdJnlDetail(GdEupsTransJournal para);
	
	/** 查找广州电力当日明细 */
	public List<Map<String, Object>> findGdJnlAllDetail(GdEupsTransJournal para);
	
	/** 查找广州电力成功的划扣明细 */
	public List<Map<String, Object>> findGdJnlSucHkDetail(GdEupsTransJournal para);
	
	/** 查找广州电力成功的缴费明细 */
	public List<Map<String, Object>> findGdJnlSucJfDetail(GdEupsTransJournal para);
	
	/** 查找广州电力失败的明细 */
	public List<Map<String, Object>> findGdJnlFalDetail(GdEupsTransJournal para);
	
	/** 查找广州电力存疑的明细 */
	public List<Map<String, Object>> findGdJnlUnsDetail(GdEupsTransJournal para);
	
	/** 查找广州电力其他的明细 */
	public List<Map<String, Object>> findGdJnlOthDetail(GdEupsTransJournal para);
	
	/** 查找广州电力明细统计 */
	public List<Map<String, Object>> findGdJnlDetailAll(GdEupsTransJournal para);

	/** 燃气单笔代扣成功流水 */
	@Find
	public List<Map<String, Object>> findGasSucJnlInfo(Map<String, Object> detailMap);
	/** 燃气单笔代扣失败流水 */
	@Find
	public List<Map<String, Object>> findGasFalJnlInfo(Map<String, Object> detailMap);
	/** 燃气单笔代扣汇总流水 */
	@Find
	public List<Map<String, Object>> findGasAllJnlInfo(
			Map<String, Object> detailMap);
	@Find
	public List<Map<String, Object>> findRptJnlInfo(GdEupsTransJournal eupsJnl);
	@Find
	public List<Map<String, Object>> findGasCheckRec(Map<String, Object> baseMap);
	@Find
	public List<Map<String, Object>> findElec00AllTxnList(
			GdEupsTransJournal eupsJnl);

	public Page<Map<String, Object>> findTxnJnlInfo(Pageable pageable,
			GdEupsTransJournal txnJnl);
	@Find
	public List<Map<String, Object>> findGasSuccTxnList(
			GdEupsTransJournal eupsJnl);
	@Find
	public List<Map<String, Object>> findElec00SusList(
			GdEupsTransJournal eupsJnl);
	@Find
	public List<Map<String, Object>> findEfekRptJnlInfo(
			GdEupsTransJournal eupsJnl);
	
	

}