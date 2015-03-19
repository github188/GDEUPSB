package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.annotation.Update;
import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GdLotDrwTbl;

public interface GdLotDrwTblRepository extends PagingAndSortingRepository<GdLotDrwTbl, Long> {
    /**
     * 查询奖期信息表未返奖奖期信息
     * @param eupsLotDrwTbl
     * @return
     */
    List<GdLotDrwTbl> query(GdLotDrwTbl gdLotDrwTbl);
    /**
     * 查询快乐十分keno期都完成返奖的奖期信息
     * @param eupsLotDrwTbl
     * @return
     */
    List<GdLotDrwTbl> queryKeno(GdLotDrwTbl gdLotDrwTbl);
    /**
     * 查询快乐十分keno期未返奖的数量
     * @param eupsLotDrwTbl
     * @return
     */
    List<GdLotDrwTbl> queryKenoCnt(GdLotDrwTbl gdLotDrwTbl);
    /**
     * 汇总返奖金额
     * @param eupsLotDrwTbl
     * @return
     */
    Map<String,Object> sumPrzDrw(GdLotDrwTbl gdLotDrwTbl);
    /**
     * 更新汇总返奖金额
     * @param eupsLotDrwTbl
     * @return
     */
    void updDrwPrzInf(Map<String,Object> map);
    /**
     * 更新轧差
     * @param eupsLotDrwTbl
     * @return
     */
    void updLotDifAmt(GdLotDrwTbl gdLotDrwTbl);
    /**
     * 计算轧差
     * @param eupsLotDrwTbl
     * @return
     */
    List<GdLotDrwTbl> calcLotDifAmt(Map<String,Object> map);
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
    public List<GdLotDrwTbl> qryLotDrwInf(Map<String, Object> map);

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

    /** 清算奖期查询 */
    @Find
    public Page<GdLotDrwTbl> queryClearPrize(Pageable pageable,Map<String,String>map);
    /** 奖期查询 */
    @Find
    public Page<GdLotDrwTbl> queryPrize(Pageable pageable,Map<String,String>map);
    /**查询奖期信息*/
    @Find
    public List<GdLotDrwTbl>queryPrizeInfo(Map<String,String>map);
    
    /**检查奖期信息是否已经存在*/
    @Find
    public List<Map>checkPrizeInfo(Map<String,String>map);
    

    
    /** 查询返奖金额汇总 */
    @Find
    public Map<String, String>  findSumPrzDrw(GdLotDrwTbl gdLotDrwTbl);
    /** 更新汇总返奖信息 */
    @Update
    public void upateDrwPrzTbl(GdLotDrwTbl gdLotDrwTbl);
   
    /** 查询返奖轧差 */
    @Find
    public List<GdLotDrwTbl> qryLotDrwDifAmt();
    
    /** 更新更新轧差信息 */
    @Update
    public void UpdLotDifAmt(GdLotDrwTbl lotDrwTbl);


}