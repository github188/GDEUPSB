package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.annotation.Update;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdLotDrwTbl;
import com.bocom.bbip.gdeupsb.entity.GdLotDrwTblKey;

public interface GdLotDrwTblRepository extends PagingAndSortingRepository<GdLotDrwTbl, GdLotDrwTblKey> {
	/**
	 * 查询奖期信息表未返奖奖期信息
	 * @param eupsLotDrwTbl
	 * @return
	 */
	List<GdLotDrwTbl> query(GdLotDrwTbl gdLotDrwTbl);
	/**
	 * 更新返奖流程控制标志
	 * @param eupsLotDrwTbl
	 */
	void updateFlwCtl(GdLotDrwTbl gdLotDrwTbl);
	/**
	 * 更新返奖垫付标志、返奖金额
	 * @param eupsLotDrwTbl
	 */
	void updatePayFlg(GdLotDrwTbl gdLotDrwTbl);
	/**
	 * 更新返奖垫付标志、返奖金额
	 * @param eupsLotDrwTbl
	 */
	void updatePrzAmt(GdLotDrwTbl gdLotDrwTbl);

	@Update
	public void update(GdLotDrwTbl lotDrwTbl);
	
	@Find
	public GdLotDrwTbl qryLotDrwInf(Map<String, Object> map);
	
	
	/** 福彩清算-查询汇总垫付信息 */
	public List<Map<String, Object>> findTolPayInf(GdLotDrwTbl gdLotDrwTbl);
	
	/** 福彩清算-统计轧差金额信息 */
	public List<Map<String, Object>> findTolDifInf(GdLotDrwTbl gdLotDrwTbl);
	
	/** 福彩清算-检查是否可以进行清算 */
	public List<String> findChkClrFlg(GdLotDrwTbl gdLotDrwTbl);
	
	/** 福彩清算-更新垫付状态  */
	public void updateTolPayInf(GdLotDrwTbl gdLotDrwTbl);
	
	/** 福彩清算-更新轧差状态  */
	public void updateTolDifInf(GdLotDrwTbl gdLotDrwTbl);
   
	/** 福彩查询奖期信息表  */
	@Find
    public List<GdLotDrwTbl> qryUnPrzDrw();
	
	/** 查询福彩奖期信息总数 */
    @Find
    public Map<String, String> statUnPrzDrw(GdLotDrwTbl gdLotDrwTbl);

}