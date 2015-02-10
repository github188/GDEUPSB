package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTransJournal;
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
	public List<GDEupsbTransJournal> findCheck(GDEupsbTransJournal gdEupsTransJournal);

	
	/** 惠州燃气代扣流水信息查询*/
	public List<GdEupsTransJournal> findGasJnlInfo(GdEupsTransJournal gdEupsTransJournal);

	/** 广东烟草流水信息查询*/
    public List<GdEupsTransJournal> findTbcTransJournals(GdEupsTransJournal transJournal);
    /** 广东烟草流水表信息查询*/
    public List<GdEupsTransJournal> findTbcTransJnls(GdEupsTransJournal transJournal);

}