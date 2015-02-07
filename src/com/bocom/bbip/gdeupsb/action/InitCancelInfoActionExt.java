
package com.bocom.bbip.gdeupsb.action;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.eups.vo.AccountInfo;
import com.bocom.bbip.eups.vo.SimplePaymentDomain;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class InitCancelInfoActionExt extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(InitCancelInfoActionExt.class);

    public void execute(Context context) throws CoreException, CoreRuntimeException {
        logger.info("==========Initial Cancel Info begin===========================");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);

        EupsTransJournal updOldTxnJnl = context.getData(ParamKeys.CONSOLE_LCLJNL_LIST);
        
        System.out.println(updOldTxnJnl.getSqn());
        
        String br = (String) context.getData(ParamKeys.BR);
        if (StringUtils.isEmpty(br)) {
            logger.info("=====================errorcode[" + ErrorCodes.EUPS_FIELD_EMPTY
                + "],errormessage is [the transaction organization is blank!]=====================");
            throw new CoreException(ErrorCodes.EUPS_FIELD_EMPTY);
        } else {
            context.setData(ParamKeys.ORG_CDE, br);
            context.setData(ParamKeys.TXN_ORG_CDE, br);
        }
        // 交易流水表扩展标志初始化为不扩展
        context.setData(ParamKeys.FIL_FLG, Constants.TXNJNL_FIL_FLAG_NOT_EXTENDED);
        // 交易流水完整性类型：9-交易撤销
        context.setData(ParamKeys.ITG_TYP, Constants.ITG_FLG_CANCEL);
        // 打印标志默认为：0-未打印
        // context.setData(ParamKeys.PRT_FLG, Constants.IVO_PRT_FLG_N);

        // 交易流水表收付标志默认为：0-代收
        if (StringUtils.isEmpty((String) context.getData(ParamKeys.BUS_TYP))) {
            context.setData(ParamKeys.RAP_TYPE, Constants.TXN_RAP_TYPE_COLLECTION);
        }else{
        	context.setData(ParamKeys.RAP_TYPE,context.getData(ParamKeys.BUS_TYP));
        }
        // 交易流水表交易类型默认为：N-正常交易---前序处理时设置，如未设置，默认为正常交易
        if (StringUtils.isEmpty((String) context.getData(ParamKeys.TXN_TYP))) {
            context.setData(ParamKeys.TXN_TYP, Constants.EUPS_TXN_TYP_CANCEL);
        }
        // 交易流水表币种默认为：CNY-人民币
        if (StringUtils.isEmpty((String) context.getData(ParamKeys.CCY))) {
            context.setData(ParamKeys.CCY, Constants.CCY_CDE_CNY);
        }
        logger.info("===============the transaction directory is [" + context.getData(ParamKeys.TRADE_TXN_DIR) + "]=================");
        if (Constants.TRADE_TXN_DIR_ONLINE.equals(context.getData(ParamKeys.TRADE_TXN_DIR))) {
            // 记账交易域字段处理
            logger.info("==============prepare the field for account========");
            AccountInfo ac = new AccountInfo();
            //修改客户账号取不到得情况
//            String cusAc = context.getData(ParamKeys.CUS_AC);
            String cusAc =updOldTxnJnl.getCusAc();
            if (StringUtils.isNotEmpty(cusAc)) {
                ac.setAccountNo(cusAc.trim());
            }
            ac.setCcyCode(context.getData(ParamKeys.CCY).toString().trim());
            // 缴费方式为必输，在此不需再次验证是否非空
            //修改缴费类型
//            ac.setAccountType(context.getData(ParamKeys.ACC_TYPE).toString().trim());
            ac.setAccountType(updOldTxnJnl.getAccTyp().toString().trim());

            SimplePaymentDomain spd = new SimplePaymentDomain();
            spd.setAccount(ac);
            spd.setChannelType((String) context.getData(ParamKeys.CHL_TYP));
            spd.setSourceJournalNo((String) context.getData(ParamKeys.OLD_TXN_SEQUENCE));
            spd.setTellerId((String) context.getData(ParamKeys.TXN_TLR));
            spd.setTerminalNo((String) context.getData(ParamKeys.TERMINAL));
            spd.setCorporAgentNo((String) context.getData(ParamKeys.COMPANY_NO));
            // 金额转换Long型？
            Double lamt = 0.00;
            //TODO 
//            if (null != context.getData(ParamKeys.TXN_AMOUNT)) {
            if (null != updOldTxnJnl.getTxnAmt()) {
                if (StringUtils.isNotBlank(updOldTxnJnl.getTxnAmt().toString())) {
                    lamt = Double.parseDouble(updOldTxnJnl.getTxnAmt().toString());
                }
            }
            lamt = lamt * 100;
            spd.setTransAmount(lamt.longValue());
            //TODO 修改时间
//            spd.setTransTime(DateUtils.parse(context.getData(ParamKeys.TXN_TIME).toString(), DateUtils.STYLE_FULL));
            spd.setTransTime(DateUtils.parse(DateUtils.format(updOldTxnJnl.getTxnTme(),DateUtils.STYLE_FULL), DateUtils.STYLE_FULL));

            context.setVariable(ParamKeys.AGENTCP_BIZ_DATA, spd);
        }
        logger.info("==============Insert upfront status transaction journal========");

        // 预计交易流水，交易状态：S-成功;F-失败;R-冲正;X-发送失败;T-发送超时;C-抹账;D-交易撤销;E-其他错误;U-预计
        context.setData(ParamKeys.TXN_STS, Constants.TXNSTS_UPFRONT);
        context.setData(ParamKeys.MFM_TXN_STS, Constants.TXNSTS_UPFRONT);
        context.setData(ParamKeys.THD_TXN_STS, Constants.TXNSTS_UPFRONT);
        //增加流水必输字段
        context.setData(ParamKeys.TXN_AMT, updOldTxnJnl.getTxnAmt());
        //TODO 修改过 
        context.setData(ParamKeys.TXN_TME, DateUtils.parse(DateUtils.formatAsTranstime(new Date())));
        EupsTransJournal eupsTransJournal = BeanUtils.toObject(context.getDataMap(), EupsTransJournal.class);
        eupsTransJournal.setSqn((String)context.getData(ParamKeys.SEQUENCE));     
        get(EupsTransJournalRepository.class).insert(eupsTransJournal);
        
        context.setData(ParamKeys.OLD_TXN_STS, updOldTxnJnl.getTxnSts());
        context.setData(ParamKeys.OLD_MFM_TXN_STS, updOldTxnJnl.getMfmTxnSts());
        context.setData(ParamKeys.OLD_THD_TXN_STS, updOldTxnJnl.getThdTxnSts());

        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
        log.info("InitCancelInfoAction end! context="+context.getDataMap());
    }

}
