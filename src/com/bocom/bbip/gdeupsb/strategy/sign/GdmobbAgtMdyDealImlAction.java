package com.bocom.bbip.gdeupsb.strategy.sign;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdsAgtInf;
import com.bocom.bbip.gdeupsb.entity.GdsAgtTrc;
import com.bocom.bbip.gdeupsb.entity.GdsRunCtl;
import com.bocom.bbip.gdeupsb.expand.AgtMdyDealImlService;
import com.bocom.bbip.gdeupsb.repository.GdsAgtInfRepository;
import com.bocom.bbip.gdeupsb.repository.GdsAgtTrcRepository;
import com.bocom.bbip.gdeupsb.repository.GdsAgtWaterRepository;
import com.bocom.bbip.gdeupsb.utils.CodeSwitchUtils;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 广东移动协议处理函数流程
 * 
 * @author tandun
 * 
 */
public class GdmobbAgtMdyDealImlAction implements AgtMdyDealImlService {

	@Autowired
	BBIPPublicService bbipPublicService;

	@Autowired
	GdsAgtInfRepository gdsAgtInfRepository;

	@Autowired
	GdsAgtWaterRepository gdsAgtWaterRepository;

	@Autowired
	GdsAgtTrcRepository gdsAgtTrcRepository;

	private final static Logger log = LoggerFactory.getLogger(GdmobbAgtMdyDealImlAction.class);

