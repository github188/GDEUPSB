package com.bocom.bbip.gdeupsb.service.impl.lotr01;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GdLotAwdDtl;
import com.bocom.bbip.gdeupsb.entity.GdLotDrwTbl;
import com.bocom.bbip.gdeupsb.entity.GdLotPrzCtl;
import com.bocom.bbip.gdeupsb.entity.GdLotPrzDtl;
import com.bocom.bbip.gdeupsb.entity.GdLotPrzInf;
import com.bocom.bbip.gdeupsb.repository.GdLotAwdDtlRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotDrwTblRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotPrzCtlRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotPrzDtlRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotPrzInfRepository;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.ServiceAccessObject;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * 定投返奖
 * @author hefengwen
 *
 */
public class ReturnPlanPayServiceActionLOTR01 extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ReturnPlanPayServiceActionLOTR01.class);
	
	@Autowired
	BGSPServiceAccessObject a;
	@Autowired
	ServiceAccessObject b;
	
	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;
	
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("ReturnPlanPayStrategyActionLOTR01 start ... ...");
		//TODO:获取福彩时间，测试时默认与系统日期相同
		String fcTim = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss);
		
		GdLotDrwTbl gdLotDrwTbl = new GdLotDrwTbl();
		//查询奖期信息表未返奖奖期，从奖期信息表中获取福彩时间比销售结束时间大，且返奖流程控制
		//为非10(返奖完成)的记录，按先7快乐十分5双色球、期号最小排序
		gdLotDrwTbl.setCshStr(fcTim);
		List<GdLotDrwTbl> gdLotDrwTbls = get(GdLotDrwTblRepository.class).query(gdLotDrwTbl);
		//按奖期进行返奖
		for(GdLotDrwTbl drw:gdLotDrwTbls){
			int flwCtl = Integer.parseInt(drw.getFlwCtl());
			logger.info("游戏编号["+drw.getGameId()+"]期号["+drw.getDrawId()	+"]Keno期号["+drw.getKenoId()+"]返奖流程状态flwctl:["+flwCtl+"]");
			switch(flwCtl){
			//返奖流程初始状态
			case 0:
				logger.info("游戏编号["+drw.getGameId()+"]期号["+drw.getDrawId()	+"]Keno期号["+drw.getKenoId()+"]开始执行第["+flwCtl+"]步：初始状态");
				flwCtl++;
				drw.setFlwCtl(flwCtl+"");
				get(GdLotDrwTblRepository.class).updateFlwCtl(drw);
			//开奖公告下载开始
			/*检查开奖公告表对应的游戏编号、期号、keno期号对应的开奖公告是否下载
			 * 是，继续执行
			 * 否，从福彩中心下载开奖公告，下载开奖公告成功，更新流程状态为2，如未开奖，循环到下一个奖期
			 */
			case 1:
				logger.info("游戏编号["+drw.getGameId()+"]期号["+drw.getDrawId()	+"]Keno期号["+drw.getKenoId()+"]开始执行第["+flwCtl+"]步：开奖公告下载开始");
				GdLotPrzInf gdLotPrzInf = new GdLotPrzInf();
				gdLotPrzInf.setGameId(drw.getGameId());
				gdLotPrzInf.setDrawId(drw.getDrawId());
				gdLotPrzInf.setKenoId(drw.getKenoId());
				List<GdLotPrzInf> eupsLotPrzInfs = get(GdLotPrzInfRepository.class).find(gdLotPrzInf);
				if(eupsLotPrzInfs==null||eupsLotPrzInfs.size()==0){
					//TODO:无记录，从福彩中心下载开奖公告
					
					Map<String,Object> thdReturnMessage = callThdTradeManager.trade(context);
					if(context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)){
						CommThdRspCdeAction commThdRspCdeAction = new CommThdRspCdeAction();
						String responseCode = commThdRspCdeAction.getThdRspCde(thdReturnMessage, context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
						if(responseCode.equals(Constants.RESPONSE_CODE_SUCC)){
							List<Map<String,Object>> prizeItems = context.getData("prize");
							for(Map<String,Object> prizeItem:prizeItems){
								@SuppressWarnings("unchecked")
								List<Map<String,Object>> classes = (List<Map<String, Object>>) prizeItem.get("classes");
								for(Map<String,Object> c:classes){
//									context.setData("gameid", drw.getGameid());
//									context.setData("drawid", drw.getDrawid());
//									context.setData("kenoid", drw.getKenoid());
//									context.setData("clsnam", c.get("clsnam"));
//									context.setData("bonamt", c.get("bonamt"));
//									context.setData("bonnum", c.get("bonnum"));
//									eupsLotPrzInf = BeanUtils.toObject(context.getDataMap(), EupsLotPrzInf.class);
									
									gdLotPrzInf.setGameNm(context.getData("gameNm").toString());
									gdLotPrzInf.setDrawNm(context.getData("drawNm").toString());
									gdLotPrzInf.setKenoNm(context.getData("kenoNm").toString());
									gdLotPrzInf.setStrTim(context.getData("kenoNm").toString());
									gdLotPrzInf.setStpTim(context.getData("kenoNm").toString());
									gdLotPrzInf.setTotPrz(context.getData("kenoNm").toString());
									gdLotPrzInf.setJacPot(context.getData("kenoNm").toString());
									gdLotPrzInf.setOpnTot(context.getData("kenoNm").toString());
									gdLotPrzInf.setOpnNum(prizeItem.get("kenoNm").toString());
									gdLotPrzInf.setBonCod(prizeItem.get("kenoNm").toString());
									gdLotPrzInf.setClsNum(prizeItem.get("kenoNm").toString());
									gdLotPrzInf.setClsNam(c.get("kenoNm").toString());
									gdLotPrzInf.setBonAmt(c.get("kenoNm").toString());
									gdLotPrzInf.setBonNum(c.get("kenoNm").toString());
									get(GdLotPrzInfRepository.class).insert(gdLotPrzInf);
								}
							}
						}else{
							//TODO:系统异常，发送短信给维护人员
							String message = "银福通系统游戏编号"+drw.getGameId()+"期号"+drw.getDrawId()+"小期号"+drw.getKenoId()+"定时返奖交易第1步，新增开奖公告信息失败，定投执行程序退出，请检查！";
							
							throw new CoreException(GDErrorCodes.EUPS_LOTR01_09_ERROR);
						}
					}else{
						//TODO:系统异常，发送短信给维护人员
						String message = "银福通系统游戏编号"+drw.getGameId()+"期号"+drw.getDrawId()+"小期号"+drw.getKenoId()+"定时返奖交易第1步，查询福彩中心开奖公告通讯异常，定投执行程序退出，请检查！";

						throw new CoreException(GDErrorCodes.EUPS_LOTR01_10_ERROR);
					}
				}
				flwCtl++;
				drw.setFlwCtl(flwCtl+"");
				get(GdLotDrwTblRepository.class).updateFlwCtl(drw);
			case 2:
				logger.info("游戏编号["+drw.getGameId()+"]期号["+drw.getDrawId()	+"]Keno期号["+drw.getKenoId()+"]开始执行第["+flwCtl+"]步：开奖公告下载完成");
				
				flwCtl++;
				drw.setFlwCtl(flwCtl+"");
				get(GdLotDrwTblRepository.class).updateFlwCtl(drw);
			/*检查中奖记录表对应的游戏编号、期号、keno期号记录是否下载
			 * 是，继续执行
			 * 否，中奖记录下载，双色球是下载文件导入，快乐十分是报文下载
			 */
			case 3:
				logger.info("游戏编号["+drw.getGameId()+"]期号["+drw.getDrawId()	+"]Keno期号["+drw.getKenoId()+"]开始执行第["+flwCtl+"]步：中奖文件下载开始");
				GdLotPrzCtl gdLotPrzCtl = new GdLotPrzCtl();
				gdLotPrzCtl.setGameId(drw.getGameId());
				gdLotPrzCtl.setDrawId(drw.getDrawId());
				gdLotPrzCtl.setKenoId(drw.getKenoId());
				List<GdLotPrzCtl> gdLotPrzCtls = get(GdLotPrzCtlRepository.class).find(gdLotPrzCtl);
				if(gdLotPrzCtls==null||gdLotPrzCtls.size()==0){
					if("7".equals(gdLotPrzCtl.getGameId())){//快乐十分
						//TODO:发送第三方获取快乐十分中奖信息
						
						Map<String,Object> thdReturnMessage = callThdTradeManager.trade(context);
						if(context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)){
							CommThdRspCdeAction commThdRspCdeAction = new CommThdRspCdeAction();
							String responseCode = commThdRspCdeAction.getThdRspCde(thdReturnMessage, context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
							if(responseCode.equals(Constants.RESPONSE_CODE_SUCC)){
								List<Map<String,Object>> items = context.getData("items");
								for(Map<String,Object> item:items){
									//新增中奖记录控制表
									gdLotPrzCtl.setCipher(item.get("cipher").toString());
									gdLotPrzCtl.setBigBon(item.get("bigBon").toString());
									gdLotPrzCtl.setTotPrz(item.get("totPrz").toString());
									gdLotPrzCtl.setTxnlog(item.get("txnLog").toString());
									gdLotPrzCtl.settLogNo(item.get("tLogNo").toString());
									gdLotPrzCtl.setTermId(item.get("termID").toString());
									get(GdLotPrzCtlRepository.class).insert(gdLotPrzCtl);
									
									List<Map<String,Object>> nodes = context.getData("nodes");
									for(Map<String,Object> node:nodes){
										GdLotPrzDtl gdLotPrzDtl = new GdLotPrzDtl();
										
										gdLotPrzDtl.setGameId(gdLotPrzCtl.getGameId());
										gdLotPrzDtl.setDrawId(gdLotPrzCtl.getDrawId());
										gdLotPrzDtl.setKenoId(gdLotPrzCtl.getKenoId());
										gdLotPrzDtl.setTxnLog(gdLotPrzCtl.getTxnlog());
										gdLotPrzDtl.settLogNo(gdLotPrzCtl.gettLogNo());
										gdLotPrzDtl.setBetMod(node.get("betMod").toString());
										gdLotPrzDtl.setBetMul(node.get("betMul").toString());
										gdLotPrzDtl.setClassNo(node.get("cls").toString());
										gdLotPrzDtl.setPrzAmt(node.get("przAmt").toString());
										gdLotPrzDtl.setBetLin(node.get("betLin").toString());
										get(GdLotPrzDtlRepository.class).insert(gdLotPrzDtl);
									}
								}
							}else{
								//TODO:系统异常，发送短信给维护人员
								String message = "银福通系统游戏编号"+drw.getGameId()+"期号"+drw.getDrawId()+"小期号"+drw.getKenoId()+"定时返奖交易第3步，新增中奖明细异常，定投执行程序退出，请检查！";
								
								throw new CoreException(GDErrorCodes.EUPS_LOTR01_11_ERROR);
							}
						}else{
							//TODO:系统异常，发送短信给维护人员
							String message = "银福通系统游戏编号"+drw.getGameId()+"期号"+drw.getDrawId()+"小期号"+drw.getKenoId()+"定时返奖交易第3步，下载福彩中心中奖信息通讯异常，定投执行程序退出，请检查！";

							throw new CoreException(GDErrorCodes.EUPS_LOTR01_12_ERROR);
						}
					}else{//双色球
						//TODO:发送报文查询下载地址
						
						//TODO:下载文件
						
						//TODO:文件入库
						
					}
				}
				flwCtl++;
				drw.setFlwCtl(flwCtl+"");
				get(GdLotDrwTblRepository.class).updateFlwCtl(drw);
			/*检查是否存在中奖记录
			 * 无，直接到10
			 * 有，执行返奖流程
			 */
			case 4:
				logger.info("游戏编号["+drw.getGameId()+"]期号["+drw.getDrawId()	+"]Keno期号["+drw.getKenoId()+"]开始执行第["+flwCtl+"]步");
				gdLotPrzCtl = new GdLotPrzCtl();
				gdLotPrzCtl.setGameId(drw.getGameId());
				gdLotPrzCtl.setDrawId(drw.getDrawId());
				gdLotPrzCtl.setKenoId(drw.getKenoId());
				gdLotPrzCtls = get(GdLotPrzCtlRepository.class).find(gdLotPrzCtl);
				if(gdLotPrzCtls==null||gdLotPrzCtls.size()==0){
					flwCtl = 10;
					drw.setFlwCtl(flwCtl+"");
					get(GdLotDrwTblRepository.class).updateFlwCtl(drw);
					//更新返奖垫付标志、返奖垫付金额、返奖款划拨标志
					String payflg = "0";//0:没垫付
					String payamt = "0";//返奖垫付金额为0
					String xfeflg = "0";//0:无返奖划拨
					String przamt = "0";//当期返奖总金额为0
//					EupsLotDrwTbl eupsLotDrwTbl = new EupsLotDrwTbl();
//					eupsLotDrwTbl.setGameid(drw.getGameid());
//					eupsLotDrwTbl.setDrawid(drw.getDrawid());
//					eupsLotDrwTbl.setKenoid(drw.getKenoid());
					drw.setPayFlg(payflg);
					drw.setPayAmt(payamt);
					drw.setXfeFlg(xfeflg);
					drw.setPrzAmt(przamt);
					get(GdLotDrwTblRepository.class).updatePayFlg(drw);
					get(GdLotDrwTblRepository.class).updatePrzAmt(drw);
					break;
				}
				flwCtl++;
				drw.setFlwCtl(flwCtl+"");
				get(GdLotDrwTblRepository.class).updateFlwCtl(drw);
			/*检查返奖记录明细表的游戏编号、期号、keno期号对应记录已经插入
			 * 是，继续执行
			 * 否，根据游戏编号、期号、keno期号、投注交易流水号匹配投注交易流水表和
			 * 中奖记录控制表，将返奖记录插入返奖记录明细表，检查重复交易流水号返奖
			 * 记录情况，更新返奖表的返奖总金额字段，导入完成改为6
			 */
			case 5:
				logger.info("游戏编号["+drw.getGameId()+"]期号["+drw.getDrawId()	+"]Keno期号["+drw.getKenoId()+"]开始执行第["+flwCtl+"]步");
				GdLotAwdDtl gdLotAwdDtl = new GdLotAwdDtl();
				gdLotAwdDtl.setGameId(drw.getGameId());
				gdLotAwdDtl.setDrawId(drw.getDrawId());
				gdLotAwdDtl.setKenoId(drw.getKenoId());
				List<GdLotAwdDtl> gdLotAwdDtls = get(GdLotAwdDtlRepository.class).find(gdLotAwdDtl);
				if(gdLotAwdDtls!=null&&gdLotAwdDtls.size()!=0){
					//TODO:不应该有记录，如果有，发送短信给维护人员
					String message = "银福通系统游戏编号"+drw.getGameId()+"期号"+drw.getDrawId()+"小期号"+drw.getKenoId()+"定时返奖交易第5步，检查返奖记录表有返奖记录存在异常，定投执行程序退出，请检查！";
					
					throw new CoreException(GDErrorCodes.EUPS_LOTR01_13_ERROR);
				}else{
					//TODO:将中奖信息插入返奖记录明细表
					get(GdLotAwdDtlRepository.class).insertAwdDtl(gdLotAwdDtl);
					//TODO:查看有无返奖记录
					gdLotAwdDtls = get(GdLotAwdDtlRepository.class).find(gdLotAwdDtl);
					if(gdLotAwdDtls==null||gdLotAwdDtls.size()==0){
						//无小奖记录
						//TODO:统计返奖总金额
						Map<String,Object> amtMap = get(GdLotAwdDtlRepository.class).sumAmt(gdLotAwdDtl);
						String przamt =  amtMap.get("przamt")==null?"0":amtMap.get("przamt").toString();
						drw.setPrzAmt(przamt);
						String payflg = "0";//0:未垫付
						String payamt="0";//返奖垫付金额
						drw.setPayFlg(payflg);
						drw.setPayAmt(payamt);
						get(GdLotDrwTblRepository.class).updatePayFlg(drw);
						get(GdLotDrwTblRepository.class).updatePrzAmt(drw);
						flwCtl = 10;
						drw.setFlwCtl(flwCtl+"");
						get(GdLotDrwTblRepository.class).updateFlwCtl(drw);
						break;
					}
					//TODO:检查同意游戏编号、期号、投注流水号是否有多条记录存在
					Map<String,Object> dupMap = get(GdLotAwdDtlRepository.class).checkTxnLog();
					if(dupMap!=null){
						String dupnum = dupMap.get("dupnum").toString().trim();
						if(Integer.parseInt(dupnum)>1){
							//TODO:有重复流水
							String message = "银福通系统游戏编号"+drw.getGameId()+"期号"+drw.getDrawId()+"小期号"+drw.getKenoId()+"定时返奖交易第5步，返奖记录存在同一投注流水号有重复，定投执行程序退出，请检查！";
							
							
						}
					}
					//统计返奖总金额
					Map<String,Object> amtMap = get(GdLotAwdDtlRepository.class).sumAmt(gdLotAwdDtl);
					String przAmt = amtMap.get("przamt").toString();
					logger.info("返奖总金额["+przAmt+"]");
					drw.setPrzAmt(przAmt);
					get(GdLotDrwTblRepository.class).updatePrzAmt(drw);
					//TODO:更新流水表中奖标志
					String awdFlg = "1";
//					EupsLotTxnJnl eupsLotTxnJnl = new EupsLotTxnJnl();
					
					
					
				}
				flwCtl++;
				drw.setFlwCtl(flwCtl+"");
				get(GdLotDrwTblRepository.class).updateFlwCtl(drw);
			//插入返奖明细结束
			case 6:
				logger.info("游戏编号["+drw.getGameId()+"]期号["+drw.getDrawId()	+"]Keno期号["+drw.getKenoId()+"]开始执行第["+flwCtl+"]步");
				
				flwCtl++;
				drw.setFlwCtl(flwCtl+"");
				get(GdLotDrwTblRepository.class).updateFlwCtl(drw);
			/*返奖资金划拨开始
			 * 1.更新奖期信息表的返奖流程控制标志为7
			 * 2.查询购彩内部账户余额，并比较是否大于当前返奖总金额
			 * 3.是，从购彩内部户划款都代发内部户，更新返奖流程控制标志为8和返奖垫付标志为0
			 * 4.否，查询华祥对公账户余额，并比较是否大于当期返奖总金额
			 * 5.华祥余额足够划扣，从华祥对公账户划款到代发内部户，更新返奖流程控制标志为8
			 * 		和返奖垫款标志为1和返奖垫付金额
			 * 6.华祥余额不够扣款，发短信通知补款
			 */
			case 7:
				logger.info("游戏编号["+drw.getGameId()+"]期号["+drw.getDrawId()	+"]Keno期号["+drw.getKenoId()+"]开始执行第["+flwCtl+"]步");
				//检查该期返奖资金是否已经划拨
				gdLotDrwTbls = get(GdLotDrwTblRepository.class).find(drw);
				if(gdLotDrwTbls==null||gdLotDrwTbls.size()==0){
					//TODO:不存在奖期信息
					
					
				}
				gdLotDrwTbl = gdLotDrwTbls.get(0);
				if("1".equals(gdLotDrwTbl.getXfeFlg())){
					//TODO:已划款
					String message = "银福通系统游戏编号"+drw.getGameId()+"期号"+drw.getDrawId()+"小期号"+drw.getKenoId()+"定时返奖交易第7步，该期返奖款已经划拨，定投执行程序退出，请检查！";
				}
				//TODO:查询代收内部户
				String tlrId = "EPBKBI0";
				
				
				//查询返奖总金额
				String przAmt = gdLotDrwTbl.getPrzAmt();
				logger.info("返奖总金额przAmt["+przAmt+"]");
				
				//TODO:查询代收内部户余额
				
				//TODO:判断代收内部户余额是否大于等于当期返奖总金额
				
				if("".equals("")){
					//代收内部户余额是否大于等于当期返奖总金额
					
					//从购彩内部户划款到代发内部户，
					
					
					//更新返奖流程控制标志为8，返奖垫付标志为0
					
				}else{
					//从华祥对公户划款到代发内部户，返奖垫付标志为1和返奖垫付金额
					//查询华祥对公户余额
					
					if("".equals("")){
						//余额小于返奖金额
						
						
					}
					//华祥对公账户划款都代发内部户
					
					
					//更新奖期表的返奖垫付标志为1、返奖垫付金额、返奖划拨标志
					
					
				}
				
				
				
				flwCtl++;
				drw.setFlwCtl(flwCtl+"");
				get(GdLotDrwTblRepository.class).updateFlwCtl(drw);
			case 8:
				logger.info("游戏编号["+drw.getGameId()+"]期号["+drw.getDrawId()	+"]Keno期号["+drw.getKenoId()+"]开始执行第["+flwCtl+"]步");
				//返奖资金划拨完成
				flwCtl++;
				drw.setFlwCtl(flwCtl+"");
				get(GdLotDrwTblRepository.class).updateFlwCtl(drw);
			/*开始返奖
			 * 游戏编号、期号、keno期号批量取出未返奖记录，进行返奖
			 * 返奖成功后更新对应的投注流水表和返奖记录表返奖标志
			 */
			case 9:
				logger.info("游戏编号["+drw.getGameId()+"]期号["+drw.getDrawId()	+"]Keno期号["+drw.getKenoId()+"]开始执行第["+flwCtl+"]步");
				
				flwCtl++;
				drw.setFlwCtl(flwCtl+"");
				get(GdLotDrwTblRepository.class).updateFlwCtl(drw);
			case 10:
				logger.info("游戏编号["+drw.getGameId()+"]期号["+drw.getDrawId()	+"]Keno期号["+drw.getKenoId()+"]开始执行第["+flwCtl+"]步");
				
				logger.info("游戏编号["+drw.getGameId()+"]期号["+drw.getDrawId()	+"]Keno期号["+drw.getKenoId()+"]结束执行第["+flwCtl+"]步");
			}
		}
		//TODO:返奖总金额与购彩总金额扎差清算
		
		
		
		logger.info("ReturnPlanPayStrategyActionLOTR01 end ... ...");
	}

	private void fundTransfer(Map<String,Object> param){
		a.callService("", param);
		b.callService("GDEUPS", "acpFundsTransfer", param);
	}
}
