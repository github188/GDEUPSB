package com.bocom.bbip.gdeupsb.action.efek;


import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class TallyCancelQryOnlineAction extends BaseAction{
	@Autowired
	EupsTransJournalRepository eupsTransJournalRepository;
	/**
	 *抹账信息查询 
	 */
		public void execute(Context context)throws CoreException,CoreRuntimeException{
				log.info("~~~~~~~~~~Start TallyCancelQryOnlineAction");
				context.setData(GDParamKeys.TOTNUM,"1");
				String mfmVchNo=context.getData(ParamKeys.MFM_VCH_NO).toString();
				EupsTransJournal eupsTransJournals=new EupsTransJournal();
				eupsTransJournals.setMfmVchNo(mfmVchNo);
				eupsTransJournals.setEupsBusTyp("ELEC00");
				eupsTransJournals.setTxnDte(DateUtils.parse(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd),DateUtils.STYLE_yyyyMMdd));
				List<EupsTransJournal> list=eupsTransJournalRepository.find(eupsTransJournals);
				if(CollectionUtils.isEmpty(list)){
						throw new CoreException("该交易不是当日交易，或交易不存在");
				}
				EupsTransJournal eupsTransJournal=list.get(0);
				if(null != eupsTransJournal){
						if(eupsTransJournal.getTxnSts().equals("C")){
								throw new CoreException("该交易以抹账");
						}
						context.setData(ParamKeys.TRADE_TXN_DIR, "C");//交易方向
						//本次交易日期时间
						Date date=new Date();
						context.setData(ParamKeys.TXN_DTE, DateUtils.format(date,DateUtils.STYLE_yyyyMMdd));
						context.setData(ParamKeys.TXN_TME, DateUtils.format(date,DateUtils.STYLE_HHmmss));
						//原交易流水日期时间
						context.setData(ParamKeys.OLD_TXN_SQN, eupsTransJournal.getSqn());
						Date thdTxnDate=eupsTransJournal.getTxnDte();
						context.setData(ParamKeys.THD_TXN_DATE, DateUtils.format(thdTxnDate,DateUtils.STYLE_yyyyMMdd));
						Date thdTxnTime=eupsTransJournal.getTxnTme();
						context.setData(ParamKeys.THD_TXN_TIME, DateUtils.format(thdTxnTime,DateUtils.STYLE_HHmmss));
						context.setData(GDParamKeys.PAY_NO, eupsTransJournal.getThdCusNo());
						context.setData(ParamKeys.PAYFEE_TYPE, eupsTransJournal.getPfeTyp());
						context.setData(ParamKeys.COMPANY_NO, eupsTransJournal.getComNo());
						context.setData(ParamKeys.ORDER_REG_AC, eupsTransJournal.getCusAc());
						context.setData("CusNme", eupsTransJournal.getCusNme());
						context.setData(ParamKeys.FULL_DED_FLAG,eupsTransJournal.getFulDedFlg() );
						context.setData(ParamKeys.CCY, eupsTransJournal.getCcy());
						context.setData(ParamKeys.TXN_AMT, eupsTransJournal.getTxnAmt());
						context.setData(ParamKeys.CUS_AC, eupsTransJournal.getCusAc());
						context.setData(ParamKeys.BANK_NO, eupsTransJournal.getThdObkCde());
						context.setData(GDParamKeys.BUS_TYPE, eupsTransJournal.getRsvFld4());
						context.setData(GDParamKeys.PAY_TYPE, eupsTransJournal.getRsvFld5());
						context.setData(GDParamKeys.ELECTRICITY_YEARMONTH, eupsTransJournal.getRsvFld6());
						context.setData(GDParamKeys.ACCOUNTS_SERIAL_NO, eupsTransJournal.getRsvFld3());
				}else{
					log.error("~~~~~~~~~~没有记录");
					throw new CoreException("没有记录");
				}
		}
}