	@Override
	public Map<String, Object> agtDelByGdsIdService(Context context) throws CoreException {
		log.info("agtDelByGdsIdService start! ");
		log.info("开始处理广东移动业务！");

		String bnkNme = bbipPublicService.getParam("GZEUPSB", "agtBkNme");
		context.setData("bnkNam", bnkNme);

		GdsRunCtl gdsRunctl = context.getVariable(GDParamKeys.SIGN_STATION_RUN_CTL_INFO);
		String agtMtb = gdsRunctl.getAgtMtb(); // 协议主表
		String agtStb = gdsRunctl.getAgtStb(); // 协议子表

		String actNo = context.getData("actNo"); // 卡号
		String gdsBId = context.getData(GDParamKeys.SIGN_STATION_BID); // 业务类型

		String func = context.getData(GDParamKeys.SIGN_STATION_FUNC); // 操作类型
		log.info("当前操作类型 func=[" + func + "]");

		if (GDConstants.SIGN_STATION_AGT_FUNC_QUERY.equals(func)) {
			log.info("当前操作类型为2，开始进行查询!");
			log.info("start query agent info!");

			Map<String, Object> inMap = new HashMap<String, Object>();
			inMap.put("actNo", actNo); // 卡号
			inMap.put("gdsBId", gdsBId); // 业务类型
			inMap.put("AgtMTb", agtMtb); // 协议主表
			inMap.put("AgtSTb", agtStb); // 协议子表

			List<Map<String, Object>> prvDatRes = new ArrayList<Map<String, Object>>(); // 返回list
			List<Map<String, Object>> qryResult = gdsAgtWaterRepository.findSignDeatilForQry(inMap);

			// 循环组返回报文
			for (Map<String, Object> qryMap : qryResult) {
				log.info("查询返回的map为[" + qryMap + "]");
				Map<String, Object> prvDatMap = new HashMap<String, Object>();
				prvDatMap.put("SUBSTS", qryMap.get("SUB_STS"));
				prvDatMap.put("ORGCOD", qryMap.get("ORG_COD"));
				prvDatMap.put("TBUSTP", qryMap.get("TBUS_TP"));
				prvDatMap.put("TCUSID", qryMap.get("TCUS_ID"));
				prvDatMap.put("TCUSNM", qryMap.get("TCUS_NM"));
				prvDatMap.put("GDSAID", qryMap.get("GDS_AID"));
				prvDatMap.put("EFFDAT", qryMap.get("EFF_DAT"));
				prvDatMap.put("IVDDAT", qryMap.get("IVD_DAT"));
				prvDatRes.add(prvDatMap);
			}
			context.setData("prvDatRes", prvDatRes); // 返回字段
			log.info("协议维护-查询结束，当前context=" + context.getDataMap());
			context.setData("recNum", qryResult.size()); // 返回笔数

			context.setData(GDParamKeys.SIGN_STATION_OEXTFLG, GDConstants.SIGN_STATION_OEXTFLG_Y);

		} else if (GDConstants.SIGN_STATION_AGT_FUNC_UPDATE.equals(func)||GDConstants.SIGN_STATION_AGT_FUNC_INSERT.equals(func)) {
			log.info("当前操作类型為" + func + ",开始进行协议更新/新增");
			log.info("start update agent info!");

			// 查询协议主表判断是否已存在协议信息
			Map<String, Object> inpara = new HashMap<String, Object>();
			inpara.put("AgtMTb", agtMtb); // 协议主表
			inpara.put("gdsBId", gdsBId); // 业务类型
			inpara.put("actNo", actNo); // 业务类型

			List<Map<String, Object>> oldAgtInfoList = gdsAgtInfRepository.findOldAgtInfo(inpara);
			GdsAgtInf gdsAgtInf = new GdsAgtInf();
			// 若不存在原协议信息则新增一条
			if (CollectionUtils.isEmpty(oldAgtInfoList)) {
				log.info("old agent info not found!start to insert new record!");
				gdsAgtInf = BeanUtils.toObject(context.getDataMap(), GdsAgtInf.class);
				// gdsAgtInf.setGdsBid(gdsBId); //业务类型
				gdsAgtInf.setEffDat(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
				gdsAgtInf.setIvdDat("99991231");
				gdsAgtInf.setBrno((String) context.getData(ParamKeys.BR));
				gdsAgtInfRepository.insert(gdsAgtInf);
			}
			// 若存在原协议信息则更新
			else {
				log.info("存在原协议，开始更新原协议信息");
				gdsAgtInf = BeanUtils.toObject(context.getDataMap(), GdsAgtInf.class);
				// 生效日期及失效处理
				if (null == gdsAgtInf.getEffDat()) {
					Map<String, Object> oldInfo = oldAgtInfoList.get(0);
					String effDat = (String) oldInfo.get("EFFDAT");
					if (StringUtils.isEmpty(effDat)) {
						gdsAgtInf.setEffDat(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
					} else {
						gdsAgtInf.setEffDat(effDat);
					}
				}
				if (null == gdsAgtInf.getIvdDat()) {
					Map<String, Object> oldInfo = oldAgtInfoList.get(0);
					String ivdDat = (String) oldInfo.get("IVDDAT");
					if (StringUtils.isEmpty(ivdDat)) {
						gdsAgtInf.setIvdDat(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
					} else {
						gdsAgtInf.setIvdDat(ivdDat);
					}
				}

				// gdsAgtInf.setIvdDat("99991231");
				gdsAgtInf = gdsAgtInfRepository.save(gdsAgtInf);
			}

			// 协议子表处理
			deataileal(agtStb, gdsAgtInf.getIvdDat(), gdsBId, actNo, context);

			context.setData(GDParamKeys.SIGN_STATION_OEXTFLG, GDConstants.SIGN_STATION_OEXTFLG_N); // 扩展标志为"N"
			// TODO：GenPrvData

			// 将操作记录计入操作流水表
			String oprTim = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss); // 操作时间
			String chn = context.getData("chn");

			GdsAgtTrc gdsAgtTrc = new GdsAgtTrc();
			gdsAgtTrc.setOprTim(oprTim);
			gdsAgtTrc.setActDat(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
			gdsAgtTrc.setTxnCnl(chn);
			gdsAgtTrc.setTlrId((String) context.getData(ParamKeys.TELLER));
			gdsAgtTrc.setSup1Id((String) context.getData(ParamKeys.SUPER_TELLER1_ID)); // 主管授权1
			gdsAgtTrc.setSup2Id((String) context.getData(GDParamKeys.SUPER_TELLER2_ID)); // 授权2
			gdsAgtTrc.setActNo(actNo);
			gdsAgtTrc.setNodNo((String) context.getData(ParamKeys.BR)); // 机构号
			gdsAgtTrc.setBrno((String) context.getData(ParamKeys.BK)); // 分行号
			gdsAgtTrc.setTtxnCd((String) context.getProcessId()); // 接入交易码
			gdsAgtTrc.setTxnCod((String) context.getData("subProcessId")); // 处理码

			// wtrDtlList
			String orgJsonDetail = context.getData("wtrDtlList"); // 原json字段
			gdsAgtTrc.setPrvDat(orgJsonDetail);

			gdsAgtTrcRepository.save(gdsAgtTrc);

		} else if (GDConstants.SIGN_STATION_AGT_FUNC_INSERT.equals(func)) {

		} else {
			log.error("操作类型错误!");
			context.setData(GDParamKeys.SIGN_STATION_OEXTFLG, GDConstants.SIGN_STATION_OEXTFLG_N);
			context.setData("responseType", "E");
			context.setData("responseMessage", "操作选项错误");
			throw new CoreException(GDErrorCodes.EUPS_SIGN_DEAL_TYPE_ERROR); // 操作选项错误
		}
		return null;

	}

	private void deataileal(String agtStb, String ivdDat, String gdsBId, String actNo, Context context) throws CoreException {

		Map<String, Object> inparaSub = new HashMap<String, Object>();
		inparaSub.put("AgtSTb", agtStb); // 子表
		inparaSub.put("ivdDat", ivdDat); // 失效日期
		inparaSub.put("gdsBId", gdsBId); // 业务类型
		inparaSub.put("actNo", actNo); // 帐号

		// 协议子表处理 更新成无效
		try{
		gdsAgtWaterRepository.updateOldAgtInfCnl(inparaSub);
		}catch(Exception CoreException){
			
		}
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> signDetailList = (List<Map<String,Object>>) context.getData("prvDatReq");

		// 卡号限制判断
		String actTyp = context.getData("actTyp"); // 账户性质
		
		String chn = context.getData("chn");
		if(!"00".equals(chn)){
			cardBinVerify(context, actTyp, actNo);
		}

		for (int i = 0; i < signDetailList.size(); i++) {

			Map<String, Object> detailMap = signDetailList.get(i);
			detailMap.put("agtSTb", agtStb); // 子表
			detailMap.put("gdsBid", gdsBId); // 代理业务id
			detailMap.put("actNo", actNo); // 卡号
			detailMap.put("MCusId", (String) detailMap.get("MCusId"));
			
			System.out.println("~~~~~~~~~~~~~~~~~context=" + context);

			detailMap.put("txnCnl", context.getData(GDParamKeys.SIGN_STATION_TXN_CNL)); // 操作渠道

			// 从detailList中获取用户编号，验证长度是否合法，协议是否可以签订等
			String cusNo = (String) detailMap.get("TCUSID"); // 用户编号
			String tBusTyp = (String) detailMap.get("TBUSTP"); // 业务类型
			String gdsAId = (String) detailMap.get("GDSAID"); // 协议号
			
			Map<String, Object> oldAgtInMap = new HashMap<String, Object>();
			oldAgtInMap.put("agtSTb", agtStb);
			oldAgtInMap.put("cusNo", cusNo);
			oldAgtInMap.put("gdsBId", gdsBId);
			oldAgtInMap.put("actNo", actNo);
			oldAgtInMap.put("gdsAId", gdsAId);

			// 获取原协议子表信息
			List<Map<String, Object>> oldAgtList = gdsAgtWaterRepository.findOldAgtInf(oldAgtInMap);
			
			detailMap.put("BnkTyp", context.getData("bnkTyp"));
			detailMap.put("BnkNo", context.getData("bnkNo"));
			detailMap.put("OrgCod",(String) detailMap.get("ORGCOD"));
			detailMap.put("TCusId",(String) detailMap.get("TCUSID"));
			detailMap.put("TCusNm",(String) detailMap.get("TCUSNM"));
			detailMap.put("MCusNm", (String) detailMap.get("TCUSNM"));
			detailMap.put("EffDat", (String) detailMap.get("EFFDAT"));
			detailMap.put("SUBSTS", detailMap.get("SUBSTS"));
			detailMap.put("txnCnl", context.getData(GDParamKeys.SIGN_STATION_TXN_CNL));
			detailMap.put("TBusTp", tBusTyp);
			detailMap.put("bnkNam", "交通银行广东省分行");
			detailMap.put("IvdDat", (String) detailMap.get("IVDDAT"));
			detailMap.put("TAgtTp", (String) detailMap.get("TAgtTp"));
			if (CollectionUtils.isNotEmpty(oldAgtList)) {
				// 更新原有记录
				String subSts = (String) detailMap.get("SUBSTS"); // 状态
				detailMap.put("SUBSTS", subSts);
				if (GDConstants.SIGN_STATION_AGT_STATUS_0.equals(subSts)) {
					log.info("有效协议变更时需重新提交第三方验证");
					detailMap.put("lagtSt", "C");
					detailMap.put("tagtSt", "C");
				}
				gdsAgtWaterRepository.updateGdmobbOldAgtInf(detailMap);
			} else {
				// 没有原记录的话则新增
				detailMap.put("lagtSt", "U");
				detailMap.put("tagtSt", "U");
				System.out.println("+=======================detailMap=" + detailMap);
				gdsAgtWaterRepository.insertgdmobbDetailAgtInf(detailMap);//待修改
			}

		}
	}

	/**
	 * 卡号限制判断
	 * 
	 * @param context
	 * @param actNo
	 * @throws CoreException
	 */
	/**
	 * 卡号限制判断
	 * 
	 * @param context
	 * @param actNo
	 * @throws CoreException
	 */
	private void cardBinVerify(Context context, String actTyp, String actNo) throws CoreException {
		log.info("start cardBinVerify,actTyp=" + actTyp);
		if (Constants.PAY_MDE_4.equals(actTyp)) { // 卡
			String carBin = actNo.substring(0, 9);

			String cardValid = "Y";
			
			String cardVldFlg=CodeSwitchUtils.codeGenerator("cardValidTel", carBin);
			
			if (!cardValid.equals(cardVldFlg)) {
				// TODO:根据GdsBId获得对应的BusNam（业务名称），使用我待测试的codeSwitch
				String busNam = "广东移动";
				context.setData("responseType", "E");
				context.setData("responseMessage", "该卡不支持" + busNam + "签约");
				throw new CoreException(GDErrorCodes.EUPS_SIGN_CARD_NOT_SUPPORT, "该卡不支持" + busNam + "签约");
			}
		}
	}

}
