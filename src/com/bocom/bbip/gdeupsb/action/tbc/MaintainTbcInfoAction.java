package com.bocom.bbip.gdeupsb.action.tbc;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdTbcBasInf;
import com.bocom.bbip.gdeupsb.repository.GdTbcBasInfRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 广东烟草公司信息维护 估计要删除  //TODO
 * @version 1.0.0 
 * DateTime 2015-01-16
 * @author GuiLin.Li
 * 
 */
public class MaintainTbcInfoAction extends BaseAction {
    @Override
    public void execute(Context context) throws CoreException {
        log.info("MaintainTbcInfo Action start!");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);

        int transactionFig = Integer.parseInt(context.getData(GDParamKeys.TBC_TXNFLG).toString());
        switch (transactionFig) {
        case 0: // insert--检测单位是否存在
            GdTbcBasInf tbcBaseInfo = get(GdTbcBasInfRepository.class).findOne(context.getData("dptId").toString());
            if (null == tbcBaseInfo) { 
                GdTbcBasInf eupsThdBase = new GdTbcBasInf();
                eupsThdBase.setDptId(context.getData("dptId").toString());
                eupsThdBase.setDptNam(context.getData(ParamKeys.COMPANY_NAME).toString());
                eupsThdBase.setBrNo(context.getData(ParamKeys.BK).toString());
                eupsThdBase.setDevId("127.0.0.1"); // TODO;
                eupsThdBase.setTeller("4411417"); // TODO;
                eupsThdBase.setComKey(context.getData("dptId").toString());
                eupsThdBase.setSigSts(Constants.TXN_CTL_STS_SIGNIN);
                eupsThdBase.setTranDt(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
                get(GdTbcBasInfRepository.class).insert(eupsThdBase);
            } else {
                context.setData(ParamKeys.COMPANY_NO, tbcBaseInfo.getDptId());
                context.setData(ParamKeys.RSP_CDE, GDConstants.TBC_RSP_COD);
                context.setData(ParamKeys.RSP_MSG, "该单位已存在 !");
                return;
            }
            break;
        case 1: // multiquery
            List <GdTbcBasInf> thdBaseInfos = get(GdTbcBasInfRepository.class).findAll();
            List<Map<String, Object>> qryResultList = new ArrayList<Map<String,Object>>();
                for(GdTbcBasInf thdBaseInfo:thdBaseInfos){
                Map<String, Object> tempMap = new HashMap<String, Object>();
                tempMap.put("dptId", thdBaseInfo.getDptId());
                tempMap.put("comNme", context.getData(ParamKeys.COMPANY_NAME).toString());
                qryResultList.add(tempMap);
            }
            context.setData("rec", qryResultList);
            break;
        case 2: // delete
            GdTbcBasInf thdBaseInfo = get(GdTbcBasInfRepository.class).findOne(context
                    .getData("dptId").toString());
            if (null == thdBaseInfo) { // --检测单位是否存在--
                context.setData(ParamKeys.RSP_CDE, GDConstants.TBC_RSP_COD);
                context.setData(ParamKeys.RSP_MSG, "该单位不存在 !");
                return;
            } else {
                get(EupsThdBaseInfoRepository.class).delete(context.getData(ParamKeys.COMPANY_NO).toString());
            }
            break;
        }
        context.setData(ParamKeys.RSP_CDE, Constants.RESPONSE_CODE_SUCC);
        context.setData(ParamKeys.RSP_MSG, Constants.RESPONSE_MSG);
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }
}
