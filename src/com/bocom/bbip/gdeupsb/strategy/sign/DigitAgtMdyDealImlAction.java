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
import com.bocom.bbip.comp.CommonRequest;
import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.comp.account.support.CusActInfResult;
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
 * 珠江数码协议处理函数流程
 * 
 * @author qc.w
 * 
 */
public class DigitAgtMdyDealImlAction implements AgtMdyDealImlService {

	@Autowired
	BBIPPublicService bbipPublicService;

	@Autowired
	GdsAgtInfRepository gdsAgtInfRepository;

	@Autowired
	GdsAgtWaterRepository gdsAgtWaterRepository;

	@Autowired
	GdsAgtTrcRepository gdsAgtTrcRepository;

	@Autowired
	AccountService accountService;

	private final static Logger log = LoggerFactory.getLogger(DigitAgtMdyDealImlAction.class);

	@Override
	public Map<String, Object> agtDelByGdsIdService(Context context) throws CoreException {
		log.info("开始处理珠江数码协议维护业务！");

		String bnkNme = bbipPublicService.getParam("GDEUPSB", "agtBkNme");
		context.setData("bnkNam", bnkNme);

		GdsRunCtl gdsRunctl = context.getVariable(GDParamKeys.SIGN_STATION_RUN_CTL_INFO);
		String agtMtb = gdsRunctl.getAgtMtb(); // 协议主表
		String agtStb = gdsRunctl.getAgtStb(); // 协议子表

		String actNo = context.getData("actNo"); // 卡号
		String gdsBId = context.getData(GDParamKeys.SIGN_STATION_BID); // 业务类型

		String func = context.getData(GDParamKeys.SIGN_STATION_FUNC); // 操作类型
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
			log.info("查询返回的list=" + qryResult);
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
			context.setData("recNum", qryResult.size()); // 返回笔数
			context.setData("prvDatRes", prvDatRes); // 返回字段
			log.info("协议维护-查询结束，当前context=" + context.getDataMap());

			context.setData(GDParamKeys.SIGN_STATION_OEXTFLG, GDConstants.SIGN_STATION_OEXTFLG_Y);

		} else if (GDConstants.SIGN_STATION_AGT_FUNC_UPDATE.equals(func) || GDConstants.SIGN_STATION_AGT_FUNC_INSERT.equals(func)) {
			log.info("当前操作类型為" + func + ",开始进行协议更新/新增");
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
				gdsAgtInf.setBrno((String) context.getData("brno"));
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
				gdsAgtInf.setBrno((String) context.getData("brno"));
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

		log.info("将原子表信息作废!");
		// 协议子表处理
		gdsAgtWaterRepository.updateOldAgtInfCnl(inparaSub);

		List<Map<String, Object>> signDetailList = context.getData("prvDatReq");

		// 卡号限制判断
		String actTyp = context.getData("actTyp"); // 账户性质
		String chn = context.getData("chn");
		if (!"00".equals(chn)) {
			cardBinVerify(context, actTyp, actNo);
		}

		for (int i = 0; i < signDetailList.size(); i++) {

			Map<String, Object> detailMap = signDetailList.get(i);
			log.info("签约子表明细数据为:[" + detailMap + "]");

			// 请求字段转换
			detailMap.put("EffDat", detailMap.get("EFFDAT"));
			detailMap.put("OrgCod", detailMap.get("ORGCOD"));
			detailMap.put("SubSts", detailMap.get("SUBSTS"));
			detailMap.put("GdsAId", detailMap.get("GDSAID"));
			detailMap.put("BnkTyp", context.getVariable("BnkTyp"));
			detailMap.put("BnkNo", "");
			detailMap.put("bnkNam", "交通银行广东省分行");
			detailMap.put("TBusTp", detailMap.get("TBUSTP"));
			detailMap.put("TCusId", detailMap.get("TCUSID"));
			detailMap.put("TCusNm", detailMap.get("TCUSNM"));
			detailMap.put("IvdDat", detailMap.get("IVDDAT"));

			detailMap.put("agtSTb", agtStb); // 子表
			detailMap.put("gdsBid", gdsBId); // 代理业务id
			detailMap.put("actNo", actNo); // 卡号

			detailMap.put("txnCnl", context.getData(GDParamKeys.SIGN_STATION_TXN_CNL)); // 操作渠道

			// 从detailList中获取用户编号，验证长度是否合法，协议是否可以签订等
			String cusNo = (String) detailMap.get("TCusId"); // 用户编号
			String tBusTyp = (String) detailMap.get("TBusTp"); // 业务类型
			chechkCusInf(context, cusNo, tBusTyp, agtStb, i + 1);

			String gdsAId = (String) detailMap.get("GdsAId"); // 协议号
			Map<String, Object> oldAgtInMap = new HashMap<String, Object>();
			oldAgtInMap.put("agtSTb", agtStb);
			oldAgtInMap.put("cusNo", cusNo);
			oldAgtInMap.put("gdsBId", gdsBId);
			oldAgtInMap.put("actNo", actNo);
			oldAgtInMap.put("gdsAId", gdsAId);

			// 获取原协议子表信息
			List<Map<String, Object>> oldAgtList = gdsAgtWaterRepository.findOldAgtInf(oldAgtInMap);
			if (CollectionUtils.isNotEmpty(oldAgtList)) {
				// 更新原有记录
				String subSts = (String) detailMap.get("SubSts"); // 状态
				if (GDConstants.SIGN_STATION_AGT_STATUS_0.equals(subSts)) {
					log.info("有效协议变更时需重新提交第三方验证");
					detailMap.put("lagtSt", "C");
					detailMap.put("tagtSt", "C");
				}
				gdsAgtWaterRepository.updateOldAgtInf(detailMap);
			} else {
				// 没有原记录的话则新增
				detailMap.put("lagtSt", "U");
				detailMap.put("tagtSt", "U");

				String idNo = context.getData("idNo");
				// 如果输入了身份证则校验身份证号码与卡号是否绑定
				if (StringUtils.isNotEmpty(idNo)) {
					if(StringUtils.isEmpty((String)context.getData("batchOprFlg"))){
						boolean crdChkR = idCardCheck(context, actNo,idNo);
						if (crdChkR) {
							detailMap.put("lagtSt", "S");
						} else {
							detailMap.put("lagtSt", "F");
							detailMap.put("lerMsg", "卡号与身份证号不匹配");
							throw new CoreException(GDErrorCodes.EUPS_SIGN_CARD_CHECK_ERROR);
						}
					}
				}

				log.info("新增信息=" + detailMap);
				gdsAgtWaterRepository.insertDetailAgtInf(detailMap);
			}

		}
	}

