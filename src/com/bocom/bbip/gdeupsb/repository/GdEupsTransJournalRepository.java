package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.EupsTransJournalELEC02Check;
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
    public Page<EupsTransJournalELEC02Check>getCheckInfo(Pageable pageable,Map<String,String>map);

}