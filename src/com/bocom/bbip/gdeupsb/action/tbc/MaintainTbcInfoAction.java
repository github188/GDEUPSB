package com.bocom.bbip.gdeupsb.action.tbc;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdBaseInfo;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.euif.component.util.StringUtil;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 广东烟草公司信息维护
 * 
 * @version 1.0.0 DateTime 2015-01-16
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
            EupsThdBaseInfo eupsThdBaseInfo = get(EupsThdBaseInfoRepository.class).findOne(context.getData(ParamKeys.COMPANY_NO).toString());
            if (null == eupsThdBaseInfo) { // --检测单位是否存在--
                EupsThdBaseInfo eupsThdBase = new EupsThdBaseInfo();
                eupsThdBase.setComNo(context.getData(ParamKeys.COMPANY_NO).toString());
                eupsThdBase.setComNme(context.getData(ParamKeys.COMPANY_NAME).toString());
                eupsThdBase.setBusKnd("01");
                eupsThdBase.setEupsBusTyp("GDTBC");
                eupsThdBase.setCmuMde("TCP");
                eupsThdBase.setPkgLenTyp("0");
                eupsThdBase.setPkgLenPosLen(0);
                eupsThdBase.setThdIpAdr("127.0.0.1"); // TODO;
                eupsThdBase.setBidPot("9997"); // TODO;
                eupsThdBase.setUseSts("0");
                get(EupsThdBaseInfoRepository.class).insert(eupsThdBase);
            } else {
                context.setData(ParamKeys.COMPANY_NO, eupsThdBaseInfo.getComNo());
                context.setData(ParamKeys.RSP_CDE, GDConstants.TBC_RSP_COD);
                context.setData(ParamKeys.RSP_MSG, "该单位已存在 !");
                return;
            }
            break;
        case 1: // <!--multiquery-->
            List <EupsThdBaseInfo> thdBaseInfos = get(EupsThdBaseInfoRepository.class).findAll();
            List<Map<String,Object>> resultList=(List<Map<String, Object>>) BeanUtils.toMaps(thdBaseInfos);
            context.setData("rec", resultList);
            break;
        case 2: // <!--delete-->
            EupsThdBaseInfo NewEupsThdBaseInfo = get(EupsThdBaseInfoRepository.class).findOne(context
                    .getData(ParamKeys.COMPANY_NO).toString());
            if (StringUtil.isEmptyOrNull(NewEupsThdBaseInfo.getComNo())) { // --检测单位是否存在--
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