	private boolean idCardCheck(Context context, String actNo,String idNo) throws CoreException {

		String cusAc = actNo; 
		CommonRequest commonRequest = CommonRequest.build(context);
		CusActInfResult cusActInfResult = accountService.getAcInf(commonRequest, cusAc);
		if (!cusActInfResult.isSuccess()) {
			return false;
		} else {
			String hostIdno = cusActInfResult.getIdNo();
			log.info("after qry id info,查询身份证信息，主机返回的身份证号为:[" + hostIdno + "],前台输入的身份证号为:[" + idNo + "]");

			if (!hostIdno.equals(idNo)) {
				return false;
			}
		}
		return true;
	}

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

			String cardValid = CodeSwitchUtils.codeGenerator("CardBinExc", carBin);
			if (!"Y".equals(cardValid)) {
				// TODO:根据GdsBId获得对应的BusNam（业务名称），使用我待测试的codeSwitch
				String busNam = "珠江数码";
				context.setData("responseType", "E");
				context.setData("responseMessage", "该卡不支持" + busNam + "签约");
				throw new CoreException(GDErrorCodes.EUPS_SIGN_CARD_NOT_SUPPORT, "该卡不支持" + busNam + "签约");
			}
		}
	}

	/**
	 * 检查用户编号长度是否满足，该业务种类下该用户是否已注册
	 * 
	 * @param cusNo
	 * @throws CoreException
	 */
	private void chechkCusInf(Context context, String cusNo, String tBusTyp, String agtStb, int i) throws CoreException {
		// 校验用户编号长度
		// if (cusNo.length() != 10) {
		// context.setData("responseType", "E");
		// context.setData("responseMessage", "第" + i + "条记录，用户编号" + cusNo +
		// "，长度不足10位");
		// throw new CoreException(GDErrorCodes.EUPS_SIGN_CUSNO_LENGTH_ERROR,
		// "第" + i + "条记录，用户编号" + cusNo + "，长度不足10位");
		// }
		// 校验是否存在已签约的数据
		Map<String, Object> inpara = new HashMap<String, Object>();
		inpara.put("agtSTb", agtStb);
		inpara.put("cusNo", cusNo);
		inpara.put("tBusTyp", tBusTyp);

		List<Map<String, Object>> coll = gdsAgtWaterRepository.findCusIsExist(inpara);
		if (CollectionUtils.isNotEmpty(coll)) {
			context.setData("responseType", "E");
			context.setData("responseMessage", "第" + i + "条记录，用户编号为" + cusNo + "，已签约");
			throw new CoreException(GDErrorCodes.EUPS_SIGN_AGT_EXIST, "第" + i + "条记录，用户编号为" + cusNo + "，已签约");
		}

	}

}
