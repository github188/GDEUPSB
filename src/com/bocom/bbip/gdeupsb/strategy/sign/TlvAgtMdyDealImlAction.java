package com.bocom.bbip.gdeupsb.strategy.sign;

import java.io.UnsupportedEncodingException;
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
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.channel.http.support.JsonUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 广东有线协议处理函数流程
 * 
 * @author qc.w
 * 
 */
public class TlvAgtMdyDealImlAction implements AgtMdyDealImlService {

	@Autowired
	BBIPPublicService bbipPublicService;

	@Autowired
	GdsAgtInfRepository gdsAgtInfRepository;

	@Autowired
	GdsAgtWaterRepository gdsAgtWaterRepository;

	@Autowired
	GdsAgtTrcRepository gdsAgtTrcRepository;

	private final static Logger log = LoggerFactory.getLogger(TlvAgtMdyDealImlAction.class);

	@Override
	public Map<String, Object> agtDelByGdsIdService(Context context) throws CoreException {
		log.info("agtDelByGdsIdService start!");
		String bnkNme = bbipPublicService.getParam("GZEUPSB", "agtBkNme");
		context.setData("bnkNam", bnkNme);

		GdsRunCtl gdsRunctl = context.getVariable(GDParamKeys.SIGN_STATION_RUN_CTL_INFO);
		String agtMtb = gdsRunctl.getAgtMtb(); // 协议主表
		String agtStb = gdsRunctl.getAgtStb(); // 协议子表

		String actNo = context.getData("actNo"); // 卡号
		String gdsBId = context.getData(GDParamKeys.SIGN_STATION_BID); // 业务类型

		String func = context.getData(GDParamKeys.SIGN_STATION_FUNC); // 操作类型
		if (GDConstants.SIGN_STATION_AGT_FUNC_QUERY.equals(func)) {
			log.info("start query agent info!");

			Map<String, Object> inMap = new HashMap<String, Object>();
			inMap.put("actNo", actNo); // 卡号
			inMap.put("gdsBId", gdsBId); // 业务类型
			inMap.put("AgtMTb", agtMtb); // 协议主表
			inMap.put("AgtSTb", agtStb); // 协议子表

			List<Map<String, Object>> qryResult = gdsAgtWaterRepository.findSignDeatilForQry(inMap);
			context.setData(GDParamKeys.SIGN_STATION_AGT_QRY_RESULT, qryResult); // 返回信息

			// prvDatRes
			Map<String, Object> jsonMap = new HashMap<String, Object>();
			jsonMap.put("showInf", qryResult);

			try {
				context.setData("prvDatRes", new String(JsonUtils.jsonFromObject(jsonMap, "UTF8"), "UTF8"));
			} catch (UnsupportedEncodingException e) {
				throw new CoreException(GDErrorCodes.EUPS_SIGN_DEFAULT_ERROR); // 返回json有问题
			}

			context.setData(GDParamKeys.SIGN_STATION_OEXTFLG, GDConstants.SIGN_STATION_OEXTFLG_Y);

		} else if (GDConstants.SIGN_STATION_AGT_FUNC_UPDATE.equals(func)) {
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
			log.error("handle type error!");

			context.setData(GDParamKeys.SIGN_STATION_OEXTFLG, GDConstants.SIGN_STATION_OEXTFLG_N);
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

		// 协议子表处理
		gdsAgtWaterRepository.updateOldAgtInfCnl(inparaSub);

		// TODO:json转换
		String jsonMsg = context.getData("prvDatReq");
		log.info("==============jsonMsg:" + jsonMsg);
		// 去掉json中的[],否则JsonUtils无法解析
		// if(jsonMsg.contains("[")&&jsonMsg.contains("]")){
		// jsonMsg=jsonMsg.replace("[", "").replace("]", "");
		// }
		if (jsonMsg.length() != 0) {
			context.setDataMap(JsonUtils.objectFromJson(jsonMsg, Map.class));
		}

		List<Map<String, Object>> signDetailList = context.getData("InRec");

		// 卡号限制判断
		String actTyp = context.getData("actTyp"); // 账户性质
		cardBinVerify(actTyp, actNo);

		for (int i = 0; i < signDetailList.size(); i++) {

			Map<String, Object> detailMap = signDetailList.get(i);
			detailMap.put("agtSTb", agtStb); // 子表
			detailMap.put("gdsBid", gdsBId); // 代理业务id
			detailMap.put("actNo", actNo); // 卡号
			System.out.println("~~~~~~~~~~~~~~~~~context=" + context);

			detailMap.put("txnCnl", context.getData(GDParamKeys.SIGN_STATION_TXN_CNL)); // 操作渠道

			// 从detailList中获取用户编号，验证长度是否合法，协议是否可以签订等
			String cusNo = (String) detailMap.get("TCusId"); // 用户编号
			String tBusTyp = (String) detailMap.get("TBusTp"); // 业务类型

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

				System.out.println("+=======================detailMap=" + detailMap);
				gdsAgtWaterRepository.insertDetailAgtInf(detailMap);
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
	private void cardBinVerify(String actTyp, String actNo) throws CoreException {
		log.info("start cardBinVerify,actTyp=" + actTyp);
		if (Constants.PAY_MDE_4.equals(actTyp)) { // 卡
			String carBin = actNo.substring(0, 9);

			String cardValid = "Y";
			// TODO:判断cardValid，使用我待测试的codeSwitch
			// <Item CardBin="622261071" CardValid="Y"/>
			// <Item CardBin="622260071" CardValid="Y"/>
			// <Item CardBin="622259071" CardValid="Y"/>
			// <Item CardBin="622258071" CardValid="Y"/>
			// <Item CardBin="622262071" CardValid="Y"/>
			// <Item CardBin="622261571" CardValid="Y"/>
			// <Item CardBin="622260571" CardValid="Y"/>
			// <Item CardBin="622259571" CardValid="Y"/>
			// <Item CardBin="622258571" CardValid="Y"/>
			// <Item CardBin="622262571" CardValid="Y"/>
			// <Item CardBin="622261073" CardValid="Y"/>
			// <Item CardBin="622260073" CardValid="Y"/>
			// <Item CardBin="622259073" CardValid="Y"/>
			// <Item CardBin="622258073" CardValid="Y"/>
			// <Item CardBin="622262073" CardValid="Y"/>
			// <Item CardBin="622261074" CardValid="Y"/>
			// <Item CardBin="622260074" CardValid="Y"/>
			// <Item CardBin="622259074" CardValid="Y"/>
			// <Item CardBin="622258074" CardValid="Y"/>
			// <Item CardBin="622262074" CardValid="Y"/>
			// <Item CardBin="622261075" CardValid="Y"/>
			// <Item CardBin="622260075" CardValid="Y"/>
			// <Item CardBin="622259075" CardValid="Y"/>
			// <Item CardBin="622258075" CardValid="Y"/>
			// <Item CardBin="622262075" CardValid="Y"/>
			// <Item CardBin="622261078" CardValid="Y"/>
			// <Item CardBin="622260078" CardValid="Y"/>
			// <Item CardBin="622259078" CardValid="Y"/>
			// <Item CardBin="622258078" CardValid="Y"/>
			// <Item CardBin="622262078" CardValid="Y"/>
			// <Item CardBin="622261371" CardValid="Y"/>
			// <Item CardBin="622260371" CardValid="Y"/>
			// <Item CardBin="622259371" CardValid="Y"/>
			// <Item CardBin="622258371" CardValid="Y"/>
			// <Item CardBin="622262371" CardValid="Y"/>
			// <Item CardBin="622261373" CardValid="Y"/>
			// <Item CardBin="622260373" CardValid="Y"/>
			// <Item CardBin="622259373" CardValid="Y"/>
			// <Item CardBin="622258373" CardValid="Y"/>
			// <Item CardBin="622262373" CardValid="Y"/>
			// <Item CardBin="622261491" CardValid="Y"/>
			// <Item CardBin="622260491" CardValid="Y"/>
			// <Item CardBin="622259491" CardValid="Y"/>
			// <Item CardBin="622258491" CardValid="Y"/>
			// <Item CardBin="622262491" CardValid="Y"/>
			// <Item CardBin="622261761" CardValid="Y"/>
			// <Item CardBin="622260761" CardValid="Y"/>
			// <Item CardBin="622259761" CardValid="Y"/>
			// <Item CardBin="622258761" CardValid="Y"/>
			// <Item CardBin="622262761" CardValid="Y"/>
			// <Item CardBin="622261448" CardValid="Y"/>
			// <Item CardBin="622260448" CardValid="Y"/>
			// <Item CardBin="622259448" CardValid="Y"/>
			// <Item CardBin="622258448" CardValid="Y"/>
			// <Item CardBin="622262448" CardValid="Y"/>
			// <Item CardBin="622261493" CardValid="Y"/>
			// <Item CardBin="622260493" CardValid="Y"/>
			// <Item CardBin="622259493" CardValid="Y"/>
			// <Item CardBin="622258493" CardValid="Y"/>
			// <Item CardBin="622262493" CardValid="Y"/>
			// <Item CardBin="622261492" CardValid="Y"/>
			// <Item CardBin="622260492" CardValid="Y"/>
			// <Item CardBin="622259492" CardValid="Y"/>
			// <Item CardBin="622258492" CardValid="Y"/>
			// <Item CardBin="622262492" CardValid="Y"/>
			// <Item CardBin="622261495" CardValid="Y"/>
			// <Item CardBin="622260495" CardValid="Y"/>
			// <Item CardBin="622259495" CardValid="Y"/>
			// <Item CardBin="622258495" CardValid="Y"/>
			// <Item CardBin="622262495" CardValid="Y"/>

			if (!"Y".equals(cardValid)) {
				// TODO:根据GdsBId获得对应的BusNam（业务名称），使用我待测试的codeSwitch
				String busNam = "有线电视";
				throw new CoreException(GDErrorCodes.EUPS_SIGN_CARD_NOT_SUPPORT, "该卡不支持" + busNam + "签约");
			}
		}
	}

}
