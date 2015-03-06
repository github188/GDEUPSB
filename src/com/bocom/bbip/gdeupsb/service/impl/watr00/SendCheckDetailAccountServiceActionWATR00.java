package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
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
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsCheckControl;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsCheckControlRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.eups.utils.SupportFtpUtils;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.euif.component.util.StringUtil;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.service.id.seq.StepSequenceFactory;
import com.bocom.jump.bp.service.sqlmap.SqlMap;
/**
 * 银行发送明细对账文件
 * @author hefengwen
 *
 */
public class SendCheckDetailAccountServiceActionWATR00 extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(SendCheckDetailAccountServiceActionWATR00.class);

	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;
	
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		logger.info("SendCheckDetailAccountServiceActionWATR00 execute start ... ...");
		String hno = context.getData("hno");
		String beginDate = context.getData("beginDate");
		String endDate = context.getData("endDate");
		logger.info("hno:["+hno+"]beginDate:["+beginDate+"]endDate:["+endDate+"]");
		context.setData("eupeBusTyp", "WATR00");
		context.setData("txnSts", "S");
		context.setData("txnTyp", "N");
		
		Map<String,Object> map =((SqlMap)get("sqlMap")).queryForObject("watr00.findCountAmt", context.getDataMap());//查询总金额、总笔数
		String je = String.valueOf(map.get("JE"));
		if(je==null||"null".equals(je)){
			je = "0";
		}
		String count = String.valueOf( map.get("COUNT"));
		context.setData("je", NumberUtils.yuanToCentString(je));
		context.setData("count", count);
		logger.info("je:["+je+"]count:["+count+"]");
		List<Map<String,Object>> list =((SqlMap)get("sqlMap")).queryForList("watr00.findDetailAmt", context.getDataMap());//查询明细信息
		
//		EupsTransJournal eupsTransJournal = new EupsTransJournal();
//		List<EupsTransJournal> eupsTransJournals = get(EupsTransJournalRepository.class).find(eupsTransJournal);
		
		String filename = "WATR00"+DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd)+".txt";
		File file = new File(filename);
		
		try {
			file.createNewFile();
			PrintWriter writer = new PrintWriter(file);
			for(Map<String,Object> m:list){
//				String thdCusNo = String.format("%09s", m.get("THD_CUS_NO").toString());
//				String txnDte = String.format("%08s", m.get("TXN_DTE").toString().replaceAll("-", ""));
//				String txnAmt = String.format("%11s", m.get("TXN_AMT").toString().replaceAll("\\.", ""));
//				String cusAc = String.format("%40s", m.get("CUS_AC").toString());
				
				String thdCusNo =  m.get("THD_CUS_NO").toString();
				String txnDte = m.get("TXN_DTE").toString().replaceAll("-", "");
				String txnAmt = m.get("TXN_AMT").toString().replaceAll("\\.", "");
				String cusAc =  m.get("CUS_AC").toString();
				logger.info("thdCusNo:["+thdCusNo+"]txnDte:["+txnDte+"]txnAmt:["+txnAmt+"]cusAc:["+cusAc+"]");
				writer.print(String.format("%9s",thdCusNo));
				writer.print(String.format("%8s",txnDte));
				writer.print(String.format("%11s",txnAmt));
				writer.print(String.format("%40s",cusAc));
				writer.print("\t");
			}
			writer.println();
			writer.print(count);
			writer.print("\t");
			writer.print(je);
			writer.close();
			logger.info("filename:["+file.getName()+"]");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//TODO:获取FTP信息，修改对账文件存放目录
		EupsCheckControl eupsCheckControl = BeanUtils.toObject(context.getDataMap(), EupsCheckControl.class);
		List<EupsCheckControl> eupsCheckControls = get(EupsCheckControlRepository.class).find(eupsCheckControl);
		if(eupsCheckControls==null||eupsCheckControls.size()==0){
			logger.error("对账控制信息不存在");
			throw new CoreException(ErrorCodes.EUPS_CHECK_CONTROL_INFO_NOTEXIST);
		}
		String processId = ((EupsCheckControl)eupsCheckControls.get(0)).getChkPro().trim();
		if(processId==null||processId.length()==0){
			logger.error("必输项没有输入:数据域{processId}");
			throw new CoreException(ErrorCodes.EUPS_FIELD_EMPTY);
		}
		log.info((new StringBuilder("process:")).append(processId).toString());
		String ftpNo = ((EupsCheckControl)eupsCheckControls.get(0)).getFtpNo();
        context.setData("ftpNo", ftpNo);
        EupsThdFtpConfig eupsThdFtpConfig = new EupsThdFtpConfig();
        eupsThdFtpConfig.setFtpNo(ftpNo);
        eupsThdFtpConfig = get(EupsThdFtpConfigRepository.class).find(eupsThdFtpConfig).get(0);
		
        SupportFtpUtils ftp = new SupportFtpUtils();
        try {
			ftp.connect(eupsThdFtpConfig.getThdIpAdr(), Integer.parseInt(eupsThdFtpConfig.getBidPot()),
					eupsThdFtpConfig.getOppNme(), eupsThdFtpConfig.getOppUsrPsw(), false);
			ftp.upload("/home/eups/"+filename, file);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		context.setData("type", "Y014");
		context.setData("accountdate", DateUtils.format((Date)context.getData(ParamKeys.AC_DATE), DateUtils.STYLE_yyyyMMdd));
		
		StepSequenceFactory s = context.getService("logNoService");
		String logNo = s.create().toString();
		context.setData("waterno", "JH"+logNo);//流水号生成
		
		context.setData("bankcode", "JT");
		context.setData("salesdepart",((String)context.getData(ParamKeys.BR)).substring(2, 8));
		context.setData("salesperson", ((String)context.getData(ParamKeys.TELLER)).substring(4, 7));
		context.setData("busitime", DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMddHHmmss));
		context.setData("thdRspCde", "0000");
		context.setData("zprice", "");
		context.setData("months", "");
		context.setData("operano", "");
		context.setData("password", "        ");
		context.setData("md5digest", " ");
		
		Map<String,Object> thdReturnMessage = callThdTradeManager.trade(context);
		
		if(context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)){
			CommThdRspCdeAction cRspCdeAction = new CommThdRspCdeAction();
			String responseCode = cRspCdeAction.getThdRspCde(thdReturnMessage, 	context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
			logger.info("responseCode:["+responseCode+"]");
			if(Constants.RESPONSE_CODE_SUCC.equals(responseCode)){
				logger.info("SendCheckDetailAccountServiceActionWATR00 success!");
				
			}else{
				if(StringUtil.isEmpty(responseCode)){
					responseCode = ErrorCodes.EUPS_THD_RSP_CODE_ERROR;
				}
				throw new CoreException(responseCode);
			}
		}else{
			logger.error("SendCheckDetailAccountServiceActionWATR00 return has error!");
			throw new CoreException(ErrorCodes.EUPS_THD_SYS_ERROR);
		}
		
		logger.info("SendCheckDetailAccountServiceActionWATR00 execute end ... ...");
	}
	
	

}
