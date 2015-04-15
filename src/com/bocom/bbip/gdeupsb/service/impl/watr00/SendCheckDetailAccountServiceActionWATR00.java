package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import com.bocom.bbip.utils.FileUtils;
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
		
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		logger.info("SendCheckDetailAccountServiceActionWATR00 execute start ... ...");
		String hno = context.getData("hno");
		String beginDate = context.getData("beginDate");
		String endDate = context.getData("endDate");
		logger.info("hno:["+hno+"]beginDate:["+beginDate+"]endDate:["+endDate+"]");
		context.setData("eupeBusTyp", "WATR00");
		context.setData("txnSts", "S");
		context.setData("txnTyp", "N");
		Date startDat=DateUtils.parse(context.getData("beginDate").toString());
		Date endDat=DateUtils.parse(context.getData("endDate").toString());
		context.setData("startDat", startDat);
		context.setData("endDat", endDat);
		Map<String,Object> map =((SqlMap)get("sqlMap")).queryForObject("watr00.findCheckSum", context.getDataMap());//查询总金额、总笔数
		String je = String.valueOf(map.get("TOTAMT"));
		if(je==null||"null".equals(je)){
			je = "0";
		}
		
		String count = String.valueOf( map.get("TOTCNT"));
		if(count==null||"null".equals(count)){
			count = "0";
		}
		context.setData("je", NumberUtils.yuanToCentString(je));
		context.setData("count", count);
		logger.info("je:["+je+"]count:["+count+"]");
		List<Map<String,Object>> list =((SqlMap)get("sqlMap")).queryForList("watr00.findDetailAmt", context.getDataMap());//查询明细信息
		
//		EupsTransJournal eupsTransJournal = new EupsTransJournal();
//		List<EupsTransJournal> eupsTransJournals = get(EupsTransJournalRepository.class).find(eupsTransJournal);
		
		String filename = "WATR00"+DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd)+".txt";
		File file = new File("E:\\"+filename);
		List<String>ret=new ArrayList<String>();
	
			//file.createNewFile();
			//PrintWriter writer = new PrintWriter(file);
			for(Map<String,Object> m:list){
//				String thdCusNo = String.format("%09s", m.get("THD_CUS_NO").toString());
//				String txnDte = String.format("%08s", m.get("TXN_DTE").toString().replaceAll("-", ""));
//				String txnAmt = String.format("%11s", m.get("TXN_AMT").toString().replaceAll("\\.", ""));
//				String cusAc = String.format("%40s", m.get("CUS_AC").toString());
				String str="";
				String thdCusNo =  m.get("HNO").toString();
				String txnDte = m.get("ACT_DAT").toString().replaceAll("-", "");
				String txnAmt = m.get("JE").toString().replaceAll("\\.", "");
				String cusAc =  m.get("BCOUNT").toString();
				logger.info("thdCusNo:["+thdCusNo+"]txnDte:["+txnDte+"]txnAmt:["+txnAmt+"]cusAc:["+cusAc+"]");
				/*writer.print(String.format("%9s",thdCusNo));
				writer.print(String.format("%8s",txnDte));
				writer.print(String.format("%11s",txnAmt));
				writer.print(String.format("%40s",cusAc));
				writer.print("\t");*/
				str+=String.format("%9s",thdCusNo);
				str+=(String.format("%8s",txnDte));
				str+=(String.format("%11s",txnAmt));
				str+=(String.format("%40s",cusAc));
				str+=("\t");
				ret.add(str);
			}
			
			String string="";
			string+=count;
			string+="\t";
			string+=je;
			ret.add(string);
			//writer.println();
			//writer.print(count);
			//writer.print("\t");
			//writer.print(je);
			//writer.close();
			logger.info("filename:["+file.getName()+"]");
		try {
			FileUtils.writeLines(file, ret);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
        EupsThdFtpConfig eupsThdFtpConfig = new EupsThdFtpConfig();
        eupsThdFtpConfig.setFtpNo("waterBatchFile");
        eupsThdFtpConfig = get(EupsThdFtpConfigRepository.class).find(eupsThdFtpConfig).get(0);
        eupsThdFtpConfig.setLocFleNme("E:\\"+filename);
        eupsThdFtpConfig.setRmtFleNme(filename);
        SupportFtpUtils ftp = new SupportFtpUtils();
        try {
			ftp.connect(eupsThdFtpConfig.getThdIpAdr(), Integer.parseInt(eupsThdFtpConfig.getBidPot()),
					eupsThdFtpConfig.getOppNme(), eupsThdFtpConfig.getOppUsrPsw(), false);
			ftp.upload(eupsThdFtpConfig.getRmtWay()+filename, file);
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
		
		context.setData("path", eupsThdFtpConfig.getRmtWay());
		context.setData("filename", filename);
		context.setData("filesize", file.length());
		context.setData("reserved", "");
		
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
