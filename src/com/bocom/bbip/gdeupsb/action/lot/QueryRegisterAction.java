package com.bocom.bbip.gdeupsb.action.lot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdLotCusInf;
import com.bocom.bbip.gdeupsb.repository.GdLotCusInfRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 福彩用户注册查询  485406
 * @version 1.0.0
 * Date 2015-01-28
 * @author GuiLin.Li
 */
public class QueryRegisterAction  extends BaseAction{

    @Override
    public void execute (Context context) throws CoreException {
        log.info("==》》》》》》QueryRegisterAction Start !!==》》》》》》");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        
        GdLotCusInf lotCusInf = new GdLotCusInf();
        lotCusInf.setCrdNo(context.getData("crdNo").toString());
        lotCusInf.setStatus("1");
        
        List<GdLotCusInf> lotCusInfs =get(GdLotCusInfRepository.class).find(lotCusInf);
        if (CollectionUtils.isEmpty(lotCusInfs)) {
            log.info("卡号:"+context.getData("crdNo").toString()+"没注册 !");
            throw new CoreException(GDErrorCodes.EUPS_LOT_CAR_NOT_REG);

        }
        
        //向福彩中心发送彩民信息查询
        context.setData("action", "209");
        context.setData(ParamKeys.EUPS_BUSS_TYPE, "LOTR01");
        context.setData("version", "");
        context.setData("sent_time", DateUtils.format(new Date(), DateUtils.STYLE_FULL));
        context.setData("dealId", "141");
        context.setData("gambler_name", lotCusInfs.get(0).getLotNam());
        context.setData("gambler_pwd", lotCusInfs.get(0).getLotPsw());
        Date regTime= DateUtils.parse(lotCusInfs.get(0).getRegTim(), DateUtils.STYLE_yyyyMMddHHmmss);
        context.setData("modify_time",DateUtils.format(regTime, DateUtils.STYLE_FULL) );

        System.out.println(">>>>>>>>>>><><><><><>"+context.getData("modify_time").toString());
        // 向福彩中心发送请求
        Map<String,Object> resultMap = new HashMap<String, Object>();
        resultMap = get(ThirdPartyAdaptor.class).trade(context);
        String responseCode = resultMap.get(GDParamKeys.LOT_RESULT_CODE).toString();
        if (BPState.isBPStateOvertime(context)) {
            throw new CoreException(ErrorCodes.TRANSACTION_ERROR_TIMEOUT);
        } else if (!"0".equals(responseCode)) {
            if (StringUtils.isEmpty(responseCode)) {
                throw new CoreException(GDErrorCodes.EUPS_THD_SYS_ERROR);
            }
            log.info("Query User Info Fail!");
            throw new CoreException(GDErrorCodes.EUPS_LOT_QRY_CUSINFO_FAIL);
        }
        context.setData("cusNam",lotCusInfs.get(0).getCusNam());
        context.setData("idTyp",lotCusInfs.get(0).getIdTyp());
        context.setData("idNo",lotCusInfs.get(0).getIdNo());
        context.setData("mobTel",lotCusInfs.get(0).getMobTel());
        context.setData("lotNam",lotCusInfs.get(0).getLotNam());

        context.setData("MsgTyp",Constants.RESPONSE_TYPE_SUCC);
        context.setData(ParamKeys.RSP_CDE,Constants.RESPONSE_CODE_SUCC);
        context.setData(ParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);

    } 
}